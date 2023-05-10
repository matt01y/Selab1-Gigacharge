/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package be.ugent.gigacharge.model.service.impl

import android.util.Log
import be.ugent.gigacharge.data.local.models.Profile
import be.ugent.gigacharge.model.AuthenticationError
import be.ugent.gigacharge.model.service.AccountService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.properties.Delegates

class AccountServiceImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AccountService {
    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()

    override val hasUser: Boolean
        get() = auth.currentUser != null

    private val userCollection: CollectionReference
        get() = firestore.collection(USERS_COLLECTION_NAME)

    private val currentUserState: MutableStateFlow<FirebaseUser?> = MutableStateFlow(null)
    override val currentUser: Flow<Profile> = currentUserState.asStateFlow().transform { user ->
        if (user != null) {
            val userDoc = userCollection.document(user.uid).get().await()
            val userCardNumber = userDoc.get(CARDNUMBER_FIELD)
            if (userCardNumber != null) {
                emit(Profile(userCardNumber.toString(), false))
            } else {
                emptyFlow<Profile>()
            }
        } else {
            emptyFlow<Profile>()
        }
    }

    override fun syncProfile() {
        auth.addAuthStateListener { auth -> currentUserState.value = auth.currentUser }
    }

    private val authErrorFlow: MutableStateFlow<AuthenticationError> = MutableStateFlow(AuthenticationError.NO_ERROR)
    override val authError: StateFlow<AuthenticationError> = authErrorFlow.asStateFlow()

    override suspend fun createAnonymousAccount() {
        auth.signInAnonymously().await()
        Log.println(Log.INFO, "frick", "userid: $currentUserId")
    }

    override suspend fun deleteAccount() {
        Log.println(Log.INFO, "frick", "anon account in auth")
        auth.currentUser!!.delete().await()
    }

    override suspend fun signOut() {
        if (auth.currentUser!!.isAnonymous) {
            auth.currentUser!!.delete()
        }
        auth.signOut()

        // Sign the user back in anonymously.
        createAnonymousAccount()
    }

    override suspend fun tryEnable(cardNumber: String) {
        val enablerequest = hashMapOf(
            CARDNUMBER_FIELD to cardNumber,
            TIMESTAMP_FIELD to FieldValue.serverTimestamp()
        )
        Log.println(Log.INFO, "user id", currentUserId)
        userCollection.document(currentUserId).set(enablerequest).await()

        val startTime: Long = System.currentTimeMillis()
        var timer: Long
        var stop = false
        while (!stop) {
            // Force reloading user
            auth.currentUser?.reload()?.await()

            // Check if a user is logged in
            if (hasUser) {
                // Get the status
                val userDoc = userCollection.document(auth.currentUser!!.uid).get().await()

                // Check if enabled
                when (userDoc.get(ENABLE_STATUS).toString()) {
                    ENABLED -> {
                        isEnabledObservable = true
                        authErrorFlow.value = AuthenticationError.NO_ERROR
                        stop = true
                    }
                    NOT_IN_WHITELIST -> {
                        authErrorFlow.value = AuthenticationError.INVALID_CARD_NUMBER
                        stop = true
                    }
                    ENABLE_ERROR -> {
                        authErrorFlow.value = AuthenticationError.ERROR
                        stop = true
                    }
                }
            }

            // Update timer
            timer = System.currentTimeMillis() - startTime

            // Timeout error
            if (!stop && timer >= TIMEOUT_TIME) {
                authErrorFlow.value = AuthenticationError.TIMEOUT
                stop = true
            }
        }
    }

    override suspend fun isEnabled(): Boolean {
        return (hasUser && auth.currentUser?.getIdToken(true)
            ?.await()?.claims?.get("enabled") == true)
    }

    override val isEnabledObservers = mutableListOf<((Boolean) -> Unit)>()
    private var isEnabledObservable: Boolean by Delegates.observable(false) { _, _, new ->
        isEnabledObservers.forEach { (it(new)) }
    }

    override fun sendToken(token: String) {
        userCollection.document(currentUserId).update("fcmtoken", token)
    }

    override suspend fun deleteProfile() {
        // Delete user data
        userCollection.document(currentUserId).delete().await()
        // Delete user
        auth.currentUser?.delete()?.await()
        // Go anonymous because account doesn't exist anymore
        createAnonymousAccount()
    }

    companion object {
        private const val TIMEOUT_TIME = 5000L
        private const val USERS_COLLECTION_NAME = "users"
        private const val CARDNUMBER_FIELD = "kaartnummer"
        private const val TIMESTAMP_FIELD = "timestamp"
        
        private const val ENABLE_STATUS = "enablestatus"
        private const val ENABLED = "enabled"
        private const val NOT_IN_WHITELIST = "not_in_whitelist_error"
        private const val ENABLE_ERROR = "unknown_error"
    }
}

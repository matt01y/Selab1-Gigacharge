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

import android.content.res.Resources
import android.util.Log
import androidx.compose.ui.res.stringResource
import be.ugent.gigacharge.R
import be.ugent.gigacharge.model.AuthenticationError
import be.ugent.gigacharge.model.User
import be.ugent.gigacharge.model.service.AccountService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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

    val userCollection: CollectionReference
        get() = firestore.collection(USERS_COLLECTION_NAME)

    override val currentUser: Flow<User>
        get() = callbackFlow {
            Log.println(Log.INFO, "frick", "current user flow")
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let { User(it.uid, it.isAnonymous) } ?: User())
                }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }

    private val authErrorFlow: MutableStateFlow<AuthenticationError> = MutableStateFlow(AuthenticationError.NO_ERROR)
    override val authError: StateFlow<AuthenticationError> = authErrorFlow.asStateFlow()

    override suspend fun createAnonymousAccount() {
        auth.signInAnonymously().await()
        Log.println(Log.INFO, "frick", "userid: ${currentUserId}")
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
        val enablerequest = hashMapOf<String, Any>(
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
            
            val claims = auth.currentUser?.getIdToken(true)?.await()?.claims
            // Enabled
            if (claims?.get(ENABLE_STATUS) == ENABLED) {
                isEnabledObservable = true
                authErrorFlow.value = AuthenticationError.NO_ERROR
                stop = true
            // CardNumber not in whitelist
            } else if (claims?.get(ENABLE_STATUS) == NOT_IN_WHITELIST) {
                authErrorFlow.value = AuthenticationError.INVALID_CARD_NUMBER
                stop = true
            // Error occurred
            } else if (claims?.get(ENABLE_STATUS) == ENABLE_ERROR) {
                authErrorFlow.value = AuthenticationError.ERROR
                stop = true
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
    var isEnabledObservable: Boolean by Delegates.observable(false) { prop, old, new ->
        isEnabledObservers.forEach { (it(new)) }
    }

    override suspend fun init() {

    }

    override fun sendToken(token: String) {
        userCollection.document(currentUserId).update("fcmtoken", token)
    }

    companion object {
        private const val TIMEOUT_TIME = 5000L
        private const val LINK_ACCOUNT_TRACE = "linkAccount"
        private const val USERS_COLLECTION_NAME = "users"
        private const val CARDNUMBER_FIELD = "kaartnummer"
        private const val TIMESTAMP_FIELD = "timestamp"
        
        private const val ENABLE_STATUS = "enablestatus"
        private const val ENABLED = "enabled"
        private const val NOT_IN_WHITELIST = "not_in_whitelist_error"
        private const val ENABLE_ERROR = "unknown_error"
    }
}

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
import be.ugent.gigacharge.model.User
import be.ugent.gigacharge.model.service.AccountService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.properties.Delegates

class AccountServiceImpl @Inject constructor(private val auth: FirebaseAuth, private val firestore: FirebaseFirestore,) : AccountService {

  override val currentUserId: String
    get() = auth.currentUser?.uid.orEmpty()

  override val hasUser: Boolean
    get() = auth.currentUser != null

  val userCollection : CollectionReference
    get() = firestore.collection(USERS_COLLECTION_NAME)

  override val currentUser: Flow<User>
    get() = callbackFlow {
      val listener =
        FirebaseAuth.AuthStateListener { auth ->
          this.trySend(auth.currentUser?.let { User(it.uid, it.isAnonymous) } ?: User())
        }
      auth.addAuthStateListener(listener)
      awaitClose { auth.removeAuthStateListener(listener) }
    }

  override suspend fun authenticate(email: String, password: String) {
    auth.signInWithEmailAndPassword(email, password).await()
  }

  override suspend fun sendRecoveryEmail(email: String) {
    auth.sendPasswordResetEmail(email).await()
  }

  override suspend fun createAnonymousAccount() {
    auth.signInAnonymously().await()
  }

  override suspend fun linkAccount(email: String, password: String) {

  }

  override suspend fun deleteAccount() {
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

    var tries = 0
    while(!isEnabled && tries < 5){
      auth.currentUser?.reload()?.await()
      val claims = auth.currentUser?.getIdToken(true)?.await()?.claims
      if(claims?.get("enabled") == true){
        isEnabled = true
        break
      }else{
        Log.println(Log.INFO, "auth", "NOT NIET ENABLED")
      }
      delay(1000)
      tries ++
    }
  }

  override val isEnabledObservers = mutableListOf<((Boolean) -> Unit)>()
  var isEnabled : Boolean by Delegates.observable(false){ prop, old, new ->
    isEnabledObservers.forEach { (it(new)) }
  }

  companion object {
    private const val LINK_ACCOUNT_TRACE = "linkAccount"
    private const val USERS_COLLECTION_NAME = "users"
    private const val CARDNUMBER_FIELD = "kaartnummer"
    private const val TIMESTAMP_FIELD = "timestamp"
  }
}

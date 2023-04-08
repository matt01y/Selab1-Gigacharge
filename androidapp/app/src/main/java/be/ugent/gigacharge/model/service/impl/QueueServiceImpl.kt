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

import be.ugent.gigacharge.model.Location
import be.ugent.gigacharge.model.Task
import be.ugent.gigacharge.model.service.AccountService
import be.ugent.gigacharge.model.service.QueueService
import be.ugent.gigacharge.model.service.StorageService
import be.ugent.gigacharge.model.service.trace
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import javax.inject.Inject
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await

class QueueServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore, private val auth: AccountService) :
  QueueService {

  override val locations: Flow<List<Location>>
    get() = emptyFlow()

  override suspend fun getLocation(taskId: String): Location? =
    currentCollection(auth.currentUserId).document(taskId).get().await().toObject()

  override suspend fun joinQueue(loc: Location) {
    TODO("Not yet implemented")
  }

  override suspend fun leaveQueue(loc: Location) {
    TODO("Not yet implemented")
  }

  override suspend fun updateLocation(loc: Location) {
    TODO("Not yet implemented")
  }

  private fun currentCollection(uid: String): CollectionReference =
    firestore.collection(USER_COLLECTION).document(uid).collection(TASK_COLLECTION)




  companion object {
    private const val USER_COLLECTION = "users"
    private const val TASK_COLLECTION = "tasks"
    private const val SAVE_TASK_TRACE = "saveTask"
    private const val UPDATE_TASK_TRACE = "updateTask"
  }
}

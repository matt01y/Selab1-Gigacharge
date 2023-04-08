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
import be.ugent.gigacharge.model.service.AccountService
import be.ugent.gigacharge.model.service.QueueService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class QueueServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore, private val auth: AccountService) :
  QueueService {

  private val locationCollection : CollectionReference
    get() = firestore.collection(LOCATION_COLLECTION)

  override suspend fun getLocations(): List<Location> {
      val results = mutableListOf<Location>()
      val locationsnap = locationCollection.get().await()
      locationsnap.forEach { snap ->
        val name = snap.getString("name")!!
        results.add(
          Location(
            id = snap.id,
            name = name,
            amountWaiting = 0,
            myPosition = 0,
            amIJoined = false
          )
        )
      }
      return results
    }

  override suspend fun getLocation(locId: String): Location?{
    val data = locationCollection.document(locId).get().await()
    return Location(data.getString("name")!!, "placeholder", amountWaiting = 0, myPosition = 0, amIJoined = false)
  }


  override suspend fun joinQueue(loc: Location) {
    TODO("Not yet implemented")
  }

  override suspend fun leaveQueue(loc: Location) {
    TODO("Not yet implemented")
  }

  override suspend fun updateLocation(loc: Location) {
    TODO("Not yet implemented")
  }





  companion object {
    private const val LOCATION_COLLECTION = "locations"
  }
}

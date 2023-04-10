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
import be.ugent.gigacharge.model.Location
import be.ugent.gigacharge.model.service.AccountService
import be.ugent.gigacharge.model.service.QueueService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Singleton
class QueueServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore, private val accountService: AccountService) :
    QueueService {

    private val locationCollection: CollectionReference
        get() = firestore.collection(LOCATION_COLLECTION)

    private fun queueCollection(locid : String) : CollectionReference{
        return locationCollection.document(locid).collection(QUEUE_COLLECTION)
    }

    private var locations : List<Location> = emptyList()

    private var reEmitLocations : (() -> Unit)? = null

    override val getLocations: Flow<List<Location>> = callbackFlow {
        reEmitLocations = {
            Log.println(Log.INFO, "queue", "emitting now")
            trySend(locations)
        }
        Log.println(Log.INFO, "queue", "flow has been consumed")
        trySend(locations)
        awaitClose{reEmitLocations = null}
    }

    override suspend fun updateLocations(): List<Location> {
        Log.println(Log.INFO, "queue", "LOCATION UPDATE")
        val results = mutableListOf<Location>()
        val locationsnap = locationCollection.get().await()
        locationsnap.forEach { snap ->
            results.add(
                snapToLocation(snap)
            )
        }
        locations = results
        Log.println(Log.INFO, "queue", locations.toString())
        reEmitLocations?.let { it() }
        return locations
    }

    override suspend fun getLocation(locId: String): Location {
        val data = locationCollection.document(locId).get().await()
        return Location(
            data.getString("name")!!,
            "placeholder",
            amountWaiting = 0,
            myPosition = 0,
            amIJoined = false
        )
    }


    override suspend fun joinQueue(loc: Location) {
        Log.println(Log.INFO, "queue", "JOINING")
        val locid = loc.id

        queueCollection(locid).add(hashMapOf<String, Any>(
            USERID_FIELD to accountService.currentUserId,
            JOINEDAT_FIELD to FieldValue.serverTimestamp(),
            STATUS_FIELD to STATUS_WAITING
        ))

        locations = locations.map {
            if(it.id === locid) loc.copy(amIJoined = true)
            else it
        }
        reEmitLocations?.let { it() }

        //TODO("Not yet implemented")
    }

    override suspend fun leaveQueue(loc: Location) {
        Log.println(Log.INFO, "queue", "LEAVING")
        val batch = firestore.batch()
        val queue = queueCollection(loc.id)
        queue.whereEqualTo(USERID_FIELD, accountService.currentUserId).get().await().forEach {
            batch.update(queue.document(it.id), hashMapOf<String, Any>(
                STATUS_FIELD to STATUS_LEFT
            ))
        }
        batch.commit().await()
        locations = locations.map {
            if(it.id === loc.id) loc.copy(amIJoined = false)
            else it
        }
        reEmitLocations?.let { it() }
        //TODO("Not yet implemented")
    }

    //TODO: testen
    override suspend fun updateLocation(loc: Location): Location {
        val snap = locationCollection.document(loc.id).get().await()
        val newloc = snapToLocation(snap)
        locations = locations.mapIndexed{i, oloc ->
            if(oloc.id == loc.id){
                return newloc
            }else{
                return oloc;
            }
        }
        reEmitLocations?.let { it() }
        return newloc
        //TODO("Not yet implemented")
    }

    private fun snapToLocation(snap : DocumentSnapshot) : Location{
        return Location(
            id = snap.id,
            name = snap.getString("name")!!,
            amountWaiting = 0,
            myPosition = 0,
            amIJoined = false
        );
    }


    companion object {
        private const val LOCATION_COLLECTION = "locations"
        private const val QUEUE_COLLECTION = "queue"
        private const val USERID_FIELD = "user-id"
        private const val JOINEDAT_FIELD = "joined-at"
        private const val STATUS_FIELD = "status"
        private const val STATUS_WAITING = "waiting"
        private const val STATUS_LEFT = "left"
    }
}

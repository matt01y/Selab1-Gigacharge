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
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.location.QueueState
import be.ugent.gigacharge.model.service.AccountService
import be.ugent.gigacharge.model.service.QueueService
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
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

    private val emptyEmit : (() -> Unit) = {
        Log.println(Log.INFO, "queue", "emit geprobeerd maar is null")
    }
    private var reEmitLocations : (() -> Unit) = emptyEmit

    override val getLocations: Flow<List<Location>> = callbackFlow {
        reEmitLocations = {
            Log.println(Log.INFO, "queue", "emitting now")
            Log.println(Log.INFO, "queue", locations.toString())
            trySend(locations)
        }
        Log.println(Log.INFO, "queue", "flow has been consumed")
        trySend(locations)
        awaitClose{reEmitLocations = emptyEmit}
    }

    override suspend fun updateLocations(): List<Location> {
        Log.println(Log.INFO, "queue", "LOCATION UPDATE")
        val results = mutableListOf<Location>()
        val locationsnap = locationCollection.get().await()
        locationsnap.forEach { snap ->
            results.add(
                refToLocation(locationCollection.document(snap.id))
            )
        }
        locations = results
        Log.println(Log.INFO, "queue", locations.toString())
        return locations
    }

    override suspend fun getLocation(locId: String): Location {
        return refToLocation(locationCollection.document(locId))
    }


    override suspend fun joinQueue(loc: Location) {
        Log.println(Log.INFO, "queue", "JOINING")
        val locid = loc.id

        queueCollection(locid).add(hashMapOf<String, Any>(
            USERID_FIELD to accountService.currentUserId,
            JOINEDAT_FIELD to FieldValue.serverTimestamp(),
            STATUS_FIELD to STATUS_WAITING
        )).await()

        updateLocation(loc)

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
        updateLocation(loc)
        reEmitLocations()
        //TODO("Not yet implemented")
    }

    //TODO: testen
    override suspend fun updateLocation(loc: Location): Location {
        val newloc = refToLocation(locationCollection.document(loc.id))
        locations = locations.mapIndexed{i, oloc ->
            if(oloc.id == loc.id){
                Log.println(Log.INFO, "queue", "updated")
                Log.println(Log.INFO, "queue", newloc.toString())
                newloc
            }else{
                oloc
            }
        }
        Log.println(Log.INFO, "queueupdate", locations.toString())
        reEmitLocations()
        return newloc
        //TODO("Not yet implemented")
    }

    private suspend fun refToLocation(ref : DocumentReference) : Location {
        val snap = ref.get().await()
        val queuecollection = ref.collection(QUEUE_COLLECTION)
        val amountwaiting = queuecollection.count().get(AggregateSource.SERVER).await().count

        val state : QueueState
        if(amountwaiting > 0){
            val myJoinEvent = queuecollection.whereEqualTo(USERID_FIELD, accountService.currentUserId).whereEqualTo(
                STATUS_FIELD, STATUS_WAITING).limit(1).get().await()
            if(myJoinEvent.size() >= 1){
                val time =  myJoinEvent.first().getTimestamp(TIMESTAMP_FIELD)?:Timestamp.now() //TODO: deze null case beter maken
                val myPostition = queuecollection.whereLessThanOrEqualTo(TIMESTAMP_FIELD, time).count().get(AggregateSource.SERVER).await().count
                state = QueueState.Joined(myPosition = myPostition)
            }else{
                state = QueueState.NotJoined
            }
        }else{
            state = QueueState.NotJoined
        }

        return Location(
            id = snap.id,
            name = snap.getString(NAME_FIELD)?:"ERROR GEEN NAAM",
            amountWaiting = amountwaiting,
            queue = state
        )
    }


    companion object {
        private const val LOCATION_COLLECTION = "locations"
        private const val QUEUE_COLLECTION = "queue"
        private const val USERID_FIELD = "user-id"
        private const val JOINEDAT_FIELD = "joined-at"
        private const val STATUS_FIELD = "status"
        private const val NAME_FIELD = "name"
        private const val STATUS_WAITING = "waiting"
        private const val STATUS_LEFT = "left"
        private const val TIMESTAMP_FIELD = "timestamp"
    }
}

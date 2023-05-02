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

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.location.LocationStatus
import be.ugent.gigacharge.model.location.QueueState
import be.ugent.gigacharge.model.location.charger.Charger
import be.ugent.gigacharge.model.location.charger.ChargerStatus
import be.ugent.gigacharge.model.location.charger.UserField
import be.ugent.gigacharge.model.location.charger.UserType
import be.ugent.gigacharge.model.service.AccountService
import be.ugent.gigacharge.model.service.QueueService
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore, private val accountService: AccountService) :
    QueueService {

    private val locationCollection: CollectionReference
        get() = firestore.collection(LOCATION_COLLECTION)

    private fun queueCollection(locid: String): CollectionReference {
        return locationCollection.document(locid).collection(QUEUE_COLLECTION)
    }


    override val locationMap: SnapshotStateMap<String, Location> = mutableStateMapOf()

    @RequiresApi(Build.VERSION_CODES.N)
    override suspend fun updateLocations(): List<Location> {
        Log.println(Log.INFO, "queue", "LOCATION UPDATE")
        val results = mutableListOf<Location>()
        val locationsnap = locationCollection.get().await()

        val newmap = HashMap<String, Location>();

        locationsnap.forEach { snap ->
            val res = refToLocation(locationCollection.document(snap.id))
            locationMap[res.id] = res
            newmap.put(res.id, res)
            results.add(res)
        }

        locationMap.replaceAll { id, location ->
            val loc : Location = newmap.get(id)!!
            loc
        }

        Log.println(Log.INFO, "queue", locationMap.toMap().toString())
        return results
    }

    override suspend fun getLocation(locId: String): Location {
        return refToLocation(locationCollection.document(locId))
    }


    override suspend fun joinQueue(loc: Location) {
        Log.println(Log.INFO, "queue", "JOINING")
        val locid = loc.id

        queueCollection(locid).add(
            hashMapOf<String, Any>(
                USERID_FIELD to accountService.currentUserId,
                JOINEDAT_FIELD to FieldValue.serverTimestamp(),
                STATUS_FIELD to STATUS_WAITING
            )
        ).await()

        updateLocation(loc)

        //TODO("Not yet implemented")
    }

    override suspend fun leaveQueue(loc: Location) {
        Log.println(Log.INFO, "queue", "LEAVING")
        val batch = firestore.batch()
        val queue = queueCollection(loc.id)
        queue.whereEqualTo(USERID_FIELD, accountService.currentUserId).get().await().forEach {
            batch.update(
                queue.document(it.id), hashMapOf<String, Any>(
                    STATUS_FIELD to STATUS_LEFT
                )
            )
        }
        batch.commit().await()
        updateLocation(loc)
        //TODO("Not yet implemented")
    }

    //TODO: testen
    override suspend fun updateLocation(loc: Location): Location {
        val newloc = refToLocation(locationCollection.document(loc.id))
        locationMap[loc.id] = newloc
        Log.println(Log.INFO, "queueupdate", locationMap.toMap().toString())
        return newloc
        //TODO("Not yet implemented")
    }

    private suspend fun refToLocation(ref: DocumentReference): Location {
        val snap = ref.get().await()
        val queuecollection = ref.collection(QUEUE_COLLECTION)
        val amountwaiting = queuecollection.whereEqualTo(STATUS_FIELD, STATUS_WAITING).count()
            .get(AggregateSource.SERVER).await().count

        var state: QueueState
        if (amountwaiting > 0) {
            val myJoinEvent =
                queuecollection.whereEqualTo(USERID_FIELD, accountService.currentUserId)
                    .whereEqualTo(
                        STATUS_FIELD, STATUS_WAITING
                    ).limit(1).get().await()
            if (myJoinEvent.size() >= 1) {
                val time = myJoinEvent.first().getTimestamp(TIMESTAMP_FIELD)
                    ?: Timestamp.now() //TODO: deze null case beter maken
                val myPosition =
                    queuecollection.whereLessThanOrEqualTo(JOINEDAT_FIELD, time).whereIn(
                        STATUS_FIELD, listOf(STATUS_WAITING, STATUS_ASSIGNED)
                    ).count().get(AggregateSource.SERVER).await().count - 1
                Log.println(Log.INFO, "queue", "POSITION $myPosition")
                state = QueueState.Joined(myPosition = myPosition)
            } else {
                state = QueueState.NotJoined
            }
        } else {
            state = QueueState.NotJoined
        }


        val chargerdocuments = snap.get("chargers") as List<DocumentSnapshot>?
        val chargers: List<Charger> = (chargerdocuments ?: listOf()).map {
            val ut : UserType = UserType.valueOf(it.get("usertype") as String)
            Charger(
                it.get("description") as String,
                it.id, //.get("id") as String,
                ChargerStatus.valueOf(it.get("status") as String),
                when(ut) {
                    UserType.USER -> UserField.UserID(it.get("user") as String)
                    UserType.NONUSER -> UserField.CardNumber(it.get("user") as String)
                },
                ut
            )
        }

        val user_id = snap.id

        // check of er een charger id die een user charget met jouw id
        // als dat het geval is, betekent dat dat jij bent aan het opladen
        for (charger in chargers) {
            when(charger.user) {
                is UserField.UserID -> {
                    if (charger.user.id == user_id) {
                        state = QueueState.Charging
                        break
                    }
                }
                is UserField.CardNumber -> {
                    if (charger.user.cardnum == user_id) {
                        state = QueueState.Charging
                        break
                    }
                }
                else -> {}
            }
        }

        Log.i("userid", user_id)

        val queuedocuments = snap.reference.collection(QUEUE_COLLECTION)
            .whereEqualTo(USERID_FIELD, accountService.currentUserId)
            .whereEqualTo(STATUS_FIELD, STATUS_ASSIGNED)
            .get().await().documents;
        if (queuedocuments.isNotEmpty()) {
            val queueEntry = queuedocuments.first()
            println(queueEntry != null)
            val chargerID = queueEntry.get("assigned") as String
            println("charger id: " + chargerID)
            var assignedCharger : Charger = chargers.first()
            for (charger in chargers) {
                if (charger.id == chargerID) {
                    assignedCharger = charger
                    break;
                }
            }
            state = QueueState.Assigned
        }

        val result = Location(
            id = user_id,
            name = snap.getString(NAME_FIELD) ?: "ERROR GEEN NAAM",
            amountWaiting = amountwaiting,
            status = LocationStatus.valueOf(snap.get("status") as String),
            queue = state,
            chargers = chargers
        )

        println(result.status.toString())


        return result
    }


    companion object {
        private const val LOCATION_COLLECTION = "locations"
        private const val QUEUE_COLLECTION = "queue"
        private const val USERID_FIELD = "user-id"
        private const val JOINEDAT_FIELD = "joined-at"
        private const val STATUS_FIELD = "status"
        private const val NAME_FIELD = "name"
        private const val STATUS_WAITING = "waiting"
        private const val STATUS_ASSIGNED = "assigned"
        private const val STATUS_LEFT = "left"
        private const val TIMESTAMP_FIELD = "timestamp"
    }
}
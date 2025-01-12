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

        val newmap = HashMap<String, Location>()

        locationsnap.forEach { snap ->
            val res = refToLocation(locationCollection.document(snap.id))
            newmap[res.id] = res
            results.add(res)
        }

        locationMap.clear()
        locationMap.putAll(newmap)

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
            hashMapOf(
                USERID_FIELD to accountService.currentUserId,
                JOINEDAT_FIELD to FieldValue.serverTimestamp(),
                STATUS_FIELD to STATUS_WAITING
            )
        ).await()

        updateLocation(locid)

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
        updateLocation(loc.id)
    }

    //TODO: testen
    override suspend fun updateLocation(locId : String): Location? {
        return if(locationMap.keys.contains(locId)){
            val newloc = refToLocation(locationCollection.document(locId))
            locationMap[locId] = newloc
            Log.println(Log.INFO, "queueupdate", locationMap.toMap().toString())
            newloc
        }else{
            null
        }
    }

    private suspend fun refToLocation(ref: DocumentReference): Location {
        val snap = ref.get().await()
        val queuecollection = ref.collection(QUEUE_COLLECTION)
        val chargerscollection = ref.collection(CHARGER_COLLECTION)

        //chargers inlezen
        val chargerdocuments = chargerscollection.get().await().documents
        val chargers: List<Charger> = chargerdocuments.map {
            var ut : UserType = UserType.NONUSER
            try{
                ut = UserType.valueOf(it.getString("usertype")?:"NONUSER")
            }catch(err : Exception){
                println("GEEN USERTYPE")
            }
            Charger(
                it.getString("description")?:"Geen beschrijving",
                it.id, //.get("id") as String,
                ChargerStatus.valueOf(it.getString("status")?.uppercase()?:"FOUT"),
                when(ut) {
                    UserType.USER -> UserField.UserID(it.getString("user")?:"FOUT")
                    UserType.NONUSER -> UserField.CardNumber(it.getString("user")?:"FOUT")
                },
                ut
            )
        }

        //status van user in de queue bepalen
        val amountwaiting = queuecollection.whereIn(
                STATUS_FIELD,
                listOf( STATUS_WAITING, STATUS_ASSIGNED )
            ).count().get(AggregateSource.SERVER).await().count
        var state: QueueState = QueueState.NotJoined
        if (amountwaiting > 0) {
            Log.i("queue", "amount groter dan 0")
            val myJoinEvents =
                queuecollection.whereEqualTo(USERID_FIELD, accountService.currentUserId)
                    .whereIn(
                        STATUS_FIELD,
                        listOf( STATUS_WAITING, STATUS_ASSIGNED )
                    ).limit(1).get().await()
            if (myJoinEvents.size() >= 1) {
                Log.i("queue", "JOINDOC GEVONDEN")
                val myJoinEvent = myJoinEvents.first()
                val statusString = myJoinEvent.getString(STATUS_FIELD)
                Log.i("queue", statusString?:"geen statusstring")
                val time = myJoinEvent.getTimestamp(JOINEDAT_FIELD)
                    ?: Timestamp.now() //TODO: deze null case beter maken
                when(statusString){
                    STATUS_WAITING -> {

                        val myPosition =
                            queuecollection.whereLessThan(JOINEDAT_FIELD, time).whereIn(
                                STATUS_FIELD, listOf(STATUS_WAITING, STATUS_ASSIGNED)
                            ).count().get(AggregateSource.SERVER).await().count
                        Log.println(Log.INFO, "queue", "POSITION $myPosition")
                        state = QueueState.Joined(myPosition = myPosition)
                    }

                    STATUS_ASSIGNED -> {
                        Log.i("queue", "assigned gevonden")
                        val mycharger =
                            chargers.firstOrNull { it.id == myJoinEvent.getString("assigned") }
                        Log.i("queue", chargers.toString())
                        val expiretime = myJoinEvent.getTimestamp(EXPIRES_FIELD)
                            ?: Timestamp.now() //TODO: deze null case beter maken
                        mycharger?.apply {
                            Log.i("queue", "state gezet")
                            state = QueueState.Assigned(expiretime.toDate(), mycharger)
                        }
                    }
                }

            }
        }


        val result = Location(
            id = ref.id,
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
        private const val CHARGER_COLLECTION = "chargers"
        private const val USERID_FIELD = "user-id"
        private const val JOINEDAT_FIELD = "joined-at"
        private const val STATUS_FIELD = "status"
        private const val NAME_FIELD = "name"
        private const val STATUS_WAITING = "waiting"
        private const val STATUS_ASSIGNED = "assigned"
        private const val STATUS_LEFT = "left"
        private const val EXPIRES_FIELD = "expires"
    }
}
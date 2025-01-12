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

package be.ugent.gigacharge.model.location

import be.ugent.gigacharge.model.location.charger.Charger
import com.google.firebase.firestore.DocumentId

data class Location(
    @DocumentId val id: String = "",
    val name: String = "",
    val queue: QueueState,
    val status: LocationStatus,
    val amountWaiting: Long,
    val chargers: List<Charger>
) {
    constructor() : this(
        "",
        "",
        QueueState.NotJoined,
        LocationStatus.OUT,
        0,
        listOf()
    )

    val amIJoined: Boolean
        get() = queue is QueueState.Joined

    val assignedChargerId : String?
        get() = when(queue){
                    is QueueState.Assigned -> queue.charger.id
                    else -> null
                }
}

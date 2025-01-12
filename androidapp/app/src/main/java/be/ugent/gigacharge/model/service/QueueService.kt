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

package be.ugent.gigacharge.model.service

import androidx.compose.runtime.snapshots.SnapshotStateMap
import be.ugent.gigacharge.model.location.Location

interface QueueService {
    val locationMap: SnapshotStateMap<String, Location>
    suspend fun updateLocations(): List<Location>

    suspend fun getLocation(locId: String): Location?

    suspend fun joinQueue(loc: Location)
    suspend fun leaveQueue(loc: Location)
    suspend fun updateLocation(locId: String): Location?
}

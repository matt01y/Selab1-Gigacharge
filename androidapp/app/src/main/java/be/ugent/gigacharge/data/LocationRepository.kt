package be.ugent.gigacharge.data

import androidx.compose.runtime.snapshotFlow
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.service.QueueService
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val queueService: QueueService
) {
    private val locationIdFlow: MutableStateFlow<String?> = MutableStateFlow(null)
    public val locations : Flow<Map<String, Location>> = snapshotFlow { queueService.locationMap.toMap() }

    val getLocation : Flow<Location?> = locations.combine(locationIdFlow){map, id -> map[id]}


    fun setLocation(location: Location) {
        locationIdFlow.value = location.id
    }

    fun toggleFavorite(location: Location) {
        // Suggestion: at most 1
    }
}
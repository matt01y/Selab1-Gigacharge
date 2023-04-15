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
    private val locations : Flow<Map<String, Location>> = snapshotFlow { queueService.locationMap.toMap() }

    fun getLocation(): Flow<Location> = locations.combine(locationIdFlow){map, id -> map[id]}.transform { location ->
        if (location == null) {
            emit(locations.last().values.first())
        } else {
            emit(location)
        }
    }

    fun getLocations(): Flow<List<Location>> {
        return locations.map { it.values.toList() }
    }

    fun setLocation(location: Location) {
        locationIdFlow.value = location.id
    }

    fun toggleFavorite(location: Location) {
        // Suggestion: at most 1
    }
}
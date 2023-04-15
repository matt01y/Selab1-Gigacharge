package be.ugent.gigacharge.data

import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.location.QueueState
import be.ugent.gigacharge.model.service.QueueService
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val queueService: QueueService
) {
    private val locationFlow: MutableStateFlow<Location?> = MutableStateFlow(null)

    fun getLocation(): Flow<Location> = locationFlow.flatMapLatest { location ->
        // If the location is null, get the first, first favorite, last used, ... location
        // Simplest: first in locations
        if (location == null) {
            getLocations().transform { locations ->
                if (locations.isEmpty()) {
                    emptyFlow<Location>()
                } else {
                    emit(locations[0])
                }
            }
        } else {
            flowOf(location)
        }
    }

    fun getLocations(): Flow<List<Location>> {
        return queueService.getLocations
    }

    fun setLocation(location: Location) {
        locationFlow.value = location
    }
}
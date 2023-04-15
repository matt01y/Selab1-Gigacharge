package be.ugent.gigacharge.data

import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.service.QueueService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(queueService: QueueService) {
    fun getLocation(): Flow<Location> {
        return emptyFlow()
    }

    fun getLocations(): Flow<List<Location>> {
        return emptyFlow()
    }

    fun setLocation(location: Location) {

    }
}
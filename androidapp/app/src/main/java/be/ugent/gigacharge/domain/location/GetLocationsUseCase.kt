package be.ugent.gigacharge.domain.location

import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.service.QueueService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationsUseCase @Inject constructor(
    private val queueService : QueueService
) {
    operator fun invoke(): Flow<List<Location>> = queueService.getLocations
}
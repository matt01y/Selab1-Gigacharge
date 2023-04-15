package be.ugent.gigacharge.domain.location

import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.service.QueueService
import javax.inject.Inject

class SetLocationUseCase @Inject constructor(
    private val queueService: QueueService
) {
    suspend operator fun invoke(location: Location) {
        queueService.updateLocation(location)
    }
}
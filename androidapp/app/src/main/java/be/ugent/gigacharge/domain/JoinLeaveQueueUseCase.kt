package be.ugent.gigacharge.domain

import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.service.QueueService
import javax.inject.Inject

class JoinLeaveQueueUseCase @Inject constructor(
    private val queueService: QueueService
) {
    suspend operator fun invoke(location: Location) {
        if (location.amIJoined) {
            queueService.leaveQueue(location)
        } else {
            queueService.joinQueue(location)
        }
    }
}
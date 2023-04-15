package be.ugent.gigacharge.domain.queue

import be.ugent.gigacharge.data.QueueRepository
import be.ugent.gigacharge.model.location.Location
import javax.inject.Inject

class JoinLeaveQueueUseCase @Inject constructor(
    private val queueRepository: QueueRepository
) {
    suspend operator fun invoke(location: Location) = queueRepository.joinLeaveQueue(location)
}
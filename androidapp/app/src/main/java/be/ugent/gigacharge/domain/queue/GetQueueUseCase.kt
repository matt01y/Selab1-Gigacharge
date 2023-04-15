package be.ugent.gigacharge.domain.queue

import be.ugent.gigacharge.data.QueueRepository
import be.ugent.gigacharge.data.local.models.Queue
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQueueUseCase @Inject constructor(
    private val queueRepository: QueueRepository
) {
    operator fun invoke(): Flow<Queue> = queueRepository.getQueue()
}
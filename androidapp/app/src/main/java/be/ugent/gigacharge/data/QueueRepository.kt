package be.ugent.gigacharge.data

import be.ugent.gigacharge.data.local.models.Queue
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.service.QueueService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueRepository @Inject constructor(
    private val queueService: QueueService
) {
    fun getQueue(): Flow<Queue> = flowOf(Queue(listOf(), "Roularta Roeselare"))

    suspend fun joinLeaveQueue(location: Location) {
        if (location.amIJoined) {
            queueService.leaveQueue(location)
        } else {
            queueService.joinQueue(location)
        }
    }
}

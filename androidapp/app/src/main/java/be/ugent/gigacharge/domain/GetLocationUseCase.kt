package be.ugent.gigacharge.domain

import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.service.QueueService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val queueService : QueueService
) {
    suspend operator fun invoke(id : String): Flow<Location?> = flowOf(queueService.getLocation(id))
}
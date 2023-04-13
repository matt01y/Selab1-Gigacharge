package be.ugent.gigacharge.domain

import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.service.QueueService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationUsecase @Inject constructor(
    private val queueService : QueueService
) {
    //operator fun invoke(): Flow<Location?> = queueService.getLocation("")
}
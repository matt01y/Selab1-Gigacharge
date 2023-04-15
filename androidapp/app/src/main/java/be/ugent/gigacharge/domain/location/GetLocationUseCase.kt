package be.ugent.gigacharge.domain.location

import be.ugent.gigacharge.data.LocationRepository
import be.ugent.gigacharge.model.location.Location
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Flow<Location?> = locationRepository.getLocation
}
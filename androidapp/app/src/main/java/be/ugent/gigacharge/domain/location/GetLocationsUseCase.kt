package be.ugent.gigacharge.domain.location

import be.ugent.gigacharge.data.LocationRepository
import be.ugent.gigacharge.model.location.Location
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLocationsUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Flow<List<Location>> = locationRepository.locations.map { it.values.toList() }
}
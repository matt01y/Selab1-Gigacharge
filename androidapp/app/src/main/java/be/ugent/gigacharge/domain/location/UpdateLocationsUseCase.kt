package be.ugent.gigacharge.domain.location

import be.ugent.gigacharge.data.LocationRepository
import javax.inject.Inject

class UpdateLocationsUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke() = locationRepository.updateLocations()
}
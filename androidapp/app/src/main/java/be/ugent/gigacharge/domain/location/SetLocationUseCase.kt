package be.ugent.gigacharge.domain.location

import be.ugent.gigacharge.data.LocationRepository
import be.ugent.gigacharge.model.location.Location
import javax.inject.Inject

class SetLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(location: Location) = locationRepository.setLocation(location)
}
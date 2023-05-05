package be.ugent.gigacharge.domain.profile

import be.ugent.gigacharge.data.LocationRepository
import be.ugent.gigacharge.data.ProfileRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(action: ()->Unit, cardNumber: String) {
        profileRepository.registerUser(action, cardNumber)
        locationRepository.updateLocations()
    }
}
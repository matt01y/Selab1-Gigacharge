package be.ugent.gigacharge.domain.profile

import be.ugent.gigacharge.data.ProfileRepository
import be.ugent.gigacharge.data.local.models.Profile
import javax.inject.Inject

class SaveProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(profile: Profile) = profileRepository.saveProfile(profile)
}
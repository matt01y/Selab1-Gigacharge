package be.ugent.gigacharge.domain.profile

import be.ugent.gigacharge.data.ProfileRepository
import javax.inject.Inject

class DeleteProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke() = profileRepository.deleteProfile()
}
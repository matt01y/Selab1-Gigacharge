package be.ugent.gigacharge.domain.profile

import be.ugent.gigacharge.data.ProfileRepository
import be.ugent.gigacharge.data.local.models.Profile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(): Flow<Profile> = profileRepository.profile
}
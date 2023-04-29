package be.ugent.gigacharge.domain.profile

import be.ugent.gigacharge.data.ProfileRepository
import be.ugent.gigacharge.model.AuthenticationError
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthenticationErrorsUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(): Flow<AuthenticationError> = profileRepository.authenticationErrors
}
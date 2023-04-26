package be.ugent.gigacharge.domain.profile

import be.ugent.gigacharge.data.ProfileRepository
import javax.inject.Inject

class IsValidCardNumberUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(cardNumber: String): Boolean = profileRepository.isValidCardNumber(cardNumber)
}
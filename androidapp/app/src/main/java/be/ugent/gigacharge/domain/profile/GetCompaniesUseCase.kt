package be.ugent.gigacharge.domain.profile

import be.ugent.gigacharge.data.ProfileRepository
import javax.inject.Inject

class GetCompaniesUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    operator fun invoke(): List<String> = profileRepository.getCompanies()
}
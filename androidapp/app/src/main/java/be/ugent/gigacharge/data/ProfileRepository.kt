package be.ugent.gigacharge.data

import be.ugent.gigacharge.data.local.models.Profile
import be.ugent.gigacharge.model.service.QueueService
import be.ugent.gigacharge.model.service.AccountService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    queueService: QueueService,
    private val accountService: AccountService
) {
    private var isVisibleFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var profileFlow: MutableStateFlow<Profile?> =
        MutableStateFlow(Profile("MobilityPlus", "1234 - 5678", "Roularta", false))

    init {
        // Add listener to the whitelist documents
        accountService.syncWhitelist()
    }

    fun getProfile(): Flow<Profile> = profileFlow.flatMapLatest { profile ->
        if (profile == null) {
            emptyFlow()
        } else {
            isVisibleFlow.transform { isVisible ->
                val profile2 = Profile(
                    profile.provider,
                    profile.cardNumber,
                    profile.company,
                    isVisible
                )
                emit(profile2)
            }
        }
    }

    fun toggleProfile() {
        isVisibleFlow.value = !isVisibleFlow.value
    }

    fun saveProfile(profile: Profile) {
        profileFlow.value = profile
    }

    fun getProviders(): List<String> {
        return listOf("MobilityPlus", "BlueCorner")
    }

    fun getCompanies(): List<String> {
        return listOf("Roularta", "UGent")
    }

    fun isValidCardNumber(cardNumber: String): Boolean {
        return accountService.isCardNumberInWhitelist(cardNumber)
    }
}
package be.ugent.gigacharge.data

import be.ugent.gigacharge.data.local.models.Profile
import be.ugent.gigacharge.model.service.QueueService
import be.ugent.gigacharge.model.service.AccountService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    queueService: QueueService,
    private val accountService: AccountService
) {
    private var isVisibleFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var profileFlow: MutableStateFlow<Profile?> = MutableStateFlow(Profile("1234 - 5678", false))

    fun getProfile(): Flow<Profile> = profileFlow.flatMapLatest { profile ->
        if (profile == null) {
            emptyFlow()
        } else {
            isVisibleFlow.transform { isVisible ->
                val profile2 = Profile(profile.cardNumber, isVisible)
                emit(profile2)
            }
        }
    }

    fun toggleProfile() {
        isVisibleFlow.value = !isVisibleFlow.value
    }

    fun deleteProfile() {
        GlobalScope.launch {
            accountService.deleteProfile()
        }
    }
}
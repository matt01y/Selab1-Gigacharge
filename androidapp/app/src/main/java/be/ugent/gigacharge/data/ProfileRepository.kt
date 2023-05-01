package be.ugent.gigacharge.data

import be.ugent.gigacharge.data.local.models.Profile
import be.ugent.gigacharge.model.AuthenticationError
import be.ugent.gigacharge.model.service.AccountService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val accountService: AccountService
) {
    private var isVisibleFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val authenticationErrors: Flow<AuthenticationError> = accountService.authError
    val profile: Flow<Profile> = accountService.currentUser.flatMapLatest { profile ->
        isVisibleFlow.transform { isVisible -> emit(Profile(profile.cardNumber, isVisible)) }
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
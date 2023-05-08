package be.ugent.gigacharge.features.register

import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.domain.profile.GetAuthenticationErrorsUseCase
import be.ugent.gigacharge.domain.profile.RegisterUserUseCase
import be.ugent.gigacharge.model.AuthenticationError
import be.ugent.gigacharge.model.service.LogService
import be.ugent.gigacharge.screens.GigaChargeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    logService: LogService,
    getAuthenticationErrorsUseCase: GetAuthenticationErrorsUseCase,
    private val registerUserUseCase: RegisterUserUseCase
) : GigaChargeViewModel(logService) {
    val authenticationErrors: StateFlow<AuthenticationError> = getAuthenticationErrorsUseCase().stateIn(viewModelScope, SharingStarted.Eagerly, AuthenticationError.NO_ERROR)
    private val cardNumberState: MutableStateFlow<String> = MutableStateFlow("")
    val cardNumber: StateFlow<String> = cardNumberState.asStateFlow()

    fun setCardNumber(newCardNumber: String) {
        cardNumberState.value = newCardNumber
    }

    fun onRegister(action: () -> Unit, cardNumber: String) {
        launchCatching { registerUserUseCase(action, cardNumber) }
    }
}

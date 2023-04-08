package be.ugent.gigacharge.screens.register

import androidx.compose.runtime.mutableStateOf
import be.ugent.gigacharge.model.service.AccountService
import be.ugent.gigacharge.model.service.ConfigurationService
import be.ugent.gigacharge.model.service.LogService
import be.ugent.gigacharge.screens.GigaChargeViewModel
import be.ugent.gigacharge.screens.MAIN_SCREEN
import be.ugent.gigacharge.screens.REGISTER_SCREEN
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val accountService: AccountService,
    logService: LogService
) : GigaChargeViewModel(logService) {
    var uiState = mutableStateOf(RegisterUiState())
        private set

    private val cardnumber
        get() = uiState.value.cardnumber

    fun onCardNumberChange(newValue : String){
        uiState.value = uiState.value.copy(cardnumber = newValue)
    }

    fun onRegister(openAndPopUp: (String, String) -> Unit){
        uiState.value = uiState.value.copy(statusmessage = "nu wordt er geprobeerd om te enablen met u kaartnummber")

        launchCatching {
            accountService.isEnabledObservers.add {
                if (it) {
                    openAndPopUp(MAIN_SCREEN, REGISTER_SCREEN)
                    uiState.value = uiState.value.copy(statusmessage = "enabling gelukt")
                } else {
                    uiState.value = uiState.value.copy(statusmessage = "enabling niet gelukt")
                }
            }
            accountService.tryEnable(cardnumber)
        }
    }
}

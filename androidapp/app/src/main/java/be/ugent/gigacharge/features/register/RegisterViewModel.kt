package be.ugent.gigacharge.features.register

import androidx.compose.runtime.mutableStateOf
import be.ugent.gigacharge.model.service.AccountService
import be.ugent.gigacharge.model.service.ConfigurationService
import be.ugent.gigacharge.model.service.LogService
import be.ugent.gigacharge.navigation.Destinations
import be.ugent.gigacharge.screens.GigaChargeViewModel
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

    fun onRegister(openAndPopUp: () -> Unit){
        uiState.value = uiState.value.copy(statusmessage = "nu wordt er geprobeerd om te enablen met u kaartnummber")

        launchCatching {
            accountService.isEnabledObservers.add {
                if (it) {
                    openAndPopUp()
                    uiState.value = uiState.value.copy(statusmessage = "enabling gelukt")
                } else {
                    uiState.value = uiState.value.copy(statusmessage = "enabling niet gelukt")
                }
            }
            accountService.tryEnable(cardnumber)
        }
    }
}

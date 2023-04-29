package be.ugent.gigacharge.features.register

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import be.ugent.gigacharge.model.service.AccountService
import be.ugent.gigacharge.model.service.LogService
import be.ugent.gigacharge.model.service.QueueService
import be.ugent.gigacharge.screens.GigaChargeViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val accountService: AccountService,
    private val queueService: QueueService,
    logService: LogService,
) : GigaChargeViewModel(logService) {
    var uiState = mutableStateOf(RegisterUiState())
        private set

    private val cardnumber
        get() = uiState.value.cardnumber

    fun onCardNumberChange(newValue: String) {
        uiState.value = uiState.value.copy(cardnumber = newValue)
    }

    fun onRegister(openAndPopUp: () -> Unit) {
        uiState.value =
            uiState.value.copy(statusmessage = "nu wordt er geprobeerd om te enablen met u kaartnummber")

        launchCatching {
            accountService.isEnabledObservers.add {
                if (it) {
                    launchCatching {
                        queueService.updateLocations()

                        FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
                            if (result != null) {
                                Log.i("token", result)
                                accountService.sendToken(result)
                                // DO your thing with your firebase token
                            } else {
                                Log.i("token", "geen token gevonden")
                            }
                        }

                        openAndPopUp()
                        uiState.value = uiState.value.copy(statusmessage = "enabling gelukt")
                    }
                } else {
                    uiState.value = uiState.value.copy(statusmessage = "enabling niet gelukt")
                }
            }
            accountService.tryEnable(cardnumber)
            //queueService.updateLocations()
        }
    }
}

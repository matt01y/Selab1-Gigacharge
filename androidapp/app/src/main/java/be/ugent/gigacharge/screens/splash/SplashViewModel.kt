package be.ugent.gigacharge.screens.splash

import androidx.compose.runtime.mutableStateOf
import be.ugent.gigacharge.REGISTER_SCREEN
import be.ugent.gigacharge.model.service.AccountService
import be.ugent.gigacharge.model.service.ConfigurationService
import be.ugent.gigacharge.model.service.LogService
import be.ugent.gigacharge.SPLASH_SCREEN
import be.ugent.gigacharge.screens.GigaChargeViewModel
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    configurationService: ConfigurationService,
    private val accountService: AccountService,
    logService: LogService
) : GigaChargeViewModel(logService) {
    val showError = mutableStateOf(false)

    init {
        launchCatching { configurationService.fetchConfiguration() }
    }

    fun onAppStart(openAndPopUp: (String, String) -> Unit) {

        showError.value = false
        launchCatching(snackbar = false) {

            if (accountService.hasUser){
                accountService.start()
                openAndPopUp(REGISTER_SCREEN, SPLASH_SCREEN)
            }
            else {
                try {
                    accountService.createAnonymousAccount()
                    accountService.start()
                } catch (ex: FirebaseAuthException) {
                    showError.value = true
                    throw ex
                }
                openAndPopUp(REGISTER_SCREEN, SPLASH_SCREEN)
            }
        }

    }
}

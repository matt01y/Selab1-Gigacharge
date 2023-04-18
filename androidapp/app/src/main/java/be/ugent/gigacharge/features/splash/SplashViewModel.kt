package be.ugent.gigacharge.features.splash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import be.ugent.gigacharge.model.service.AccountService
import be.ugent.gigacharge.model.service.ConfigurationService
import be.ugent.gigacharge.model.service.LogService
import be.ugent.gigacharge.model.service.QueueService
import be.ugent.gigacharge.navigation.Destinations
import be.ugent.gigacharge.screens.GigaChargeViewModel
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    configurationService: ConfigurationService,
    private val accountService: AccountService,
    private val queueService: QueueService,
    logService: LogService
) : GigaChargeViewModel(logService) {
    val showError = mutableStateOf(false)

    init {
        launchCatching { configurationService.fetchConfiguration() }
    }

    fun onAppStart(openAndPopUp: (String) -> Unit) {

        showError.value = false
        launchCatching(snackbar = false) {
            Log.println(Log.INFO, "frick", "coroutine van splashcreen gestart")
            if (accountService.isEnabled()){

                FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
                    if(result != null){
                        Log.i("token", result)
                        accountService.sendToken(result)
                        // DO your thing with your firebase token
                    }else{
                        Log.i("token", "geen token gevonden")
                    }
                }

                Log.println(Log.INFO, "frick", "account was al ge-enabled")
                //queueService.updateLocations()
                queueService.updateLocations()
                openAndPopUp(Destinations.MAIN)
            }
            else {
                Log.println(Log.INFO, "frick", "niet enabled, create anon")
                try {
                    accountService.createAnonymousAccount()
                    Log.println(Log.INFO, "frick", "locaties updaten")
                    //queueService.updateLocations()
                } catch (ex: FirebaseAuthException) {
                    Log.println(Log.INFO, "frick", "frickin fout gdamnit")
                    showError.value = true
                    throw ex
                }
                Log.println(Log.INFO, "frick", "popup start")

                openAndPopUp(Destinations.REGISTER_SCREEN)
            }
        }

    }

}

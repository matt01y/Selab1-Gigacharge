package be.ugent.gigacharge.data

import android.util.Log
import be.ugent.gigacharge.data.local.models.Profile
import be.ugent.gigacharge.model.AuthenticationError
import be.ugent.gigacharge.model.service.AccountService
import be.ugent.gigacharge.screens.GigaChargeViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    suspend fun registerUser(action: ()->Unit, cardNumber:String) {
        Log.i("Register", cardNumber)

        // Add a listener to the isEnabled value
        accountService.isEnabledObservers.add {
            if (it) {
                // Add a messaging token to the user
                FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                    if (token != null) {
                        accountService.sendToken(token)
                    }
                }

                // The account is enabled, execute the given action
                action()
            }
        }

        // Try to enable the account
        accountService.tryEnable(cardNumber)
    }
}
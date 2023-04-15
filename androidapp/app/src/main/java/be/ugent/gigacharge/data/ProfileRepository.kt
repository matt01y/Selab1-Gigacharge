package be.ugent.gigacharge.data

import android.util.Log
import be.ugent.gigacharge.data.local.models.Profile
import be.ugent.gigacharge.data.local.models.ProfileState
import be.ugent.gigacharge.model.service.QueueService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(queueService: QueueService) {
    private var isVisibleFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var profileFlow: MutableStateFlow<Profile?> = MutableStateFlow(Profile("MobilityPlus", "1234 - 5678", "Roularta"))

    fun getProfile(): Flow<ProfileState> = profileFlow.flatMapLatest { profile ->
        if (profile == null) {
            emptyFlow()
        } else {
            isVisibleFlow.transform { isVisible ->
                if (isVisible) {
                    emit(ProfileState.Shown(profile))
                } else {
                    emit(ProfileState.Hidden)
                }
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
}
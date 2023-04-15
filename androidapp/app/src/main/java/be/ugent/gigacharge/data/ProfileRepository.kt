package be.ugent.gigacharge.data

import be.ugent.gigacharge.data.local.models.Profile
import be.ugent.gigacharge.data.local.models.ProfileState
import be.ugent.gigacharge.model.service.QueueService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(queueService: QueueService) {
    private var isProfileVisible: Boolean = false
    private var profile: Profile? = Profile("MobilityPlus", "1234 - 5678", "Roularta")

    fun getProfile(): Flow<ProfileState> = callbackFlow {
        if (isProfileVisible) {
            profile?.let { trySend(ProfileState.Shown(it)) }
        } else {
            trySend(ProfileState.Hidden)
        }
        awaitClose()
    }

    fun toggleProfile() {
        isProfileVisible = !isProfileVisible
    }

    fun saveProfile(profile: Profile) {
        this.profile = profile
    }

    fun getProviders(): List<String> {
        return listOf("MobilityPlus", "BlueCorner")
    }

    fun getCompanies(): List<String> {
        return listOf("Roularta", "UGent")
    }
}
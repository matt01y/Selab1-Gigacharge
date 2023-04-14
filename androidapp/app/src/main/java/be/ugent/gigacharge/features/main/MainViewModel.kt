package be.ugent.gigacharge.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.data.local.models.Profile
import be.ugent.gigacharge.data.local.models.Queue
import be.ugent.gigacharge.domain.JoinLeaveQueueUseCase
import be.ugent.gigacharge.features.ProfileUiState
import be.ugent.gigacharge.features.QueueUiState
import be.ugent.gigacharge.features.LocationUiState
import be.ugent.gigacharge.model.location.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val joinLeaveQueueUseCase: JoinLeaveQueueUseCase
): ViewModel() {
    // Profile
    private var isVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isVisibleState: StateFlow<Boolean> = isVisible.asStateFlow()
    private var profile: MutableStateFlow<Profile> = MutableStateFlow(Profile("MobilityPlus", "1234 - 5678", "Roularta"))
    val profileUiState: StateFlow<ProfileUiState> = profile.map{ ProfileUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, ProfileUiState.Loading)
    // Queue
    private var queue: MutableStateFlow<Queue> = MutableStateFlow(Queue(listOf(), "Roularta Roeselare"))
    val queueUiState: StateFlow<QueueUiState> = queue.map{ QueueUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, QueueUiState.Loading)
    // Location
    private var location: MutableStateFlow<LocationUiState> = MutableStateFlow(LocationUiState.Loading)
    val locationUiState: StateFlow<LocationUiState> = location.asStateFlow()

    fun toggleProfile() {
        isVisible.value = !isVisible.value
    }

    fun saveProfile(provider: String, card: String, company: String) {
        profile.value = Profile(provider, card, company)
    }

    fun getProviders(): List<String> {
        return listOf("MobilityPlus", "BlueCorner")
    }

    fun getCompanies(): List<String> {
        return listOf("Roularta", "UGent")
    }

    fun joinLeaveQueue(location: Location) = viewModelScope.launch {
        joinLeaveQueueUseCase(location)
    }

    fun toggleFavorite(loc: Location) {

    }
}
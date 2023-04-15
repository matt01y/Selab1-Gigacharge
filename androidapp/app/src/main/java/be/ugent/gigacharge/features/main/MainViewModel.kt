package be.ugent.gigacharge.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.data.local.models.Profile
import be.ugent.gigacharge.data.local.models.Queue
import be.ugent.gigacharge.domain.queue.JoinLeaveQueueUseCase
import be.ugent.gigacharge.domain.profile.*
import be.ugent.gigacharge.features.ProfileUiState
import be.ugent.gigacharge.features.QueueUiState
import be.ugent.gigacharge.features.LocationUiState
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.location.QueueState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    // Profile
    getProfileUseCase: GetProfileUseCase,
    private val toggleProfileUseCase: ToggleProfileUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val getProvidersUseCase: GetProvidersUseCase,
    private val getCompaniesUseCase: GetCompaniesUseCase,
    // Queue
    private val joinLeaveQueueUseCase: JoinLeaveQueueUseCase
    // Location
    // ...
): ViewModel() {
    // Profile
    val profileUiState: StateFlow<ProfileUiState> = getProfileUseCase().map{ProfileUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, ProfileUiState.Loading)
    // Queue
    private var queue: MutableStateFlow<Queue> = MutableStateFlow(Queue(listOf(), "Roularta Roeselare"))
    val queueUiState: StateFlow<QueueUiState> = queue.map{ QueueUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, QueueUiState.Loading)
    // Location
    private var location: MutableStateFlow<LocationUiState> = MutableStateFlow(LocationUiState.Success(Location("id", "naam", QueueState.NotJoined, 0)))
    val locationUiState: StateFlow<LocationUiState> = location.asStateFlow()

    fun toggleProfile() {
        toggleProfileUseCase()
    }

    fun saveProfile(provider: String, card: String, company: String) {
        saveProfileUseCase(Profile(provider, card, company))
    }

    fun getProviders(): List<String> {
        return getProvidersUseCase()
    }

    fun getCompanies(): List<String> {
        return getCompaniesUseCase()
    }

    fun joinLeaveQueue(location: Location) = viewModelScope.launch {
        joinLeaveQueueUseCase(location)
    }

    fun toggleFavorite(location: Location) {

    }
}
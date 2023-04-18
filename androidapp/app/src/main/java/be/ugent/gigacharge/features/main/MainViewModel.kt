package be.ugent.gigacharge.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.data.local.models.Profile
import be.ugent.gigacharge.domain.location.GetLocationUseCase
import be.ugent.gigacharge.domain.location.ToggleFavoriteLocationUseCase
import be.ugent.gigacharge.domain.queue.JoinLeaveQueueUseCase
import be.ugent.gigacharge.domain.profile.*
import be.ugent.gigacharge.domain.queue.GetQueueUseCase
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
    // Profile
    getProfileUseCase: GetProfileUseCase,
    private val toggleProfileUseCase: ToggleProfileUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val getProvidersUseCase: GetProvidersUseCase,
    private val getCompaniesUseCase: GetCompaniesUseCase,
    // Queue
    getQueueUseCase: GetQueueUseCase,
    private val joinLeaveQueueUseCase: JoinLeaveQueueUseCase,
    // Location
    getLocationUseCase: GetLocationUseCase,
    private val toggleFavoriteLocationUseCase: ToggleFavoriteLocationUseCase
): ViewModel() {
    val profileUiState: StateFlow<ProfileUiState> = getProfileUseCase().map{ProfileUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, ProfileUiState.Loading)
    val queueUiState: StateFlow<QueueUiState> = getQueueUseCase().map{QueueUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, QueueUiState.Loading)
    val locationUiState: StateFlow<LocationUiState> = getLocationUseCase().map{LocationUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, LocationUiState.Loading)

    fun toggleProfile() {
        toggleProfileUseCase()
    }

    fun saveProfile(provider: String, card: String, company: String, visible: Boolean) {
        saveProfileUseCase(Profile(provider, card, company, visible))
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
        toggleFavoriteLocationUseCase(location)
    }
}
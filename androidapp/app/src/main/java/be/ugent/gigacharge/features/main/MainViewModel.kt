package be.ugent.gigacharge.features.main

import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.data.local.models.Profile
import be.ugent.gigacharge.domain.location.GetLocationUseCase
import be.ugent.gigacharge.domain.location.ToggleFavoriteLocationUseCase
import be.ugent.gigacharge.domain.profile.*
import be.ugent.gigacharge.domain.queue.GetQueueUseCase
import be.ugent.gigacharge.domain.queue.JoinLeaveQueueUseCase
import be.ugent.gigacharge.features.LocationUiState
import be.ugent.gigacharge.features.ProfileUiState
import be.ugent.gigacharge.features.QueueUiState
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.service.LogService
import be.ugent.gigacharge.model.service.QueueService
import be.ugent.gigacharge.screens.GigaChargeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    // Profile
    getProfileUseCase: GetProfileUseCase,
    private val toggleProfileUseCase: ToggleProfileUseCase,
    private val saveProfileUseCase: SaveProfileUseCase,
    private val isValidCardNumberUseCase: IsValidCardNumberUseCase,
    // Queue
    getQueueUseCase: GetQueueUseCase,
    private val joinLeaveQueueUseCase: JoinLeaveQueueUseCase,
    // Location
    getLocationUseCase: GetLocationUseCase,
    private val toggleFavoriteLocationUseCase: ToggleFavoriteLocationUseCase,
    private val queueService: QueueService, logService: LogService
) : GigaChargeViewModel(logService) {
    val profileUiState: StateFlow<ProfileUiState> =
        getProfileUseCase().map { ProfileUiState.Success(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, ProfileUiState.Loading)
    val queueUiState: StateFlow<QueueUiState> = getQueueUseCase().map { QueueUiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, QueueUiState.Loading)
    val locationUiState: StateFlow<LocationUiState> =
        getLocationUseCase().map { LocationUiState.Success(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, LocationUiState.Loading)

    fun toggleProfile() {
        toggleProfileUseCase()
    }

    fun saveProfile(card: String, visible: Boolean) {
        saveProfileUseCase(Profile(card, visible))
    }

    fun joinLeaveQueue(location: Location) = viewModelScope.launch {
        joinLeaveQueueUseCase(location)
    }

    fun toggleFavorite(location: Location) {
        toggleFavoriteLocationUseCase(location)
    }

    fun updateLocation() {
        launchCatching {
            queueService.updateLocations()
        }
    }

    fun isValidCardNumber(cardNumber: String): Boolean {
        return isValidCardNumberUseCase(cardNumber)
    }
}
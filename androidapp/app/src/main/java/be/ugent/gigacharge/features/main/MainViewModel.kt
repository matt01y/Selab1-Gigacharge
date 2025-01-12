package be.ugent.gigacharge.features.main

import android.util.Log
import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.domain.location.GetLocationUseCase
import be.ugent.gigacharge.domain.location.UpdateCurrentLocationUseCase
import be.ugent.gigacharge.domain.profile.DeleteProfileUseCase
import be.ugent.gigacharge.domain.profile.GetProfileUseCase
import be.ugent.gigacharge.domain.profile.ToggleProfileUseCase
import be.ugent.gigacharge.domain.queue.JoinLeaveQueueUseCase
import be.ugent.gigacharge.features.LocationUiState
import be.ugent.gigacharge.features.ProfileUiState
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.service.LogService
import be.ugent.gigacharge.screens.GigaChargeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    // Profile
    getProfileUseCase: GetProfileUseCase,
    private val toggleProfileUseCase: ToggleProfileUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase,
    // Queue
    private val joinLeaveQueueUseCase: JoinLeaveQueueUseCase,
    // Location
    getLocationUseCase: GetLocationUseCase,
    private val updateCurrentLocationUseCase: UpdateCurrentLocationUseCase,
    logService: LogService
) : GigaChargeViewModel(logService) {
    val profileUiState: StateFlow<ProfileUiState> =
        getProfileUseCase().map { ProfileUiState.Success(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, ProfileUiState.Loading)
    val locationUiState: StateFlow<LocationUiState> =
        getLocationUseCase().map { LocationUiState.Success(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, LocationUiState.Loading)

    init {
        launchCatching {
            while (true) {
                delay(20000)
                launchCatching {
                    Log.i("main", "YIPPIE!")
                    updateCurrentLocationUseCase()
                }
            }
        }
    }

    fun refreshButtonPressed(){
        launchCatching {
            updateCurrentLocationUseCase()
        }
    }

    fun toggleProfile() {
        toggleProfileUseCase()
    }

    fun deleteProfile() {
        deleteProfileUseCase()
    }

    fun joinLeaveQueue(location: Location) = viewModelScope.launch {
        joinLeaveQueueUseCase(location)
    }
}
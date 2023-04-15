package be.ugent.gigacharge.features.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.domain.location.GetLocationsUseCase
import be.ugent.gigacharge.domain.location.SetLocationUseCase
import be.ugent.gigacharge.features.LocationUiState
import be.ugent.gigacharge.features.LocationsUiState
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.service.QueueService
//import be.ugent.gigacharge.data.local.models.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    getLocationsUseCase: GetLocationsUseCase,
    private val setLocationUseCase: SetLocationUseCase
): ViewModel() {
    val locationsUiState: StateFlow<LocationsUiState> = getLocationsUseCase().map{LocationsUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, LocationsUiState.Loading)

    fun setLocation(location: Location) {
        setLocationUseCase(location)
    }

    fun toggleFavorite(loc: Location) {

    }
}
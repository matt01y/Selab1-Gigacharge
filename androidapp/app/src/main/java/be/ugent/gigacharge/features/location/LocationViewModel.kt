package be.ugent.gigacharge.features.location

//import be.ugent.gigacharge.data.local.models.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.domain.location.GetLocationsUseCase
import be.ugent.gigacharge.domain.location.SetLocationUseCase
import be.ugent.gigacharge.features.LocationsUiState
import be.ugent.gigacharge.model.location.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    getLocationsUseCase: GetLocationsUseCase,
    private val setLocationUseCase: SetLocationUseCase
) : ViewModel() {
    val locationsUiState: StateFlow<LocationsUiState> =
        getLocationsUseCase().map { LocationsUiState.Success(it) }
            .stateIn(viewModelScope, SharingStarted.Eagerly, LocationsUiState.Loading)

    fun setLocation(location: Location) {
        setLocationUseCase(location)
    }
}
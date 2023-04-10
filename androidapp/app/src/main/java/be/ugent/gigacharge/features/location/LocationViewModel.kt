package be.ugent.gigacharge.features.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.data.local.models.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    // USECASES
): ViewModel() {
    private var location: MutableStateFlow<Location> = MutableStateFlow(Location("Roularta Roeselare", true))
    val locationUiState: StateFlow<LocationUiState> = location.map{LocationUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, LocationUiState.Loading)

    private var locations: MutableStateFlow<List<Location>> = MutableStateFlow(listOf(
        Location("UGent", false),
        Location("UZ Gent", true)
    ))
    val locationsUiState: StateFlow<LocationsUiState> = locations.map{LocationsUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, LocationsUiState.Loading)

    fun setLocation(loc: Location) {
        val temp: MutableList<Location> = locations.value.toMutableList()
        temp.add(location.value)
        temp.remove(loc)
        locations.value = temp.toList()
        location.value = loc
    }

    fun toggleFavorite(loc: Location) {
        // Toggle if it was the selected location
        if (location.value.name == loc.name) {
            location.value = Location(location.value.name, !location.value.favorite)
        }
        // Toggle a location in the list
        val temp: MutableList<Location> = mutableListOf()
        locations.value.forEach { l: Location ->
            if (l.name == loc.name) {
                temp.add(Location(l.name, !l.favorite))
            } else {
                temp.add(l)
            }
        }
        locations.value = temp
    }
}
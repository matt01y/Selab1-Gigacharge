package be.ugent.gigacharge.features.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.domain.GetLocationsUseCase
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.location.QueueState
//import be.ugent.gigacharge.data.local.models.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    getLocationsUseCase: GetLocationsUseCase
): ViewModel() {
    // private var location: MutableStateFlow<Location> = MutableStateFlow(Location("", "Roularta Rouselare", QueueState.NotJoined, 0))
    // Jarne versie:
    //private var location: MutableStateFlow<Location> = MutableStateFlow(Location("Roularta Roeselare", true))
    //val locationUiState: StateFlow<LocationUiState> = location.map{LocationUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, LocationUiState.Loading)

    val locationsUiState: StateFlow<LocationsUiState> = getLocationsUseCase().map{LocationsUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, LocationsUiState.Loading)
    val locationUiState: StateFlow<LocationUiState> = getLocationsUseCase().map{
            if (it.getOrNull(0) == null) LocationUiState.Loading else LocationUiState.Success(it[0])
        }.stateIn(viewModelScope, SharingStarted.Eagerly, LocationUiState.Loading)

    fun setLocation(loc: Location) {
//        val temp: MutableList<Location> = locations.value.toMutableList()
//        temp.add(location.value)
//        temp.remove(loc)
//        locations.value = temp.toList()
//        location.value = loc
    }

    fun toggleFavorite(loc: Location) {
//        // Toggle if it was the selected location
//        if (location.value.name == loc.name) {
//            location.value = Location("", location.value.name, QueueState.NotJoined, 0)
//            //Jarne versie:
//            //location.value = Location(location.value.name, !location.value.favorite)
//        }
//        // Toggle a location in the list
//        val temp: MutableList<Location> = mutableListOf()
//        locations.value.forEach { l: Location ->
//            if (l.name == loc.name) {
//                temp.add(Location("", l.name, QueueState.NotJoined, 0))
//                // Jarne versie:
//                //temp.add(Location(l.name, !l.favorite))
//            } else {
//                temp.add(l)
//            }
//        }
//        locations.value = temp
    }
}
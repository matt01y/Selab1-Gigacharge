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
    private val setLocationUseCase: SetLocationUseCase,
    private val queueService: QueueService
): ViewModel() {
    val locationsUiState: StateFlow<LocationsUiState> = getLocationsUseCase().map{ LocationsUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, LocationsUiState.Loading)

    private val myFlow: MutableStateFlow<LocationUiState> = MutableStateFlow(LocationUiState.Loading)
    private val otherFlow: StateFlow<LocationUiState> = getLocationsUseCase().map{
            if (it.getOrNull(0) == null) LocationUiState.Loading else LocationUiState.Success(it[0])
        }.stateIn(viewModelScope, SharingStarted.Eagerly, LocationUiState.Loading)
    private val location: MutableStateFlow<LocationUiState> = MutableStateFlow(LocationUiState.Loading)
    val locationUiState: StateFlow<LocationUiState> = location.asStateFlow()

    init {
        myFlow.combine(otherFlow) { myFlow, otherFlow ->
            println("MyFlow & OtherFlow: ")
            println(myFlow)
            println(otherFlow)
            var temp = myFlow
            if (myFlow == LocationUiState.Loading && otherFlow is LocationUiState.Success) {
                temp = otherFlow
            }
            location.value = temp
            print("New locationState: ")
            println(location.value)
        }.launchIn(viewModelScope)
    }

    fun setLocation(loc: Location) {
        viewModelScope.launch {
            var l = queueService.updateLocation(loc)
            var temp: LocationUiState = LocationUiState.Loading
            if (l != null) {
                temp = LocationUiState.Success(l)
            }
            myFlow.value = temp
        }
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
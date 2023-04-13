package be.ugent.gigacharge.features.location

import be.ugent.gigacharge.model.location.Location

//import be.ugent.gigacharge.data.local.models.Location

sealed interface LocationUiState {
    object Loading: LocationUiState
    data class Success(val location: Location) : LocationUiState
}
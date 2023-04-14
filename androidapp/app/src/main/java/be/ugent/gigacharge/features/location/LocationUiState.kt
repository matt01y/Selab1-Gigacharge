package be.ugent.gigacharge.features.location

import be.ugent.gigacharge.model.location.Location

sealed interface LocationUiState {
    object Loading: LocationUiState
    data class Success(val location: Location) : LocationUiState
}
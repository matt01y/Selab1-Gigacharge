package be.ugent.gigacharge.features.location

import be.ugent.gigacharge.data.local.models.Location

sealed interface LocationsUiState {
    object Loading: LocationsUiState
    data class Success(val locations: List<Location>): LocationsUiState
}
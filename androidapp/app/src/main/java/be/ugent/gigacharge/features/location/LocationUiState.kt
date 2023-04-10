package be.ugent.gigacharge.features.location

import androidx.compose.runtime.mutableStateListOf
import be.ugent.gigacharge.model.location.Location

data class LocationUiState(
    val locations: MutableList<Location> = mutableStateListOf()
)

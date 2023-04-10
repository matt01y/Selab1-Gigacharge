package be.ugent.gigacharge.features.location

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList //TODO: investigate dit
import be.ugent.gigacharge.model.Location

data class LocationUiState(
    val locations: MutableList<Location> = mutableStateListOf()
)

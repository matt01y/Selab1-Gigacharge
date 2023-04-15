package be.ugent.gigacharge.features

import be.ugent.gigacharge.data.local.models.Profile

sealed interface ProfileUiState {
    object Loading: ProfileUiState
    object Hidden: ProfileUiState
    data class Shown(val profile: Profile): ProfileUiState
}

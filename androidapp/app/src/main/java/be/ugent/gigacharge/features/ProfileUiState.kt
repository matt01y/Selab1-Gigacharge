package be.ugent.gigacharge.features

import be.ugent.gigacharge.data.local.models.ProfileState

sealed interface ProfileUiState {
    object Loading: ProfileUiState
    data class Success(val profile: ProfileState): ProfileUiState
}

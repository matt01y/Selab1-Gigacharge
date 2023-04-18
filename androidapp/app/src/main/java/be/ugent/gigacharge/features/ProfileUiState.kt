package be.ugent.gigacharge.features

import be.ugent.gigacharge.data.local.models.Profile

sealed interface ProfileUiState {
    object Loading: ProfileUiState
    data class Success(val profile: Profile): ProfileUiState
}

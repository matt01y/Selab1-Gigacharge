package be.ugent.gigacharge.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.data.local.models.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    // USECASES
): ViewModel() {
    private var isVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isVisibleState: StateFlow<Boolean> = isVisible.asStateFlow()
    private var profile: MutableStateFlow<Profile> = MutableStateFlow(Profile("MobilityPlus", "1234 - 5678", "Roularta"))
    val uiState: StateFlow<ProfileUiState> = profile.map{ProfileUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, ProfileUiState.Loading)

    fun toggleProfile() {
        isVisible.value = !isVisible.value
    }

    fun saveProfile(provider: String, card: String, company: String) {
        profile.value = Profile(provider, card, company)
    }

    fun getProviders(): List<String> {
        return listOf("MobilityPlus", "BlueCorner")
    }

    fun getCompanies(): List<String> {
        return listOf("Roularta", "UGent")
    }
}
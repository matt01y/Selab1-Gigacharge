package be.ugent.gigacharge.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    // USECASES
): ViewModel() {
    private var isVisible: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isVisibleState: StateFlow<Boolean> = isVisible.asStateFlow()
    val uiState: StateFlow<ProfileUiState> = emptyFlow<ProfileUiState>().stateIn(viewModelScope, SharingStarted.Eagerly, ProfileUiState.Loading)

    fun toggleProfile() {
        isVisible.value = !isVisible.value
    }
}
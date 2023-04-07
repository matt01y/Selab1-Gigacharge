package be.ugent.gigacharge.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.data.local.models.Queue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    // USECASES
): ViewModel() {
    // Dummy, USECASE needed
    private val q = Queue(listOf(), "Roularta Roeselare")
    val uiState: StateFlow<QueueUiState> = flowOf(QueueUiState.Success(q)).stateIn(viewModelScope, SharingStarted.Eagerly, QueueUiState.Loading)

    fun joinLeaveQueue() {

    }
}
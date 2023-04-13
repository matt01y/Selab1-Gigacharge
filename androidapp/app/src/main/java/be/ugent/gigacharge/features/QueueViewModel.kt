package be.ugent.gigacharge.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.data.local.models.Queue
import be.ugent.gigacharge.domain.JoinLeaveQueueUseCase
import be.ugent.gigacharge.model.location.Location
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    private val joinLeaveQueueUseCase: JoinLeaveQueueUseCase
): ViewModel() {
    // Dummy, USECASE needed
    private var q: MutableStateFlow<Queue> = MutableStateFlow(Queue(listOf(), "Roularta Roeselare"))
    val uiState: StateFlow<QueueUiState> = q.map{QueueUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, QueueUiState.Loading)

    fun joinLeaveQueue(location: Location) = viewModelScope.launch {
        joinLeaveQueueUseCase(location)
    }
}
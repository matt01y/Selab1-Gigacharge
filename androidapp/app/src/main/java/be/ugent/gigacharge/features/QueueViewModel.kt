package be.ugent.gigacharge.features

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.data.local.models.Queue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(
    // USECASES
): ViewModel() {
    // Dummy, USECASE needed
    private var q: MutableStateFlow<Queue> = MutableStateFlow(Queue(listOf(), "Roularta Roeselare"))
    val uiState: StateFlow<QueueUiState> = q.map{QueueUiState.Success(it)}.stateIn(viewModelScope, SharingStarted.Eagerly, QueueUiState.Loading)

    fun joinLeaveQueue(cardNumber:String) {
        if (q.value.queue.contains(cardNumber)) {
            q.value = Queue(listOf(), "Roularta Roeselare")
        } else {
            q.value = Queue(listOf("1234 - 5678"), "Roularta Roeselare")
        }
    }
}
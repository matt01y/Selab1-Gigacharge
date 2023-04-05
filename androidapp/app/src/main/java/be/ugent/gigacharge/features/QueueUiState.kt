package be.ugent.gigacharge.features

import be.ugent.gigacharge.data.local.models.Queue

sealed interface QueueUiState {
    object Loading : QueueUiState
    data class Success(val queue: Queue) : QueueUiState
}

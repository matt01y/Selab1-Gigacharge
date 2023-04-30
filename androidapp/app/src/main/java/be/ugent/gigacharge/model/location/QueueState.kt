package be.ugent.gigacharge.model.location

sealed interface QueueState {
    object NotJoined : QueueState

    object Charging : QueueState

    data class Joined(val myPosition: Long) : QueueState
}
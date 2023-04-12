package be.ugent.gigacharge.model.location

sealed interface QueueState {
    object NotJoined : QueueState
    data class Joined(val myPosition : Long) : QueueState
}
package be.ugent.gigacharge.model.location

import be.ugent.gigacharge.model.location.charger.Charger

sealed interface QueueState {
    object NotJoined : QueueState

    object Charging : QueueState

    object Assigned : QueueState

    //data class Assigned(val charger: Charger) : QueueState

    data class Joined(val myPosition: Long) : QueueState
}
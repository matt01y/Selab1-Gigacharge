package be.ugent.gigacharge.model.location

import be.ugent.gigacharge.model.location.charger.Charger
import java.util.*

sealed interface QueueState {
    object NotJoined : QueueState

    object Charging : QueueState

    data class Assigned(val expiretime: Date, val charger: Charger) : QueueState

    //data class Assigned(val charger: Charger) : QueueState

    data class Joined(val myPosition: Long) : QueueState
}
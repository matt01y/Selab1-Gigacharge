package be.ugent.gigacharge.model.location

data class Charger (
    val status: ChargerStatus,
    val description : String,
    val user : UserField,
    val usertype: UserType,
    val assignedJoin: String
)
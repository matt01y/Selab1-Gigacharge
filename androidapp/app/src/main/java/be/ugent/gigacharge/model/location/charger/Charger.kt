package be.ugent.gigacharge.model.location.charger

data class Charger(
    val description: String,
    val id: String,
    val status: ChargerStatus,
    val user: UserField,
    val usertype: UserType
) {
}
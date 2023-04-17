package be.ugent.gigacharge.model.location.charger

data class Charger (
    val status: ChargerStatus,
    val description : String,
    val user : UserField,
    val usertype: UserType,
    val assignedJoin: String
) {
    constructor() : this(ChargerStatus.OUT,
                        "",
                        UserField.Null,
                        UserType.NONUSER,
                        ""
    )
}
package be.ugent.gigacharge.model.location

sealed interface UserField {
    object Null : UserField

    data class UserID(val id : String) : UserField

    data class CardNumber(val cardnum : String) : UserField
}
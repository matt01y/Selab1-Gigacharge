package be.ugent.gigacharge.screens.register

data class RegisterUiState(
    val cardnumber: String = "",
    val statusmessage: String = "Normaal is je firebase user nu aangemaakt maar niet enabled"
)

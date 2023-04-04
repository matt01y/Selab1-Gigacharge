package be.ugent.gigacharge.screens.Register

data class RegisterUiState(
    val cardnumber: String = "",
    val statusmessage: String = "Normaal is je firebase user nu aangemaakt maar niet enabled"
)

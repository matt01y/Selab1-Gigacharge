package be.ugent.gigacharge.features.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoginRoute(onLoginClick : () -> Unit) {
    LoginScreen(onLoginClick)
}

@Composable
fun LoginScreen(onLoginClick : () -> Unit) {
    Column() {
        Text("login view")
        Button(onClick = onLoginClick) {
            Text("login")
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen({})
}
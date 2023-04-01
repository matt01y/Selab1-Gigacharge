package be.ugent.gigacharge.features

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

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
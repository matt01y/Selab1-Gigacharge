package be.ugent.gigacharge.features

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable


@Composable
fun MainRoute(onLocationSelectClick : () -> Unit) {
    MainScreen(onLocationSelectClick)
}

@Composable
fun MainScreen(onLocationSelectClick : () -> Unit) {
    Column() {
        Text("laadpalen view")
        Button(onClick = onLocationSelectClick) {
            Text("select location")
        }
    }

}
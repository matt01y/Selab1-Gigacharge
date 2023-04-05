package be.ugent.gigacharge.features

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import be.ugent.gigacharge.common.composable.LocationButtonComposable
import be.ugent.gigacharge.common.composable.MainHeaderComposable
import be.ugent.gigacharge.common.composable.QueueButtonComposable


@Composable
fun MainRoute(onLocationSelectClick : () -> Unit) {
    MainScreen(onLocationSelectClick, "currentLocation")
}

@Composable
fun MainScreen(onLocationSelectClick: () -> Unit, currentLocation: String) {
    Scaffold(
        topBar = { MainHeaderComposable {
            LocationButtonComposable(
                chooseLocation = onLocationSelectClick,
                currentLocation = currentLocation
            )
        }},
        bottomBar = { QueueButtonComposable({}, true)}
    ) {
        paddingValues -> Column(Modifier.padding(paddingValues)) {}
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen({}, "Roularta Roeselare")
}

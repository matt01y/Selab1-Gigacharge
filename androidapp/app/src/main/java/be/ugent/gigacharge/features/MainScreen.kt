package be.ugent.gigacharge.features

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import be.ugent.gigacharge.common.composable.LocationButtonComposable
import be.ugent.gigacharge.common.composable.MainHeaderComposable
import be.ugent.gigacharge.common.composable.QueueButtonComposable
import be.ugent.gigacharge.common.composable.QueueInfoComposable
import be.ugent.gigacharge.ui.theme.GigaChargeTheme


@Composable
fun MainRoute(onLocationSelectClick : () -> Unit) {
    MainScreen(onLocationSelectClick, "currentLocation", {}, true, 5)
}

@Composable
fun MainScreen(
    onLocationSelectClick: () -> Unit,
    currentLocation: String,
    queueButtonFunction: () -> Unit,
    inQueue: Boolean,
    queueSize: Int
) {
    Scaffold(
        topBar = { MainHeaderComposable {
            LocationButtonComposable(
                chooseLocation = onLocationSelectClick,
                currentLocation = currentLocation
            )
        }},
        bottomBar = { QueueButtonComposable(queueButtonFunction, inQueue)}
    ) {
        // Playing safe
        paddingValues -> LazyColumn(Modifier.padding(paddingValues)) {
           item {
               QueueInfoComposable(queueSize)
           }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    GigaChargeTheme {
        MainScreen({}, "Roularta Roeselare", {}, true, 5)
    }
}

package be.ugent.gigacharge.features

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import be.ugent.gigacharge.common.composable.LocationButtonComposable
import be.ugent.gigacharge.common.composable.MainHeaderComposable
import be.ugent.gigacharge.common.composable.QueueButtonComposable
import be.ugent.gigacharge.common.composable.QueueInfoComposable
import be.ugent.gigacharge.ui.theme.GigaChargeTheme


@Composable
fun MainRoute(onLocationSelectClick : () -> Unit, vm: QueueViewModel) {
    val uiState by vm.uiState.collectAsState()

    MainScreen(
        onLocationSelectClick,
        {},
        { vm.joinLeaveQueue() },
        uiState
    )
}

@Composable
fun MainScreen(
    onLocationSelectClick: () -> Unit,
    onProfileSelectClick: () -> Unit,
    onQueueButtonSelectClick: () -> Unit,
    uiState: QueueUiState
) {
    Scaffold(
        topBar = {
            MainHeaderComposable(
                onProfileSelectClick
            ) {
                if (uiState is QueueUiState.Success) {
                    val queue = uiState.queue
                    LocationButtonComposable(
                        chooseLocation = onLocationSelectClick,
                        currentLocation = queue.location
                    )
                }
            }
        },
        bottomBar = {
            if (uiState is QueueUiState.Success) {
                val queue = uiState.queue.queue
                //TODO Change 0 to real user
                QueueButtonComposable(onQueueButtonSelectClick, queue.contains(0))
            }
        }
    ) {
        // Playing safe
        paddingValues -> Column(Modifier.padding(paddingValues)) {
            when (uiState) {
                QueueUiState.Loading -> {
                    Text("Loading ...")
                }
                is QueueUiState.Success -> {
                    val queue = uiState.queue;

                    LazyColumn {
                        item {
                            QueueInfoComposable(queue.queue.size)
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    GigaChargeTheme {
        MainRoute({}, hiltViewModel())
    }
}

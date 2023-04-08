package be.ugent.gigacharge.features.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import be.ugent.gigacharge.common.composable.*
import be.ugent.gigacharge.features.ProfileUiState
import be.ugent.gigacharge.features.ProfileViewModel
import be.ugent.gigacharge.features.QueueUiState
import be.ugent.gigacharge.features.QueueViewModel
import be.ugent.gigacharge.ui.theme.GigaChargeTheme


@Composable
fun MainRoute(onLocationSelectClick : () -> Unit, queueVM: QueueViewModel, profileVM: ProfileViewModel) {
    val queueUiState by queueVM.uiState.collectAsState()
    val profileUiState by profileVM.uiState.collectAsState()
    val isProfileVisible by profileVM.isVisibleState.collectAsState()

    MainScreen(
        onLocationSelectClick,
        { profileVM.toggleProfile() },
        { queueVM.joinLeaveQueue() },
        queueUiState,
        profileUiState,
        isProfileVisible
    )
}

@Composable
fun MainScreen(
    onLocationSelectClick: () -> Unit,
    onProfileSelectClick: () -> Unit,
    joinLeaveQueue: () -> Unit,
    queueUiState: QueueUiState,
    profileUiState: ProfileUiState,
    isProfileVisible: Boolean
) {
    Scaffold(
        topBar = {
            MainHeaderComposable(
                onProfileSelectClick
            ) {
                if (isProfileVisible) {
                    ProfileFormComposable(
                        provider = "",
                        providers = listOf(),
                        updateProvider = {},
                        cardNumber = "",
                        updateCardNumber = {},
                        company = "",
                        companies = listOf(),
                        updateCompany = {},
                        cancel = {},
                        saveProfile = {_:String, _:String, _:String ->}
                    )
                } else {
                    if (queueUiState is QueueUiState.Success) {
                        val queue = queueUiState.queue
                        LocationButtonComposable(
                            chooseLocation = onLocationSelectClick,
                            currentLocation = queue.location
                        )
                    }
                }
            }
        },
        bottomBar = {
            Box(Modifier.height(IntrinsicSize.Max)) {
                // BottomBar
                if (queueUiState is QueueUiState.Success) {
                    val queue = queueUiState.queue.queue
                    //TODO Change 0 to real user
                    QueueButtonComposable(
                        joinLeaveQueue,
                        queue.contains(0)
                    )
                }
                // Overlay
                if (isProfileVisible) {
                    Overlay()
                }
            }
        }
    ) {
        paddingValues -> Column(Modifier.padding(paddingValues)) {
            Box() {
                // Home screen
                when (queueUiState) {
                    QueueUiState.Loading -> {
                        Text("Loading ...")
                    }
                    is QueueUiState.Success -> {
                        val queue = queueUiState.queue;

                        LazyColumn {
                            item {
                                QueueInfoComposable(queue.queue.size)
                            }
                        }
                    }
                }
                // Overlay when needed
                if (isProfileVisible) {
                    Overlay()
                }
            }
        }
    }
}

@Composable
fun Overlay() {
    Column(
        Modifier
            .fillMaxSize()
            .clip(RectangleShape)
            .background(Color(0.0F, 0.0F, 0.0F, 0.5F))
    ) {}
}

@Preview
@Composable
fun MainScreenPreview() {
    GigaChargeTheme {
        MainRoute({}, hiltViewModel(), hiltViewModel())
    }
}

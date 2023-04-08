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
        queueUiState,
        profileUiState,
        {n:String -> queueVM.joinLeaveQueue(n) },
        isProfileVisible,
        {p:String,n:String,c:String -> profileVM.saveProfile(p,n,c) },
        profileVM.getProviders(),
        profileVM.getCompanies()
    )
}

@Composable
fun MainScreen(
    // Navigation function
    onLocationSelectClick: () -> Unit,
    onProfileSelectClick: () -> Unit,
    // State
    queueUiState: QueueUiState,
    profileUiState: ProfileUiState,
    // Queue
    joinLeaveQueue: (String) -> Unit,
    // Profile
    isProfileVisible: Boolean,
    saveProfile: (String,String,String) -> Unit,
    providers : List<String>,
    companies : List<String>
) {
    Scaffold(
        topBar = {
            MainHeaderComposable(
                onProfileSelectClick
            ) {
                if (isProfileVisible) {
                    if (profileUiState is ProfileUiState.Success) {
                        val profile = profileUiState.profile
                        ProfileFormComposable(
                            provider = profile.provider,
                            providers = providers,
                            cardNumber = profile.cardNumber,
                            company = profile.company,
                            companies = companies,
                            cancel = onProfileSelectClick,
                            saveProfile = saveProfile
                        )
                    }
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
                    val profile = (profileUiState as ProfileUiState.Success).profile
                    QueueButtonComposable(
                        { joinLeaveQueue(profile.cardNumber) },
                        queue.contains(profile.cardNumber)
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

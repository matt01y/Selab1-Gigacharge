package be.ugent.gigacharge.features.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import be.ugent.gigacharge.common.composable.*
//import be.ugent.gigacharge.data.local.models.Location
import be.ugent.gigacharge.features.ProfileUiState
import be.ugent.gigacharge.features.QueueUiState
import be.ugent.gigacharge.features.LocationUiState
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.ui.theme.GigaChargeTheme


@Composable
fun MainRoute(onLocationSelectClick : () -> Unit, viewModel: MainViewModel) {
    val profileUiState by viewModel.profileUiState.collectAsState()
    val queueUiState by viewModel.queueUiState.collectAsState()
    val locationUiState by viewModel.locationUiState.collectAsState()

    MainScreen(
        // Navigation function
        onLocationSelectClick,
        { viewModel.toggleProfile() },
        // State
        queueUiState,
        profileUiState,
        locationUiState,
        // Queue
        {l:Location -> viewModel.joinLeaveQueue(l) },
        // Profile
        {p:String,n:String,c:String -> viewModel.saveProfile(p,n,c) },
        viewModel.getProviders(),
        viewModel.getCompanies(),
        // Location
        {l: Location -> viewModel.toggleFavorite(l)}
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
    locationUiState: LocationUiState,
    // Queue
    joinLeaveQueue: (Location) -> Unit,
    // Profile
    saveProfile: (String,String,String) -> Unit,
    providers : List<String>,
    companies : List<String>,
    // Location
    toggleFavorite: (Location) -> Unit
) {
    Scaffold(
        topBar = {
            MainHeaderComposable(
                onProfileSelectClick
            ) {
                when (profileUiState) {
                    // Render selectLocationButton
                    ProfileUiState.Hidden -> {
                        when (locationUiState) {
                            LocationUiState.Loading -> LoadingComposable(textColor = MaterialTheme.colors.onPrimary)
                            is LocationUiState.Success -> {
                                val location = locationUiState.location
                                LocationButtonComposable(
                                    onLocationSelectClick,
                                    { toggleFavorite(location) },
                                    location,
                                    true
                                )
                            }
                        }
                    }
                    // Data can be shown, but not yet available
                    ProfileUiState.Loading -> LoadingComposable(textColor = MaterialTheme.colors.onPrimary)
                    // Show the profile
                    is ProfileUiState.Shown -> {
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
                }
            }
        },
        bottomBar = {
            Box(Modifier.height(IntrinsicSize.Max)) {
                when (profileUiState) {
                    // Overlay bottomBar if profile is shown
                    is ProfileUiState.Shown -> Overlay()
                    // Profile is hidden or has not yet the data, render the join/leave button
                    else -> {
                        if (locationUiState is LocationUiState.Success) {
                            val location = locationUiState.location
                            QueueButtonComposable(
                                { joinLeaveQueue(location) },
                                true, // TODO InQueueUseCase
                            )
                        }
                    }
                }
            }
        }
    ) {
        paddingValues -> Column(Modifier.padding(paddingValues)) {
            Box {
                when (profileUiState) {
                    // Overlay the screen if the profile is shown
                    is ProfileUiState.Shown -> Overlay()
                    // Profile is loading or hidden, show the homescreen
                    else -> {
                        when (queueUiState) {
                            // Waiting on queue information
                            QueueUiState.Loading -> LoadingComposable()
                            // Show the queue information
                            is QueueUiState.Success -> {
                                val queue = queueUiState.queue;
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
        }
    }
}

@Composable
fun QueueInfoComposable(queueSize: Int) {
    Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text("Queue Information", color = MaterialTheme.colors.onBackground, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(5.dp))
                .padding(10.dp)
        ) {
            Text("In queue: $queueSize", color = MaterialTheme.colors.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QueueButtonComposable(onQueueButtonSelectClick: () -> Unit, inQueue: Boolean) {
    Row(
        Modifier
            .height(100.dp)
            .fillMaxWidth(),
        verticalAlignment= Alignment.CenterVertically
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment= Alignment.CenterHorizontally) {
            Button(
                onQueueButtonSelectClick,
                Modifier.height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant)
            ) {
                Text(if (inQueue) "Leave queue" else "Join queue", fontSize= 20.sp, fontWeight= FontWeight.Bold)
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
        MainRoute({}, hiltViewModel())
    }
}

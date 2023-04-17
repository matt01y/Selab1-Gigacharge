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
import be.ugent.gigacharge.data.local.models.ProfileState
import be.ugent.gigacharge.features.ProfileUiState
import be.ugent.gigacharge.features.QueueUiState
import be.ugent.gigacharge.features.LocationUiState
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.location.LocationStatus
import be.ugent.gigacharge.model.location.QueueState
import be.ugent.gigacharge.model.location.charger.ChargerStatus
import be.ugent.gigacharge.model.location.charger.UserField
import be.ugent.gigacharge.model.location.charger.UserType
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
                    ProfileUiState.Loading -> LoadingComposable(textColor = MaterialTheme.colors.onPrimary, text="Loading profile ...")
                    is ProfileUiState.Success -> {
                        when (profileUiState.profile) {
                            // Profile is not visible, show setLocationButton
                            ProfileState.Hidden -> {
                                when (locationUiState) {
                                    LocationUiState.Loading -> LoadingComposable(textColor = MaterialTheme.colors.onPrimary, text="Loading location ...")
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
                            // Profile is visible, show form
                            is ProfileState.Shown -> {
                                val profile = profileUiState.profile.profile
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
                }
            }
        },
        bottomBar = {
            if (!(locationUiState is LocationUiState.Success && locationUiState.location.status == LocationStatus.OPEN)) {
                Box(Modifier.height(IntrinsicSize.Max)) {
                    // Join/Leave button
                    if (locationUiState is LocationUiState.Success) {
                        val location = locationUiState.location
                        QueueButtonComposable(
                            { joinLeaveQueue(location) },
                            location.amIJoined
                        )
                    }
                }
            }
            // Overlay if profile is visible
            if (profileUiState is ProfileUiState.Success && profileUiState.profile is ProfileState.Shown) {
                Overlay()
            }
        }
    ) {
        paddingValues -> Column(Modifier.padding(paddingValues)) {
            Box {
                when (locationUiState) {
                    LocationUiState.Loading -> LoadingComposable()
                    is LocationUiState.Success -> {
                        if (locationUiState.location.status == LocationStatus.OPEN) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(30.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Vrij parkeerplaats", color = MaterialTheme.colors.onBackground, fontSize = 25.sp)
                            }
                        } else {
                            val location = locationUiState.location;
                            LazyColumn {
                                item {
                                    QueueInfoComposable(location.amountWaiting, location.queue, locationUiState, profileUiState)
                                }
                            }
                        }
                    }
                }
                // Overlay if profile is visible
                if (profileUiState is ProfileUiState.Success && profileUiState.profile is ProfileState.Shown) {
                    Overlay()
                }
            }
        }
    }
}

@Composable
fun QueueInfoAssignedComposable(
    expireTime : String
) {
    Text("you have been assigned")
    Text("your reservation expires at: " + expireTime)

}

@Composable
fun QueueInfoComposable(queueSize: Long, queueStatus: QueueState,
                        locationUiState : LocationUiState,
                        profileUiState: ProfileUiState) {
    Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text("Queue Information", color = MaterialTheme.colors.onBackground, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(5.dp))
                .padding(10.dp)
        ) {
            Text("In queue: $queueSize", color = MaterialTheme.colors.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            when (queueStatus) {
                QueueState.NotJoined -> {
                    Text("Queue position: Not joined", color = MaterialTheme.colors.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                is QueueState.Joined -> {
                    Text("Queue position: ${queueStatus.myPosition}", color = MaterialTheme.colors.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            when (locationUiState) {
                is LocationUiState.Success -> {
                    for (charger in locationUiState.location.chargers) {
                        if (charger.status == ChargerStatus.ASSIGNED) {
                            when (charger.usertype) {
                                UserType.USER -> {
                                    when (profileUiState) {
                                        ProfileUiState.Loading -> {}
                                        is ProfileUiState.Success -> {
                                            if ((charger.user as UserField.UserID).id.equals(profileUiState.profile)) {
                                                // status == assigned
                                                println("status is assigned")
                                                QueueInfoAssignedComposable(expireTime = "placeholder")
                                            }
                                        }
                                    }

                                }
                                UserType.NONUSER -> {
                                    when (profileUiState) {
                                        ProfileUiState.Loading -> {}
                                        is ProfileUiState.Success -> {
                                            when (profileUiState.profile) {
                                                ProfileState.Hidden -> {}
                                                is ProfileState.Shown -> {
                                                    if ((charger.user as UserField.CardNumber).cardnum.equals(profileUiState.profile.profile.cardNumber)) {
                                                        // status == assigned
                                                        println("status is assigned")
                                                        QueueInfoAssignedComposable(expireTime = "placeholder")
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
                else -> {}
            }


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

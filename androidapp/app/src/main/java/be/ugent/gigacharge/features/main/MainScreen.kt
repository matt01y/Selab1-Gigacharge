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
import be.ugent.gigacharge.features.ProfileUiState
import be.ugent.gigacharge.features.LocationUiState
import be.ugent.gigacharge.model.location.LocationStatus
import be.ugent.gigacharge.model.location.QueueState
import be.ugent.gigacharge.model.location.charger.ChargerStatus
import be.ugent.gigacharge.model.location.charger.UserField
import be.ugent.gigacharge.model.location.charger.UserType
import be.ugent.gigacharge.ui.theme.GigaChargeTheme


@Composable
fun MainRoute(onLocationSelectClick : () -> Unit, viewModel: MainViewModel) {
    MainScreen(
        // Navigation function
        onLocationSelectClick,
        viewModel
    )
}

@Composable
fun MainScreen(
    // Navigation function
    onLocationSelectClick: () -> Unit,
    viewModel: MainViewModel
) {
    val profileUiState by viewModel.profileUiState.collectAsState()
    val queueUiState by viewModel.queueUiState.collectAsState() // TODO: Wordt dit nog later gebruikt? of is dit overbodig?
    val locationUiState by viewModel.locationUiState.collectAsState()
    Box {
        Scaffold(
            topBar = {
                MainHeaderComposable(
                    { viewModel.toggleProfile() }
                ) {
                    when (profileUiState) {
                        ProfileUiState.Loading -> LoadingComposable(textColor = MaterialTheme.colors.onPrimary, text="Loading profile ...")
                        is ProfileUiState.Success -> {
                            val profileUiStateCasted = (profileUiState as ProfileUiState.Success)
                            if (!profileUiStateCasted.profile.visible) {
                                when (locationUiState) {
                                    LocationUiState.Loading -> LoadingComposable(textColor = MaterialTheme.colors.onPrimary, text="Loading location ...")
                                    is LocationUiState.Success -> {
                                        val location = (locationUiState as LocationUiState.Success).location
                                        LocationButtonComposable(
                                            onLocationSelectClick,
                                            { viewModel.toggleFavorite(location) },
                                            location,
                                            true
                                        )
                                    }
                                }
                            }
                            else {
                                val profile = profileUiStateCasted.profile
                                ProfileFormComposable(
                                    provider = profile.provider,
                                    providers = viewModel.getProviders(),
                                    cardNumber = profile.cardNumber,
                                    company = profile.company,
                                    companies = viewModel.getCompanies(),
                                    cancel = { viewModel.toggleProfile() },
                                    saveProfile = { p:String,n:String,c:String,b:Boolean -> viewModel.saveProfile(p,n,c,b) }
                                )
                            }

                        }
                    }
                }
            },
            bottomBar = {
                // TODO: VERWIJDEREN IN FINAL BUILD (ONLY FOR DEMO)
                Button(onClick = {viewModel.updateLocation()}) {
                    Text(text = "refresh")
                }
                if (!(locationUiState is LocationUiState.Success && (locationUiState as LocationUiState.Success).location.status == LocationStatus.OPEN)) {
                    Box(Modifier.height(IntrinsicSize.Max)) {
                        // Join/Leave button
                        if (locationUiState is LocationUiState.Success) {
                            val location = (locationUiState as LocationUiState.Success).location
                            QueueButtonComposable(
                                { viewModel.joinLeaveQueue(location) },
                                location.amIJoined
                            )
                        }
                        if (profileUiState is ProfileUiState.Success && (profileUiState as ProfileUiState.Success).profile.visible) {
                            Overlay()
                        }
                    }
                }
            }
        ) {
                paddingValues -> Column(Modifier.padding(paddingValues)) {
            Box {
                when (locationUiState) {
                    LocationUiState.Loading -> LoadingComposable()
                    is LocationUiState.Success -> {
                        if ((locationUiState as LocationUiState.Success).location.status == LocationStatus.OPEN) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(30.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Vrij parkeerplaats", color = MaterialTheme.colors.onBackground, fontSize = 25.sp)
                            }
                        } else {
                            LazyColumn {
                                item {
                                    QueueInfoComposable(locationUiState, profileUiState)
                                }
                            }
                        }
                    }
                }
                if (profileUiState is ProfileUiState.Success && (profileUiState as ProfileUiState.Success).profile.visible) {
                    Overlay()
                }

            }
        }
        }
        // Overlay if profile is visible
        /*if (profileUiState is ProfileUiState.Success && profileUiState.profile.visible) {
            Overlay()
        }*/
    }


}

@Composable
fun QueueInfoAssignedComposable(
    expireTime : String
) {
    Text("you have been assigned")
    Text("your reservation expires at: $expireTime")

}

@Composable
fun QueueInfoComposable(locationUiState : LocationUiState,
                        profileUiState: ProfileUiState) {
    val location = (locationUiState as LocationUiState.Success).location
    val queueSize = location.amountWaiting
    val queueStatus = location.queue
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
                                    if ((charger.user as UserField.CardNumber).cardnum.equals(profileUiState.profile.cardNumber)) {
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
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
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

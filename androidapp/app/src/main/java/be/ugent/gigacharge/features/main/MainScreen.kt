package be.ugent.gigacharge.features.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import be.ugent.gigacharge.common.composable.LoadingComposable
import be.ugent.gigacharge.common.composable.LocationButtonComposable
import be.ugent.gigacharge.common.composable.MainHeaderComposable
import be.ugent.gigacharge.common.composable.ProfileFormComposable
import be.ugent.gigacharge.features.LocationUiState
import be.ugent.gigacharge.R
import be.ugent.gigacharge.features.ProfileUiState
import be.ugent.gigacharge.model.location.LocationStatus
import be.ugent.gigacharge.model.location.QueueState
import be.ugent.gigacharge.model.location.charger.ChargerStatus
import be.ugent.gigacharge.model.location.charger.UserField
import be.ugent.gigacharge.model.location.charger.UserType
import be.ugent.gigacharge.ui.theme.GigaChargeTheme
import androidx.compose.ui.res.stringResource

@Composable
fun MainRoute(onRegisterSelectClick: () -> Unit, onLocationSelectClick : () -> Unit, viewModel: MainViewModel) {
    MainScreen(
        onRegisterSelectClick,
        onLocationSelectClick,
        viewModel
    )
}

@Composable
fun MainScreen(
    onRegisterSelectClick: () -> Unit,
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
                    when (val s = profileUiState) {
                        ProfileUiState.Loading -> LoadingComposable(textColor = MaterialTheme.colors.onPrimary, text="Loading profile ...")
                        is ProfileUiState.Success -> {
                            if (!s.profile.visible) {
                                when (val l = locationUiState) {
                                    LocationUiState.Loading -> LoadingComposable(textColor = MaterialTheme.colors.onPrimary, text="Loading location ...")
                                    is LocationUiState.Success -> {
                                        LocationButtonComposable(
                                            onLocationSelectClick,
                                            { viewModel.toggleFavorite(l.location) },
                                            l.location,
                                            true
                                        )
                                    }
                                }
                            } else {
                                val profile = s.profile
                                ProfileFormComposable(
                                    cardNumber = profile.cardNumber,
                                    deleteAccount = {
                                        viewModel.deleteProfile()
                                        onRegisterSelectClick()
                                    }
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
                val l = locationUiState
                if (!(l is LocationUiState.Success && l.location.status == LocationStatus.OPEN)) {
                    Box(Modifier.height(IntrinsicSize.Max)) {
                        // Join/Leave button
                        if (l is LocationUiState.Success) {
                            QueueButtonComposable(
                                { viewModel.joinLeaveQueue(l.location) },
                                l.location.amIJoined
                            )
                        }
                        if (profileUiState is ProfileUiState.Success && (profileUiState as ProfileUiState.Success).profile.visible) {
                            Overlay { viewModel.toggleProfile() }
                        }
                    }
                }
            }
        ) {
                paddingValues -> Column(Modifier.padding(paddingValues)) {
            Box {
                when (val l = locationUiState) {
                    LocationUiState.Loading -> LoadingComposable()
                    is LocationUiState.Success -> {
                        if (l.location.status == LocationStatus.OPEN) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(30.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(stringResource(R.string.empty_parking), color = MaterialTheme.colors.onBackground, fontSize = 25.sp)
                            }
                        } else {
                            LazyColumn {
                                item {
                                    QueueInfoComposable(l, profileUiState)
                                }
                            }
                        }
                    }
                }
                val p = profileUiState
                if (p is ProfileUiState.Success && p.profile.visible) {
                    Overlay { viewModel.toggleProfile() }
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
                Text(if (inQueue) stringResource(R.string.leave_queue) else stringResource(R.string.join_queue), fontSize= 20.sp, fontWeight= FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Overlay(cancel: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .clip(RectangleShape)
            .background(Color(0.0F, 0.0F, 0.0F, 0.5F))
            .clickable(MutableInteractionSource(), null, onClick = cancel)
    ) {}
}

@Composable
fun QueueInfoAssignedComposable(
    expireTime: String
) {
    Text(stringResource(R.string.assigned))
    Text("${stringResource(R.string.reservation_expires)}: $expireTime")
}

@Composable
fun QueueInfoComposable(locationUiState : LocationUiState.Success,
                        profileUiState: ProfileUiState) {
    val location = locationUiState.location
    val queueSize = location.amountWaiting
    val queueStatus = location.queue
    Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(stringResource(R.string.queue_info), color = MaterialTheme.colors.onBackground, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(5.dp))
                .padding(10.dp)
        ) {
            Text("${stringResource(R.string.in_queue)}: $queueSize", color = MaterialTheme.colors.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            when (queueStatus) {
                QueueState.NotJoined -> {
                    Text("${stringResource(R.string.queue_position)}: ${stringResource(R.string.queue_not_joined)}", color = MaterialTheme.colors.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                is QueueState.Joined -> {
                    Text("${stringResource(R.string.queue_position)}: ${queueStatus.myPosition}", color = MaterialTheme.colors.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
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

@Preview
@Composable
fun MainScreenPreview() {
    GigaChargeTheme {
        MainRoute({}, {}, hiltViewModel())
    }
}
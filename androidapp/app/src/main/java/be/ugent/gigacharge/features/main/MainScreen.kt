package be.ugent.gigacharge.features.main

import androidx.activity.compose.BackHandler
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
import be.ugent.gigacharge.features.LocationUiState
import be.ugent.gigacharge.R
import be.ugent.gigacharge.features.ProfileUiState
import be.ugent.gigacharge.model.location.LocationStatus
import be.ugent.gigacharge.model.location.QueueState
import be.ugent.gigacharge.ui.theme.GigaChargeTheme
import androidx.compose.ui.res.stringResource
import be.ugent.gigacharge.common.composable.*
import be.ugent.gigacharge.resources
import java.text.SimpleDateFormat

@Composable
fun MainRoute(onRegisterSelectClick: () -> Unit, onLocationSelectClick : () -> Unit, finishApp: () -> Unit, viewModel: MainViewModel) {
    MainScreen(
        onRegisterSelectClick,
        onLocationSelectClick,
        finishApp,
        viewModel
    )
}

@Composable
fun MainScreen(
    onRegisterSelectClick: () -> Unit,
    onLocationSelectClick: () -> Unit,
    finishApp : () -> Unit,
    viewModel: MainViewModel
) {
    BackHandler(onBack = finishApp, enabled = true)
    val profileUiState by viewModel.profileUiState.collectAsState()
    val locationUiState by viewModel.locationUiState.collectAsState()
    Box {
        Scaffold(
            topBar = {
                MainHeaderComposable(
                    { viewModel.toggleProfile() },
                    onLogoClick = { viewModel.refreshButtonPressed() }
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
                                            l.location,
                                            title = true
                                        )
                                    }
                                }
                            } else {
                                val profile = s.profile
                                ProfileFormComposable(
                                    cardNumber = profile.cardNumber,
                                    deleteAccount = {
                                        viewModel.toggleProfile() // When logging back in, the profile is closed
                                        viewModel.deleteProfile()
                                        onRegisterSelectClick()
                                    },
                                    readOnly = true
                                )
                            }
                        }
                    }
                }
            },
            bottomBar = {

                val l = locationUiState
                if (!(l is LocationUiState.Success && (l.location.status == LocationStatus.OPEN || l.location.queue is QueueState.Assigned))) {
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
                                    QueueInfoComposable(l)
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
fun QueueInfoComposable(locationUiState : LocationUiState.Success) {
    val location = locationUiState.location
    val queueSize = location.amountWaiting
    val queueStatus = location.queue
    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            //.padding(10.dp)
    ) {
        
        // De onderstaande box zorgt dat er geen probleem is met de maximumheight voor de Column
        Box(Modifier.height(200.dp).fillMaxWidth()) {
            Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    stringResource(R.string.queue_info),
                    color = MaterialTheme.colors.onBackground,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                when (queueStatus) {
                    is QueueState.Assigned -> {
                        val sdf = SimpleDateFormat("HH:mm")
                        MainScreenNotificationComposable(
                            notificationText = stringResource(R.string.your_turn),
                            description = "${stringResource(R.string.your_assigned_charger)} ${queueStatus.charger.description}",
                            subline = "${stringResource(R.string.your_turn_expires_at)} ${
                                sdf.format(
                                    queueStatus.expiretime
                                )
                            }"
                        )
                    }
                    else -> {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colors.onSurface,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .padding(10.dp)
                        ) {
                            Text(
                                "${stringResource(R.string.in_queue)}: $queueSize",
                                color = MaterialTheme.colors.onBackground,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            when (queueStatus) {
                                QueueState.NotJoined -> {
                                    Text(
                                        stringResource(R.string.queue_not_joined),
                                        color = MaterialTheme.colors.onBackground,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                QueueState.Charging -> {
                                    Text(stringResource(R.string.charging_car))
                                }
                                is QueueState.Joined -> {
                                    val position = queueStatus.myPosition.toInt()
                                    if (position == 0) {
                                        Text(
                                            stringResource(id = R.string.queue_position_zero),
                                            color = MaterialTheme.colors.onBackground,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        Text(
                                            resources().getQuantityString(
                                                R.plurals.queue_position_plural,
                                                queueStatus.myPosition.toInt(),
                                                queueStatus.myPosition.toInt()
                                            ),
                                            color = MaterialTheme.colors.onBackground,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                }

            }
        }
        Box(Modifier.height(500.dp)) {
            ChargerListComposable(chargers = location.chargers, location.assignedChargerId)
        }

    }

}

@Preview
@Composable
fun MainScreenPreview() {
    GigaChargeTheme {
        MainRoute({}, {}, finishApp = {}, hiltViewModel())
    }
}
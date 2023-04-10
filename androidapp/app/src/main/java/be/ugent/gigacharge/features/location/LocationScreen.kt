package be.ugent.gigacharge.features.location

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import be.ugent.gigacharge.common.composable.QueueButtonComposable

@Composable
fun LocationRoute(onBackArrowClick : () -> Unit) {
    LocationScreen(onBackArrowClick)
}

@Composable
fun LocationScreen(onBackArrowClick : () -> Unit,
                   modifier: Modifier = Modifier,
                   viewModel: LocationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val locations by viewModel.locationflow.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("choose location") },
                navigationIcon = {
                    IconButton(onClick = onBackArrowClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Text("hey", Modifier.padding(it))

        locations.forEach{
            Button(onClick = { viewModel.toggleQueue(it) }) {
                Text(text = "${it.name} - joined: ${it.amIJoined}", color = MaterialTheme.colors.secondary, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            }
        }

        QueueButtonComposable(
            { viewModel.refreshLocations() },
            false
        )

    }

    LaunchedEffect(key1 = true){
        viewModel.onStart()
    }
}
/*
@Preview
@Composable
fun LocationScreenPreview() {
    LocationScreen {
        {}
    }
}*/
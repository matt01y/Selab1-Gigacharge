package be.ugent.gigacharge.features.location

import androidx.compose.foundation.layout.Column
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
        Column {
            Text("hey", Modifier.padding(it))
            val pvalues = it

            locations.forEach{
                Button(onClick = { viewModel.toggleQueue(it) }, modifier = Modifier.padding(pvalues)) {
                    Text(text = "${it.name} - joined: ${it.amIJoined}", color = MaterialTheme.colors.secondary, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                }
            }
            Divider()

            Button(onClick = { viewModel.refreshLocations() }) {
                Text(text = "refresh locaties", color = MaterialTheme.colors.secondary, fontSize = 25.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(pvalues))
            }
        }


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
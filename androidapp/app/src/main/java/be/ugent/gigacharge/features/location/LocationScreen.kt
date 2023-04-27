package be.ugent.gigacharge.features.location

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import be.ugent.gigacharge.common.composable.LoadingComposable
import be.ugent.gigacharge.common.composable.LocationButtonComposable
import be.ugent.gigacharge.features.LocationsUiState
import be.ugent.gigacharge.model.location.Location
//import be.ugent.gigacharge.data.local.models.Location
import be.ugent.gigacharge.ui.theme.GigaChargeTheme

@Composable
fun LocationRoute(onBackArrowClick : () -> Unit, viewModel: LocationViewModel) {
    LocationScreen(
        onBackArrowClick,
        viewModel
    )
}

@Composable
fun LocationScreen(
    onBackArrowClick : () -> Unit,
    viewModel: LocationViewModel
) {
    val locationsUiState by viewModel.locationsUiState.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kies een vestiging") },
                navigationIcon = {
                    IconButton(onClick = onBackArrowClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) {
        Column(Modifier.padding(it)) {
            when (val s = locationsUiState) {
                LocationsUiState.Loading -> LoadingComposable()
                is LocationsUiState.Success -> {
                    val locations = s.locations
                    LazyColumn {
                        locations.forEach { location: Location ->
                            item {
                                LocationButtonComposable(
                                    {
                                        viewModel.setLocation(location)
                                        onBackArrowClick()
                                    },
                                    { viewModel.toggleFavorite(location) },
                                    location,
                                    modifier = Modifier.padding(0.dp, 8.dp)
                                )
                                ItemBreak()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemBreak() {
    Column(
        Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(Color.LightGray)
    ) {}
}

@Preview
@Composable
fun LocationScreenPreview() {
    GigaChargeTheme {
        LocationRoute({}, hiltViewModel())
    }
}
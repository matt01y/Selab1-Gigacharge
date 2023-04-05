package be.ugent.gigacharge.features

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LocationRoute(onBackArrowClick : () -> Unit) {
    LocationScreen(onBackArrowClick)
}

@Composable
fun LocationScreen(onBackArrowClick : () -> Unit) {
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
    }
}

@Preview
@Composable
fun LocationScreenPreview() {
    LocationScreen {
        {}
    }
}
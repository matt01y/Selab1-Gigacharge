package be.ugent.gigacharge.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.ugent.gigacharge.ui.theme.GigaChargeTheme

@Composable
fun MainHeaderComposable(onProfileSelectClick: () -> Unit, headerContent: @Composable () -> Unit) {
    Column(Modifier.background(MaterialTheme.colors.primary)) {
        Column(
            Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Title and profile
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.weight(0.85F), horizontalArrangement = Arrangement.Center) {
                    Text("GigaCharge", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colors.onPrimary)
                }
                Row(Modifier.weight(0.15F), horizontalArrangement = Arrangement.Center) {
                    ProfileComposable(onProfileSelectClick)
                }
            }
            // Content
            headerContent()
        }
    }
}

@Composable
fun ProfileComposable(onProfileSelectClick: () -> Unit) {
    IconButton(onClick=onProfileSelectClick) {
        Box(
            Modifier
                .width(40.dp)
                .height(40.dp)
                .clip(CircleShape)
                .background(Color.Transparent)
                .border(2.dp, Color.White, CircleShape)
        ) {
            Icon(
                Icons.Filled.Person,
                "Person",
                Modifier.size(40.dp),
                tint= Color.White
            )
        }
    }
}

@Preview
@Composable
fun MainHeaderComposablePreview() {
    val location = "Roelarta Roeselare"
    GigaChargeTheme {
        MainHeaderComposable({}, { LocationButtonComposable({}, location) })
    }
}
package be.ugent.gigacharge.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import be.ugent.gigacharge.data.local.models.Location
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.location.LocationStatus
import be.ugent.gigacharge.model.location.QueueState
import be.ugent.gigacharge.ui.theme.GigaChargeTheme

@Composable
fun LocationButtonComposable(
    setLocation: () -> Unit,
    toggleFavorite: () -> Unit,
    location: Location,
    title: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White, shape = RoundedCornerShape(5.dp))
    ) {
        Button(
            setLocation,
            Modifier
                .fillMaxHeight()
                .weight(0.85F),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            elevation = ButtonDefaults.elevation(0.dp, 0.dp)
        ) {
            Column(Modifier.fillMaxWidth()) {
                if (title) {
                    Text("Vestiging", color = Color.Gray, fontSize = 10.sp)
                }
                Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                    Text(location.name, color = MaterialTheme.colors.onSurface, fontSize = 18.sp)
                }
            }
        }
        IconButton(
            toggleFavorite,
            Modifier
                .fillMaxHeight()
                .weight(0.15F),
        ) {
            Icon(
                //Default sinds dat we de fvorite nog niet kennen op basis vn location
                Icons.Outlined.StarOutline,
                "Make favorite",
                //Jarne versie:
                //if (location.favorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                //if (location.favorite) "Remove favorite" else "Make favorite",
                Modifier.size(40.dp),
                tint = Color(1.0F, 0.75F, 0.0F, 1.0F)
            )
        }
    }
}

@Preview
@Composable
fun LocationButtonComposablePreview() {
    GigaChargeTheme {
        LocationButtonComposable(
            {},
            {},
            Location(
                "",
                "Roularta Rouselare",
                QueueState.NotJoined,
                LocationStatus.OUT,
                0,
                listOf()
            ),
            false
        )
    }
}

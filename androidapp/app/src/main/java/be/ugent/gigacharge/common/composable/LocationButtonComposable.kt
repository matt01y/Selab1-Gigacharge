package be.ugent.gigacharge.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.location.LocationStatus
import be.ugent.gigacharge.model.location.QueueState
import be.ugent.gigacharge.ui.theme.GigaChargeTheme


@Composable
fun VestigingButton(
    title: Boolean = false,
    location: Location,
    setLocation: () -> Unit
) {
    Button(
        setLocation,
        Modifier
            .fillMaxHeight(),
            //.weight(0.85F),
        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.background),
        elevation = ButtonDefaults.elevation(0.dp,0.dp)
    ) {
        Column(Modifier.fillMaxWidth()) {
            if (title) {
                Text("Vestiging", color = Color.Gray, fontSize = 10.sp)
            }
            Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                Text(location.name, color = MaterialTheme.colors.onBackground, fontSize = 18.sp)
            }
        }
    }
}


@Composable
fun LocationButtonComposable(
    setLocation: () -> Unit,
    location: Location,
    modifier: Modifier = Modifier,
    title: Boolean = false
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
            .background(MaterialTheme.colors.background, shape = RoundedCornerShape(5.dp))
    ) {
        VestigingButton(title, location, setLocation)
    }
}

@Preview
@Composable
fun LocationButtonComposablePreview() {
    GigaChargeTheme {
        LocationButtonComposable(
            {},
            Location(
                "",
                "Roularta Rouselare",
                QueueState.NotJoined,
                LocationStatus.OUT,
                0,
                listOf()
            ),
            title = false
        )
    }
}

package be.ugent.gigacharge.common.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.ugent.gigacharge.GigaChargeApp
import be.ugent.gigacharge.ui.theme.GigaChargeTheme

@Composable
fun LocationButtonComposable(chooseLocation: ()->Unit, currentLocation: String) {
    Button(
        chooseLocation,
        Modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(0.9F)) {
                Text("Vestiging", color = Color.Gray, fontSize = 10.sp)
                Text(currentLocation, color = MaterialTheme.colors.onSurface, fontSize = 15.sp)
            }
            Icon(
                Icons.Filled.Star,
                "Favorite",
                Modifier.weight(0.1F).size(50.dp),
                tint = Color(1.0F, 0.75F, 0.0F, 1.0F)
            )
        }
    }
}

@Preview
@Composable
fun LocationButtonComposablePreview() {
    GigaChargeTheme {
        LocationButtonComposable({}, "Roularta Roeselare")
    }
}

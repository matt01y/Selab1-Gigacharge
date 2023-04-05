package be.ugent.gigacharge.common.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LocationButtonComposable(chooseLocation: ()->Unit, currentLocation:String) {
    Button(
        chooseLocation,
        Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(0.9F)) {
                Text("Vestiging", color = Color.Gray, fontSize = 10.sp)
                Text(currentLocation, fontSize = 15.sp)
            }
            Icon(
                Icons.Filled.Star,
                "Favorite",
                Modifier
                    .weight(0.1F)
                    .size(50.dp),
                tint = Color(1.0F, 0.75F, 0.0F, 1.0F)
            )
        }
    }
}

@Preview
@Composable
fun LocationButtonComposablePreview() {
    val location = "Roularta Roeselare"
    LocationButtonComposable({}, location)
}

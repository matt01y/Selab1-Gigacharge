package be.ugent.gigacharge.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProfileComposable() {
    IconButton(onClick={}) {
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
fun ProfileComposablePreview() {
    ProfileComposable()
}

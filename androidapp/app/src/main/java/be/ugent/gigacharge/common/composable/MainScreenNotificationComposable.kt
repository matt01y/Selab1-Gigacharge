package be.ugent.gigacharge.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.ugent.gigacharge.ui.theme.DarkGreen

@Composable
fun MainScreenNotificationComposable(
    notificationText : String,
    description: String,
    subline: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = DarkGreen,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column() {
            Text(
                text = notificationText,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 30.sp
            )
            Text(
                text = description,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 20.sp
            )
            Text(
                text = subline,
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = 20.sp
            )
        }

    }
}

@Preview
@Composable
fun MainScreenNotificationComposablePreview() {
    MainScreenNotificationComposable(
        "hey, dit is mijn notification",
        "paal 1 description",
        "subline"
    )
}


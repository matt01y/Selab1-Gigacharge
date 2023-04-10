package be.ugent.gigacharge.common.composable

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import be.ugent.gigacharge.ui.theme.GigaChargeTheme

@Composable
fun LoadingComposable(spinnerColor: Color = MaterialTheme.colors.secondaryVariant, textColor: Color = MaterialTheme.colors.onBackground) {
    val transition = rememberInfiniteTransition()
    val rotation by transition.animateFloat(-36F, 324F, InfiniteRepeatableSpec(tween(durationMillis = 1000), RepeatMode.Restart))

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(
            0.2F,
            Modifier
                .size(28.dp)
                .padding(5.dp)
                .rotate(rotation),
            color = spinnerColor,
            strokeWidth = 3.dp
        )
        Spacer(Modifier.width(8.dp))
        Text("Loading ...", color = textColor)
    }
}

@Preview
@Composable
fun LoadingComposablePreview() {
    GigaChargeTheme {
        LoadingComposable()
    }
}

package be.ugent.gigacharge.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.ugent.gigacharge.ui.theme.GigaChargeTheme

@Composable
fun QueueInfoComposable(queueSize: Int) {
    Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text("Queue Information", color = MaterialTheme.colors.onBackground, fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(5.dp))
                .padding(10.dp)
        ) {
            Text("In queue: $queueSize", color = MaterialTheme.colors.onSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview
@Composable
fun QueueInfoComposablePreview() {
    GigaChargeTheme {
        QueueInfoComposable(5)
    }
}

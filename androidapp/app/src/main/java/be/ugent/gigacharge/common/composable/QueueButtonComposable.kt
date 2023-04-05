package be.ugent.gigacharge.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QueueButtonComposable(queueButtonFunction: () -> Unit, inQueue: Boolean) {
    Row(
        Modifier
            .height(100.dp)
            .fillMaxWidth(),
        verticalAlignment= Alignment.CenterVertically
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment= Alignment.CenterHorizontally) {
            Button(
                queueButtonFunction,
                Modifier.height(50.dp)
            ) {
                Text(if (inQueue) "Join queue" else "Leave queue" , fontSize= 20.sp, fontWeight= FontWeight.Bold)
            }
        }
    }
}

@Preview
@Composable
fun QueueButtonComposablePreview() {
    QueueButtonComposable({}, true)
}

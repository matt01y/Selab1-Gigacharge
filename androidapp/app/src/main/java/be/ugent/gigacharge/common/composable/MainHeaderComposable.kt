package be.ugent.gigacharge.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainHeaderComposable(headerContent: @Composable () -> Unit) {
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
                    ProfileComposable()
                }
            }
            // Content
            headerContent()
        }
    }
}

@Preview
@Composable
fun MainHeaderComposablePreview() {
    val location = "Roelarta Roeselare"
    MainHeaderComposable { LocationButtonComposable({}, location) }
}
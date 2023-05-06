package be.ugent.gigacharge.common.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.ugent.gigacharge.model.location.charger.Charger
import be.ugent.gigacharge.model.location.charger.ChargerStatus
import be.ugent.gigacharge.model.location.charger.UserField
import be.ugent.gigacharge.model.location.charger.UserType

@Composable
fun ChargerListComposable(chargers : List<Charger>) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(10.dp)
            .background(MaterialTheme.colors.background)
    ) {
        Text(
            "Laadpalen:",
            Modifier.align(Alignment.TopStart).padding(start = 10.dp, top = 10.dp),
            color = MaterialTheme.colors.onBackground,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
        LazyColumn(Modifier.padding(top = 40.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(chargers.size) { index ->
                ChargerListElementComposable(chargers[index])
            }
        }
    }
}

/*
Dit maakt een UI blok die een charger beschrijft.
Het wordt gebruikt in de ChargerListComposable
 */
@Composable
fun ChargerListElementComposable(charger: Charger) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colors.onSurface,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text("status: " + charger.status,
                color = MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.Bold)
            Text("description: " + charger.description,
                color = MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.Bold)
        }
    }
}

@Preview
@Composable
fun ChargerListComposablePreview() {
    ChargerListComposable(chargers = listOf(
        Charger("i am a kewl charger",
                "myid1",
                ChargerStatus.FREE,
                UserField.Null,
                UserType.USER),
        Charger("i am a kewler charger",
            "myid2",
            ChargerStatus.FREE,
            UserField.Null,
            UserType.USER),
        Charger("i am the kewlest charger",
            "myid3",
            ChargerStatus.FREE,
            UserField.Null,
            UserType.USER),
    ))
}
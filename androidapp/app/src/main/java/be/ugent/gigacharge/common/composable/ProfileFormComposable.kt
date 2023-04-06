package be.ugent.gigacharge.common.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.ugent.gigacharge.ui.theme.GigaChargeTheme
import be.ugent.gigacharge.ui.theme.Green
import be.ugent.gigacharge.ui.theme.Red

@Composable
fun ProfileFormComposable(
    provider: String,
    providers: List<String>,
    updateProvider: (String) -> Unit,

    cardNumber: String,
    updateCardNumber: (String) -> Unit,

    company: String,
    companies: List<String>,
    updateCompany: (String) -> Unit,

    cancel: () -> Unit,
    saveProfile: (String, String, String) -> Unit
) {
    var providerExpanded by remember { mutableStateOf(false) }
    var providerState by remember { mutableStateOf(provider) }
    val selectProvider = {s:String ->
        {
            updateProvider(s)
            providerState = s
            providerExpanded = false
        }
    }

    var companyExpanded by remember { mutableStateOf(false) }
    var companyState by remember { mutableStateOf(company) }
    val selectCompany = {s:String ->
        {
            updateCompany(s)
            companyState = s
            providerExpanded = false
        }
    }

    Column() {
        // Provider
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Kaartdeler",
                Modifier.weight(0.4F),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Box(Modifier.weight(0.6F)) {
                Button(
                    { providerExpanded = true },
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .align(Alignment.CenterStart),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        Text(providerState)
                    }
                    DropdownMenu(
                        providerExpanded,
                        {providerExpanded = false}
                    ) {
                        providers.forEach { s: String ->
                            DropdownMenuItem(selectProvider(s)) {
                                Text(s)
                            }
                        }
                    }
                }
            }
        }

        // CardNumber
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Kaartnummer",
                Modifier.weight(0.4F),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            TextField(
                cardNumber,
                updateCardNumber,
                Modifier
                    .weight(0.6F)
                    .height(50.dp),
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.surface,
                    focusedIndicatorColor = MaterialTheme.colors.secondaryVariant,
                    cursorColor = MaterialTheme.colors.secondaryVariant
                )
            )
        }

        // Company
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Bedrijf",
                Modifier.weight(0.4F),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Box(Modifier.weight(0.6F)) {
                Button(
                    { companyExpanded = true },
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.surface)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        Text(companyState)
                    }
                    DropdownMenu(companyExpanded, {companyExpanded = false}) {
                        companies.forEach { s: String ->
                            DropdownMenuItem(selectCompany(s)) {
                                Text(s)
                            }
                        }
                    }
                }

            }
        }

        // Buttons
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {cancel},
                colors = ButtonDefaults.buttonColors(backgroundColor = Red)
            ) {
                Text("Annuleer", fontWeight = FontWeight.Bold , color = Color.White)
            }
            Spacer(Modifier.width(20.dp))
            Button(
                onClick = {saveProfile("", "", "",)},
                colors = ButtonDefaults.buttonColors(backgroundColor = Green)
            ) {
                Text("Opslaan", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Preview
@Composable
fun ProfileFormComposablePreview() {
    GigaChargeTheme {
        ProfileFormComposable("test", listOf("test","test"), {}, "1234-5678", {}, "comp", listOf("comp"), {}, {}, { _: String, _: String, _: String -> })
    }
}
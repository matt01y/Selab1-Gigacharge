package be.ugent.gigacharge.common.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.ugent.gigacharge.ui.theme.GigaChargeTheme
import be.ugent.gigacharge.ui.theme.Green
import be.ugent.gigacharge.ui.theme.Red
import be.ugent.gigacharge.ui.theme.Shapes

@Composable
fun ProfileFormComposable(
    provider: String,
    providers: List<String>,
    cardNumber: String,
    company: String,
    companies: List<String>,
    cancel: () -> Unit,
    saveProfile: (String, String, String) -> Unit
) {
    var providerState by remember { mutableStateOf(provider) }
    var companyState by remember { mutableStateOf(company) }
    var cardNumberState by remember { mutableStateOf(cardNumber) }

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
            MyDropdown(providerState, providers, {s:String -> providerState = s }, Modifier.weight(0.6F))
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
                cardNumberState,
                {s:String -> cardNumberState = s},
                Modifier
                    .weight(0.6F)
                    .height(50.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = MaterialTheme.colors.onBackground,
                    backgroundColor = MaterialTheme.colors.background,
                    focusedIndicatorColor = MaterialTheme.colors.secondaryVariant,
                    cursorColor = MaterialTheme.colors.secondaryVariant,
                    placeholderColor = Color.LightGray
                ),
                singleLine = true,
                placeholder = {Text("xxxx - xxxx")},
                shape = RoundedCornerShape(5.dp)
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
            MyDropdown(companyState, companies, {s:String -> companyState = s }, Modifier.weight(0.6F))
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
                onClick = cancel,
                colors = ButtonDefaults.buttonColors(backgroundColor = Red)
            ) {
                Text("Annuleer", fontWeight = FontWeight.Bold , color = Color.White)
            }
            Spacer(Modifier.width(20.dp))
            Button(
                onClick = {
                    saveProfile(providerState, cardNumberState, companyState)
                    cancel()
                },
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
        ProfileFormComposable("test", listOf("test","test"), "", "comp", listOf("comp"), {}, { _: String, _: String, _: String -> })
    }
}
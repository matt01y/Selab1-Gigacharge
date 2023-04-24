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
fun ProviderBox(
    provider: String,
    providers: List<String>,
    providerState : String,
    onPoviderStateChange : (String) -> Unit) {
    //var providerState by remember { mutableStateOf(provider) }
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
        MyDropdown(providerState, providers, onPoviderStateChange, Modifier.weight(0.6F))
    }
}

@Composable
fun CardNumberBox(
    cardNumberState : String,
    onCardNumberStateChange : (String) -> Unit
) {
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
            onCardNumberStateChange,
            Modifier
                .weight(0.6F)
                .height(50.dp),
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                focusedIndicatorColor = MaterialTheme.colors.secondaryVariant,
                cursorColor = MaterialTheme.colors.secondaryVariant
            ),
            textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colors.onBackground)
        )
    }
}

@Composable
fun CompanyBox(
    companies: List<String>,
    companyState : String,
    onCompanyStateChange : (String) -> Unit
) {
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
        MyDropdown(companyState, companies, onCompanyStateChange, Modifier.weight(0.6F))
    }
}

@Composable
fun StandardButtonsField(
    cancel: () -> Unit,
    saveProfile: (String, String, String, Boolean) -> Unit,
    providerState : String,
    cardNumberState: String,
    companyState: String
) {
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
                saveProfile(providerState, cardNumberState, companyState, false)
                //cancel()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Green)
        ) {
            Text("Opslaan", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun ProfileFormComposable(
    provider: String,
    providers: List<String>,
    cardNumber: String,
    company: String,
    companies: List<String>,
    cancel: () -> Unit,
    saveProfile: (String, String, String, Boolean) -> Unit
) {
    var providerState by remember { mutableStateOf(provider) }
    var companyState by remember { mutableStateOf(company) }
    var cardNumberState by remember { mutableStateOf(cardNumber) }

    Column() {
        ProviderBox(provider, providers, providerState) { providerState = it }

        // CardNumber
        CardNumberBox(cardNumberState) { cardNumberState = it }

        // Company
        CompanyBox(companies, companyState) { companyState = it }

        // Buttons
        StandardButtonsField(cancel, saveProfile, providerState, cardNumberState, companyState)
    }
}

@Preview
@Composable
fun ProfileFormComposablePreview() {
    GigaChargeTheme {
        ProfileFormComposable("test", listOf("test","test"), "", "comp", listOf("comp"), {}, { _: String, _: String, _: String, _:Boolean -> })
    }
}
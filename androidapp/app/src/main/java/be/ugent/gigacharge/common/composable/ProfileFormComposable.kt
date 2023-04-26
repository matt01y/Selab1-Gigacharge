package be.ugent.gigacharge.common.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.ugent.gigacharge.ui.theme.GigaChargeTheme
import be.ugent.gigacharge.ui.theme.Green
import be.ugent.gigacharge.ui.theme.Red
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun ProfileFormComposable(
    provider: String,
    providers: List<String>,
    cardNumber: String,
    company: String,
    companies: List<String>,
    cancel: () -> Unit,
    saveProfile: (String, String, String, Boolean) -> Unit,
    isValidCardNumber: (String) -> Boolean
) {
    var providerState by remember { mutableStateOf(provider) }
    var companyState by remember { mutableStateOf(company) }
    var cardNumberState by remember { mutableStateOf(cardNumber) }
    var validCardNumberState by remember { mutableStateOf(isValidCardNumber(cardNumber)) }

    Column {
        // Provider
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Kaartdeler",
                Modifier.weight(0.4F),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            MyDropdown(
                providerState,
                providers,
                { s: String -> providerState = s },
                Modifier.weight(0.6F)
            )
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
                {
                    s: String -> cardNumberState = s
                    validCardNumberState = isValidCardNumber(s)
                },
                Modifier
                    .weight(0.6F)
                    .border(2.dp, if (!validCardNumberState) Color.Red else Color.Transparent, RoundedCornerShape(3.dp)),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.background,
                    focusedIndicatorColor = MaterialTheme.colors.secondaryVariant,
                    cursorColor = MaterialTheme.colors.secondaryVariant
                ),
                textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colors.onBackground),
                isError = !validCardNumberState
            )
        }

        // Company
        Row(
            Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Bedrijf",
                Modifier.weight(0.4F),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            MyDropdown(
                companyState,
                companies,
                { s: String -> companyState = s },
                Modifier.weight(0.6F)
            )
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
                Text("Annuleer", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(Modifier.width(20.dp))
            Button(
                onClick = {
                    if (validCardNumberState) {
                        saveProfile(providerState, cardNumberState, companyState, false)
                    }
                    //cancel()
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
        ProfileFormComposable(
            "test",
            listOf("test", "test"),
            "1234",
            "comp",
            listOf("comp"),
            {},
            { _: String, _: String, _: String, _: Boolean -> },
            {c:String -> c == "123"}
        )
    }
}
package be.ugent.gigacharge.common.composable

import android.content.res.Resources
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
import be.ugent.gigacharge.R
import be.ugent.gigacharge.ui.theme.GigaChargeTheme
import be.ugent.gigacharge.ui.theme.Green
import be.ugent.gigacharge.ui.theme.Red
import androidx.compose.ui.res.stringResource;

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
        Column() {
            Text(text = "Kaartnummer",
                color = MaterialTheme.colors.onBackground,
                fontSize = 20.sp
            )

            OutlinedTextField(
                value = cardNumberState,
                onValueChange = onCardNumberStateChange,
                //label = {Text("Kaartnummer")},
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colors.onBackground,
                    unfocusedBorderColor = MaterialTheme.colors.onBackground,
                    cursorColor = MaterialTheme.colors.onBackground,
                    textColor = MaterialTheme.colors.onBackground
                ),
                textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colors.onBackground)
            )
        }

    }
}

@Composable
fun StandardButtonsField(
    cancel: () -> Unit,
    saveProfile: (String, Boolean) -> Unit,
    cardNumberState: String,
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
            Text(stringResource(R.string.cancel), fontWeight = FontWeight.Bold , color = Color.White)
        }
        Spacer(Modifier.width(20.dp))
        Button(
            onClick = {
                saveProfile(cardNumberState, false)
                //cancel()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Green)
        ) {
            Text(stringResource(R.string.save), fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun ProfileFormComposable(
    cardNumber: String,
    cancel: () -> Unit,
    saveProfile: (String, Boolean) -> Unit
) {
    var cardNumberState by remember { mutableStateOf(cardNumber) }

    Column() {
        // CardNumber
        CardNumberBox(cardNumberState) { cardNumberState = it }
        // Buttons
        StandardButtonsField(cancel, saveProfile, cardNumberState)
    }
}

@Preview
@Composable
fun ProfileFormComposablePreview() {
    GigaChargeTheme {
        ProfileFormComposable("test", {}, { _: String, _:Boolean -> })
    }
}
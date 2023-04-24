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
    cardNumber: String,
    cancel: () -> Unit,
    saveProfile: (String, Boolean) -> Unit
) {
    var cardNumberState by remember { mutableStateOf(cardNumber) }

    Column() {
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
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = MaterialTheme.colors.background,
                    focusedIndicatorColor = MaterialTheme.colors.secondaryVariant,
                    cursorColor = MaterialTheme.colors.secondaryVariant
                ),
                textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colors.onBackground)
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
                Text("Annuleer", fontWeight = FontWeight.Bold , color = Color.White)
            }
            Spacer(Modifier.width(20.dp))
            Button(
                onClick = {
                    saveProfile(cardNumberState, false)
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
        ProfileFormComposable("test", {}, { _:String, _:Boolean -> })
    }
}
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
import be.ugent.gigacharge.R
import be.ugent.gigacharge.ui.theme.GigaChargeTheme
import be.ugent.gigacharge.ui.theme.Red
import androidx.compose.ui.res.stringResource

@Composable
fun CardNumberBox(
    cardNumber : String,
    onCardNumberStateChange : (String) -> Unit,
    readOnly: Boolean = false
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = stringResource(R.string.card_number),
                color = MaterialTheme.colors.onBackground,
                fontSize = 20.sp
            )

            OutlinedTextField(
                value = cardNumber,
                onValueChange = onCardNumberStateChange,
                singleLine = true,
                readOnly = readOnly,
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
    deleteAccount: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = deleteAccount,
            colors = ButtonDefaults.buttonColors(backgroundColor = Red)
        ) {
            Text(stringResource(R.string.delete_account), fontWeight = FontWeight.Bold , color = Color.White)
        }
    }
}

@Composable
fun ProfileFormComposable(
    cardNumber: String,
    deleteAccount: () -> Unit,
    readOnly: Boolean = false
) {
    var cardNumberState by remember { mutableStateOf(cardNumber) }

    Column {
        // CardNumber
        CardNumberBox(cardNumberState, { cardNumberState = it }, readOnly)
        // Buttons
        StandardButtonsField(deleteAccount)
    }
}

@Preview
@Composable
fun ProfileFormComposablePreview() {
    GigaChargeTheme {
        ProfileFormComposable("test", {}, true)
    }
}
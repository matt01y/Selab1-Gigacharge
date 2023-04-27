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
import androidx.compose.ui.res.stringResource;

@Composable
fun CardNumberBox(
    cardNumberState : String,
    onCardNumberStateChange : (String) -> Unit,
    readOnly: Boolean = false
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.card_number),
            Modifier.weight(0.4F),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        TextField(
            cardNumberState,
            onCardNumberStateChange,
            Modifier.weight(0.6F),
            singleLine = true,
            readOnly = readOnly,
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
) {
    var cardNumberState by remember { mutableStateOf(cardNumber) }

    Column() {
        // CardNumber
        CardNumberBox(cardNumberState, { cardNumberState = it }, true)
        // Buttons
        StandardButtonsField(deleteAccount)
    }
}

@Preview
@Composable
fun ProfileFormComposablePreview() {
    GigaChargeTheme {
        ProfileFormComposable("test") {}
    }
}
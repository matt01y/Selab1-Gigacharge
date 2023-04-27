package be.ugent.gigacharge.common.composable

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.ugent.gigacharge.data.local.models.Profile
import be.ugent.gigacharge.features.main.MainViewModel
import be.ugent.gigacharge.R
import be.ugent.gigacharge.ui.theme.GigaChargeTheme
import be.ugent.gigacharge.ui.theme.Green
import be.ugent.gigacharge.ui.theme.Red
import androidx.compose.ui.res.stringResource;
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun CardNumberBox(
    cardNumberState : String,
    onCardNumberStateChange : (String) -> Unit,
    validCardNumberState: Boolean
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
            Modifier
                .weight(0.6F)
                .border(2.dp, if (!validCardNumberState) Color.Red else Color.Transparent, RoundedCornerShape(3.dp)),
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
    saveProfile: (String, Boolean) -> Unit,
    isValidCardNumber: (String) -> Boolean
) {
    var cardNumberState by remember { mutableStateOf(cardNumber) }
    var validCardNumberState by remember { mutableStateOf(true) }

    Column() {
        // CardNumber
        CardNumberBox(cardNumberState, { cardNumberState = it }, validCardNumberState)
        // Buttons
        StandardButtonsField(cancel, {s:String, b:Boolean ->
            validCardNumberState = isValidCardNumber(s)
            if (validCardNumberState) {
                saveProfile(s,b)
            }
        }, cardNumberState)
    }
}

@Preview
@Composable
fun ProfileFormComposablePreview() {
    GigaChargeTheme {
        ProfileFormComposable("test", {}, { _: String, _:Boolean -> }, {s:String -> s == "123"})
    }
}
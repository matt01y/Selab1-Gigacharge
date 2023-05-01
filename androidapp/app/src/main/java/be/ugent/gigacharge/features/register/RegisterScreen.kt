package be.ugent.gigacharge.features.register

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.hilt.navigation.compose.hiltViewModel
import be.ugent.gigacharge.R
import be.ugent.gigacharge.common.composable.BasicButton
import be.ugent.gigacharge.common.composable.CardNumberBox
import be.ugent.gigacharge.common.composable.ProfileFormComposable
import be.ugent.gigacharge.common.ext.basicButton
import be.ugent.gigacharge.common.ext.fieldModifier
import be.ugent.gigacharge.R.string as AppText
import androidx.compose.ui.res.stringResource;
import be.ugent.gigacharge.model.AuthenticationError

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RegisterScreen(
    openAndPopUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val authenticationError by viewModel.authenticationErrors.collectAsState()
    val uiState by viewModel.uiState
    val fieldModifier = Modifier.fieldModifier()

    var validCardNumberState by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { RegisterTopBar() },
        bottomBar = {
            BasicButton(
                AppText.create_account,
                Modifier.basicButton()
            ) {
                // validCardNumberState = viewModel.isValidCardNumber(uiState.cardnumber)
                if (validCardNumberState) {
                    viewModel.onRegister(openAndPopUp)
                }
            }
        },
        modifier = modifier
            .padding(20.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(30.dp)
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.welcome), color = MaterialTheme.colors.primary)

            // CardNumber (not whole form since we don't need a back button, save button, ...)
            CardNumberBox(uiState.cardnumber, viewModel::onCardNumberChange)
            when (authenticationError) {
                AuthenticationError.INVALID_CARD_NUMBER -> ErrorMessage(stringResource(AppText.invalid_cardNumber_error))
                AuthenticationError.TIMEOUT -> ErrorMessage(stringResource(AppText.timeout_error))
                AuthenticationError.ERROR -> ErrorMessage(stringResource(AppText.enable_error))
                else -> {}
            }
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Text(message, color = Color.Red)
}

@Composable
fun RegisterTopBar() {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.background,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
            }
        }
    )
}


@Preview
@Composable
fun PreviewRegisterScreen() {
    RegisterScreen(
        openAndPopUp = { },
        modifier = Modifier,
        viewModel = hiltViewModel()
    )
}
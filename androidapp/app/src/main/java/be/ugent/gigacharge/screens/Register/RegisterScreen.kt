package be.ugent.gigacharge.screens.Register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import be.ugent.gigacharge.common.composable.BasicButton
import be.ugent.gigacharge.common.composable.BasicField
import be.ugent.gigacharge.common.ext.basicButton
import be.ugent.gigacharge.common.ext.fieldModifier
import be.ugent.gigacharge.R.string as AppText

@Composable
fun RegisterScreen(
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val fieldModifier = Modifier.fieldModifier()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = uiState.statusmessage)
        BasicField(text = AppText.cardnumber, value = uiState.cardnumber, onNewValue = viewModel::onCardNumberChange)
        BasicButton(AppText.create_account, Modifier.basicButton()) {
            viewModel.onRegister(openAndPopUp)
        }
    }
}
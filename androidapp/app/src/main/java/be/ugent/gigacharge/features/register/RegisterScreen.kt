package be.ugent.gigacharge.features.register

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import be.ugent.gigacharge.common.composable.BasicButton
import be.ugent.gigacharge.common.ext.basicButton
import be.ugent.gigacharge.common.ext.fieldModifier
import be.ugent.gigacharge.R.string as AppText

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RegisterScreen(
    openAndPopUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState
    val fieldModifier = Modifier.fieldModifier()

    Scaffold(
        topBar = { RegisterTopBar() },
        bottomBar = {
            BasicButton(
                AppText.create_account,
                Modifier.basicButton()
            ) { viewModel.onRegister(openAndPopUp) }
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
            Text(text = "Welkom!", color = MaterialTheme.colors.primary)
            //CardChooserDropDown()
            TitledDropDownComposable(
                title = "Kaartdeler",
                listContents = listOf("MobilityPlus", "Blue Corner")
            )
            Text(text = uiState.statusmessage, color = MaterialTheme.colors.primary)
            OutlinedTextField(
                value = uiState.cardnumber,
                label = { Text("Kaartnummer", color = MaterialTheme.colors.primary) },
                onValueChange = viewModel::onCardNumberChange,
                colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.primary)
            )
//            BasicField(
//                text = AppText.cardnumber,
//                value = uiState.cardnumber,
//                onNewValue = viewModel::onCardNumberChange
//            )
        }
    }
}

@Composable
fun RegisterTopBar() {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.onSecondary,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GigaCharge",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
            }
        }
    )
}

/*
@Preview
@Composable
fun PreviewRegisterScreen() {
    RegisterScreen(
        openAndPopUp = { },
        modifier = Modifier,
        viewModel = hiltViewModel()
    )
}*/
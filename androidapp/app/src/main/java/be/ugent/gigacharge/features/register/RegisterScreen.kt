package be.ugent.gigacharge.features.register

import android.annotation.SuppressLint
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
import be.ugent.gigacharge.common.composable.BasicButton
import be.ugent.gigacharge.common.composable.BasicField
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
        bottomBar = { BasicButton(AppText.create_account, Modifier.basicButton()) { viewModel.onRegister(openAndPopUp) }},
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
            Text(text = "Welkom!")
            CardChooserDropDown()
            Text(text = uiState.statusmessage)
            OutlinedTextField(
                value = uiState.cardnumber,
                label = { Text("Kaartnummer") },
                onValueChange = viewModel::onCardNumberChange
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
        backgroundColor = MaterialTheme.colors.background,
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "GigaCharge",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
fun CardChooserDropDown() {
    Box {
        var mExpanded by remember { mutableStateOf(false) }
        val mCardDealers = listOf("MobilityPlus", "Blue Corner")
        var mSelectedText by remember { mutableStateOf(mCardDealers.first()) }
        var mTextFieldSize by remember { mutableStateOf(Size.Zero) }
        val icon = if (mExpanded)
            Icons.Filled.KeyboardArrowUp
        else
            Icons.Filled.KeyboardArrowDown
        OutlinedTextField(
            value = mSelectedText,
            readOnly = true,
            onValueChange = { mSelectedText = it },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    mTextFieldSize = coordinates.size.toSize()
                },
            label = { Text("Kaartdeler") },
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { mExpanded = !mExpanded })
            }
        )
        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                .background(MaterialTheme.colors.background)
        ) {
            mCardDealers.forEach { label ->
                DropdownMenuItem(onClick = {
                    mSelectedText = label
                    mExpanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }
}
/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package be.ugent.gigacharge.common.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyDropdown(
  selection: String,
  options: List<String>,
  onActionClick: (String) -> Unit,
  modifier: Modifier
) {
  var isExpanded by remember { mutableStateOf(false) }
  var selectionState by remember { mutableStateOf(selection) }

  Box(modifier) {
    // BUTTON
    Button(
      { isExpanded = true },
      Modifier.fillMaxWidth().height(50.dp),
      colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.background)
    ) {
      Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(selectionState, Modifier.weight(0.9F), color = MaterialTheme.colors.onBackground)
        Icon(if (isExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown, "less", Modifier.weight(0.1F))
      }
    }
    // DROPDOWN
    DropdownMenu(
      isExpanded,
      { isExpanded = false }
    ) {
      options.forEach { o:String ->
        DropdownMenuItem({
          selectionState = o
          isExpanded = false
          onActionClick(o)
        }) {
          Text(o, color = MaterialTheme.colors.onBackground)
        }
      }
    }
  }
}

@Preview
@Composable
fun MyDropDownPreview() {
  MyDropdown("label", listOf("a", "b", "c", "d", "e"), {s:String ->}, Modifier)
}

@Composable
@ExperimentalMaterialApi
fun DropdownContextMenu(
  options: List<String>,
  modifier: Modifier,
  onActionClick: (String) -> Unit,
  expanded: Boolean = false
) {
  var isExpanded by remember { mutableStateOf(expanded) }

  ExposedDropdownMenuBox(
    expanded = isExpanded,
    modifier = modifier,
    onExpandedChange = { isExpanded = !isExpanded }
  ) {
    Icon(
      modifier = Modifier.padding(8.dp, 0.dp),
      imageVector = Icons.Default.MoreVert,
      contentDescription = "More"
    )

    ExposedDropdownMenu(
      modifier = Modifier.width(180.dp),
      expanded = isExpanded,
      onDismissRequest = { isExpanded = false }
    ) {
      options.forEach { selectionOption ->
        DropdownMenuItem(
          onClick = {
            isExpanded = false
            onActionClick(selectionOption)
          }
        ) {
          Text(text = selectionOption)
        }
      }
    }
  }
}

@Composable
@ExperimentalMaterialApi
fun DropdownSelector(
  @StringRes label: Int,
  options: List<String>,
  selection: String,
  modifier: Modifier,
  onNewValue: (String) -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
    expanded = isExpanded,
    modifier = modifier,
    onExpandedChange = { isExpanded = !isExpanded }
  ) {
    TextField(
      modifier = Modifier.fillMaxWidth(),
      readOnly = true,
      value = selection,
      onValueChange = {},
      label = { Text(stringResource(label)) },
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(isExpanded) },
      colors = dropdownColors()
    )

    ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
      options.forEach { selectionOption ->
        DropdownMenuItem(
          onClick = {
            onNewValue(selectionOption)
            isExpanded = false
          }
        ) {
          Text(text = selectionOption)
        }
      }
    }
  }
}

@Composable
@ExperimentalMaterialApi
private fun dropdownColors(): TextFieldColors {
  return ExposedDropdownMenuDefaults.textFieldColors(
    backgroundColor = MaterialTheme.colors.onPrimary,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    trailingIconColor = MaterialTheme.colors.onSurface,
    focusedTrailingIconColor = MaterialTheme.colors.onSurface,
    focusedLabelColor = MaterialTheme.colors.primary,
    unfocusedLabelColor = MaterialTheme.colors.primary
  )
}

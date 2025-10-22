package com.tayyipgunay.firststajproject.presentation.add

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tayyipgunay.firststajproject.domain.model.ModelTypeUi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import com.tayyipgunay.firststajproject.presentation.common.events.MessageType
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import com.tayyipgunay.firststajproject.presentation.common.events.MessageChannel
import com.tayyipgunay.firststajproject.presentation.common.events.UiEvent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

// --- Model type sabiti (gerekirse domain'e taşı)
private val MODEL_TYPES = listOf(
    ModelTypeUi("1", "Giyim / Moda"),
    ModelTypeUi("2", "Elektronik"),
    ModelTypeUi("3", "Mobilya / Bebek"),
    ModelTypeUi("4", "Kozmetik / Makyaj"),
    ModelTypeUi("5", "Aksesuar")
)

/* =========================
 *      TOP APP BAR
 * ========================= */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductTopBar() {
    TopAppBar(
        title = { Text("Add Product") }
    )
}

/* =========================
 *         FORM
 * ========================= */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductForm(
    state: AddProductState,
    onIntent: (AddProductIntent) -> Unit
) {
    // Dosya seçiciler
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        onIntent(AddProductIntent.Image(uri))
    }
    val arPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        onIntent(AddProductIntent.ArFile(uri))
    }

    // UI state (yerel) — istersen bunları da VM state’ine taşıyabilirsin
    var modelTypeExpanded by rememberSaveable { mutableStateOf(false) }
    var categoryExpanded by rememberSaveable { mutableStateOf(false) }

    val selectedModelTypeLabel = remember(state.name) {
        MODEL_TYPES.find { it.value == state.modelTypes.toString() }?.label ?: "Select Model Type"
    }
    val selectedCategoryLabel = remember(state.categories, state.selectedCategoryId) {
        state.categories.firstOrNull { it.id == state.selectedCategoryId }?.name ?: "Select Category"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = state.name,
            onValueChange = { onIntent(AddProductIntent.Name(it)) },
            label = { Text("Name*") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.details,
            onValueChange = { onIntent(AddProductIntent.Details(it)) },
            label = { Text("Details") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.priceInput,
            onValueChange = { onIntent(AddProductIntent.Price(it)) }, // VM’de BigDecimal parse + validation
            label = { Text("Price*") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )

        // Model Type (M3 ExposedDropdown)
        ExposedDropdownMenuBox(
            expanded = modelTypeExpanded,
            onExpandedChange = { modelTypeExpanded = !modelTypeExpanded }
        ) {
            OutlinedTextField(
                value = selectedModelTypeLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Model Type*") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = modelTypeExpanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = modelTypeExpanded,
                onDismissRequest = { modelTypeExpanded = false }
            ) {
                MODEL_TYPES.forEach { mt ->
                    DropdownMenuItem(
                        text = { Text(mt.label) },
                        onClick = {
                            modelTypeExpanded = false
                            onIntent(AddProductIntent.ModelType(mt.value))
                        }
                    )
                }
            }
        }

        // Category (M3 ExposedDropdown)
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategoryLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category*") },
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                state.categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            categoryExpanded = false
                            onIntent(AddProductIntent.Category(category.id))
                        }
                    )
                }
            }
        }

        // Active switch
        LabeledSwitchRow(
            label = "Active",
            checked = state.isActive,
            onCheckedChange = { onIntent(AddProductIntent.IsActive(it)) }
        )

        // Dosya butonları
        FilePickRow(
            imageLabel = state.imageUri?.lastPathSegment ?: "Pick Image (optional)",
            onPickImage = { imagePicker.launch("image/*") },
            arLabel = state.arUri?.lastPathSegment ?: "Pick AR (.usdz/.glb) (optional)",
            onPickAr = { arPicker.launch("*/*") } // İlerde MIME/uzantı filtresi ekleyebilirsin
        )
    }
}

/* =========================
 *     REUSABLE COMPOSABLES
 * ========================= */
@Composable
private fun LabeledSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun FilePickRow(
    imageLabel: String,
    onPickImage: () -> Unit,
    arLabel: String,
    onPickAr: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        OutlinedButton(onClick = onPickImage) { Text(imageLabel) }
        OutlinedButton(onClick = onPickAr) { Text(arLabel) }
    }
}

/* =========================
 *          SCREEN
 * ========================= */
@Composable
fun AddProductScreen(
    state: AddProductState,
    onIntent: (AddProductIntent) -> Unit,
    onSavedNavigateBack: () -> Unit,
    events: SharedFlow<AddProductEvent>,
    uiEvents: SharedFlow<UiEvent>
) {
    var showConfirmDialog by remember { mutableStateOf<UiEvent.ShowConfirmDialog?>(null) }
    var showInfoDialog by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scroll = rememberScrollState()

    // UI EVENTS
    LaunchedEffect(Unit) {
        uiEvents.collect { evt ->
            when (evt) {
                is UiEvent.ShowConfirmDialog -> showConfirmDialog = evt
                is UiEvent.ShowMessage -> when (evt.channel) {
                    MessageChannel.Snackbar -> snackbarHostState.showSnackbar(evt.text, duration = SnackbarDuration.Long)
                    MessageChannel.Toast -> Toast.makeText(context, evt.text, Toast.LENGTH_SHORT).show()
                    MessageChannel.Dialog -> showInfoDialog = evt.text
                }
            }
        }
    }

    // DOMAIN/SCREEN EVENTS
    LaunchedEffect(Unit) {
        events.collect { e ->
            when (e) {
                is AddProductEvent.ShowValidationError ->
                    snackbarHostState.showSnackbar(e.message, duration = SnackbarDuration.Long)
                is AddProductEvent.ShowFileError ->
                    snackbarHostState.showSnackbar(e.message, duration = SnackbarDuration.Long)
                AddProductEvent.NavigateBack -> { /* nav */ }
                AddProductEvent.NavigateToProductList -> { /* nav */ }
            }
        }
    }

    // Navigation side-effects
    LaunchedEffect(state.saved) { if (state.saved) onSavedNavigateBack() }
    LaunchedEffect(Unit) { onIntent(AddProductIntent.Init) }

    Scaffold(
        topBar = { AddProductTopBar() },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)          // ✅ innerPadding tek yerde
                .consumeWindowInsets(innerPadding)
                .verticalScroll(scroll)
                .imePadding()
                .navigationBarsPadding()
        ) {
            AddProductForm(state = state, onIntent = onIntent)

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onIntent(AddProductIntent.Save) },
                enabled = !state.isSaving,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(if (state.isSaving) "Kaydediliyor..." else "Kaydet")
            }

            state.error?.let {
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }

    // Confirm Dialog
    showConfirmDialog?.let { dialog ->
        AlertDialog(
            onDismissRequest = { showConfirmDialog = null },
            title = { Text(dialog.title) },
            text = { Text(dialog.message) },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = null
                    onIntent(AddProductIntent.Confirm(dialog.id, true))
                }) { Text(dialog.confirmText) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showConfirmDialog = null
                    onIntent(AddProductIntent.Confirm(dialog.id, false))
                }) { Text(dialog.cancelText) }
            }
        )
    }

    // Info Dialog
    showInfoDialog?.let { msg ->
        AlertDialog(
            onDismissRequest = { showInfoDialog = null },
            title = { Text("Bilgi") },
            text = { Text(msg) },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = null }) { Text("Tamam") }
            }
        )
    }
}

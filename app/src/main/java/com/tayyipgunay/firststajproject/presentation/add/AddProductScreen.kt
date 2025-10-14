package com.tayyipgunay.firststajproject.presentation.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import com.tayyipgunay.firststajproject.presentation.common.events.MessageType
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.tayyipgunay.firststajproject.presentation.common.events.MessageChannel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddProductTopBar() {
    TopAppBar(title =
        { Text("Add Product") }
    )
}

// MessageChannel bazlı mesaj gösterimi: Snackbar/Toast/Dialog

private val MODEL_TYPES = listOf(
    ModelTypeUi("1", "Giyim / Moda"),
    ModelTypeUi("2", "Elektronik"),
    ModelTypeUi("3", "Mobilya / Bebek"),
    ModelTypeUi("4", "Kozmetik / Makyaj"),
    ModelTypeUi("5", "Aksesuar")
)


// ---------- Form ----------
@Composable
private fun AddProductForm(
    state: AddProductState,
    onIntent: (AddProductIntent) -> Unit
) {
    val pickImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            onIntent(AddProductIntent.Image(uri))
        }
    val pickAr =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            onIntent(AddProductIntent.ArFile(uri))
        }
    var expandedDropid by remember { mutableStateOf(false) }
    var CategoriText by remember { mutableStateOf("Select Category") }

    var ModelTypeText by remember { mutableStateOf("Select Model Type") }
    var expandedDropType by remember { mutableStateOf(false) }



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        OutlinedTextField(
            value = state.name,
            onValueChange = {
                onIntent(AddProductIntent.Name(it))
            },
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
            onValueChange = { onIntent(AddProductIntent.Price(it)) },
            label = { Text("Price*") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Box{
            // Tetikleyici buton
            OutlinedButton(onClick = { expandedDropType = true }) {
                Text(ModelTypeText)
            }

            // Menü
            DropdownMenu(
                expanded = expandedDropType,
                onDismissRequest = { expandedDropType = false }
            ) {
               // state.categories.forEach { category ->
                MODEL_TYPES.forEach { modelType ->
                    DropdownMenuItem(
                        text = { Text(modelType.label) },
                        onClick = {
                            expandedDropType = false
                            onIntent(AddProductIntent.ModelType(modelType.value))

                            ModelTypeText=modelType.label

                        }
                    )
                }
            }
        }

        /*OutlinedTextField(
            value = state.modelType,
            onValueChange = { onIntent(AddProductIntent.ModelType(it)) },
            label = { Text("Model Type*") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )*/



        Spacer(Modifier.height(9.dp))
        Box {
            // Tetikleyici buton
            OutlinedButton(onClick = { expandedDropid = true }) {
                Text(CategoriText)
            }

            // Menü
            DropdownMenu(
                expanded = expandedDropid,
                onDismissRequest = { expandedDropid = false }
            ) {
                println("🔍 Kategori dropdown açıldı - categories count: ${state.categories.size}")
                state.categories.forEach { category ->
                    println("🔍 Kategori: ${category.name} (ID: ${category.id})")
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            println("🔍 Kategori seçildi: ${category.name} (ID: ${category.id})")
                            expandedDropid = false
                            onIntent(AddProductIntent.Category(category.id))
                            CategoriText=category.name
                        }
                    )
                }
            }
        }
    }




        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Active")
            Switch(
                checked = state.isActive,
                onCheckedChange = { onIntent(AddProductIntent.IsActive(it)) }
            )
        }

       Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { pickImage.launch("image/*") }) {
                Text(state.imageUri?.lastPathSegment ?: "Pick Image (optional)")
            }
            OutlinedButton(onClick = { pickAr.launch("*/*") }) {
                Text(state.arUri?.lastPathSegment ?: "Pick AR (.usdz/.glb) (optional)")
            }
        }
}

// ---------- Screen ----------
@Composable
fun AddProductScreen(
    state: AddProductState,
    onIntent: (AddProductIntent) -> Unit,
    onSavedNavigateBack: () -> Unit,
    events: kotlinx.coroutines.flow.SharedFlow<AddProductEvent>
) {
    var showConfirmDialog by remember { mutableStateOf<AddProductEvent.ShowConfirmDialog?>(null) }
    var showInfoDialog by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Event handling
    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is AddProductEvent.ShowConfirmDialog -> {
                    showConfirmDialog = event
                }

                is AddProductEvent.ShowMessage -> {
                    println("📱 Mesaj: ${event.text} (${event.type}) [${event.channel}]")
                    // Kanal bazlı mesaj gösterimi
                    when (event.channel) {
                        MessageChannel.Snackbar -> {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = event.text,
                                    duration = SnackbarDuration.Long
                                )
                            }
                        }
                        MessageChannel.Toast -> {
                            Toast.makeText(context, event.text, Toast.LENGTH_SHORT).show()
                        }
                        MessageChannel.Dialog -> {
                            showInfoDialog = event.text
                        }
                    }
                }

                is AddProductEvent.NavigateBack -> {
                    onSavedNavigateBack()
                }

                AddProductEvent.NavigateToProductList -> {
                    onSavedNavigateBack()
                }
                is AddProductEvent.ShowFileError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
                is AddProductEvent.ShowValidationError -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            }

        }
    }
    LaunchedEffect(state.saved) {
        if (state.saved) onSavedNavigateBack()
    }
    LaunchedEffect(Unit) {
        onIntent(AddProductIntent.Init)
    }

    val scroll = rememberScrollState()

    Scaffold(
        topBar = { AddProductTopBar() },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { inner ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .verticalScroll(scroll)   // 🔹 içerik taşarsa kaydır
                    .imePadding()             // 🔹 klavye açılınca alttan yer aç
                    .navigationBarsPadding()  // (opsiyonel) alt gesture/nav bar için
            ) {
            AddProductForm(state = state, onIntent = onIntent)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    onIntent(AddProductIntent.Save)
                },
                enabled = !state.isSaving,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(if (state.isSaving){
                    "Kaydediliyor..."
                } else {
                    "Kaydet"
                }
                )
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
                TextButton(
                    onClick = {
                        showConfirmDialog = null
                        onIntent(AddProductIntent.Confirm(dialog.id, true))
                    }
                ) {
                    Text(dialog.confirmText)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = null
                        onIntent(AddProductIntent.Confirm(dialog.id, false))
                    }
                ) {
                    Text(dialog.cancelText)
                }
            }
        )
    }

    // Info Dialog (MessageChannel.Dialog için)
    showInfoDialog?.let { msg ->
        AlertDialog(
            onDismissRequest = { showInfoDialog = null },
            title = { Text("Bilgi") },
            text = { Text(msg) },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = null }) {
                    Text("Tamam")
                }
            }
        )
    }
}
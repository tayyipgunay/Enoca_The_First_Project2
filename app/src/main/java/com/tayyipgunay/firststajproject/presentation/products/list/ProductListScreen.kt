package com.tayyipgunay.firststajproject.presentation.products.list

import androidx.compose.foundation.MutatePriority
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tayyipgunay.firststajproject.presentation.ui.components.PillChip
import com.tayyipgunay.firststajproject.presentation.ui.components.StatusBadge
import com.tayyipgunay.firststajproject.presentation.ui.state.EmptyStateCard
import com.tayyipgunay.firststajproject.presentation.ui.state.ErrorStateCard
import com.tayyipgunay.firststajproject.presentation.ui.state.LoadingStateCard
import androidx.compose.runtime.*
import com.tayyipgunay.firststajproject.core.util.Constants
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.tayyipgunay.firststajproject.domain.model.ProductSummary
import com.tayyipgunay.firststajproject.domain.model.Product
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.tayyipgunay.firststajproject.presentation.common.events.MessageChannel
// =====================================================
// ================  PUBLIC API: SCREEN  ===============
// =====================================================

@Composable
fun ProductListScreen(
    state: ProductListState,
    onIntent: (ProductListIntent) -> Unit,
    onAddClick: () -> Unit,
    events: kotlinx.coroutines.flow.SharedFlow<ProductListEvent>? = null
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = state.isRefreshing)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showInfoDialog by remember { mutableStateOf<String?>(null) }
    
    // Event handling - kanal bazlı mesaj gösterimi
    LaunchedEffect(Unit) {
        events?.collect { event ->
            when (event) {
                is ProductListEvent.ShowMessage -> {
                    when (event.channel) {
                        MessageChannel.Snackbar -> {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = event.text,
                                    duration = SnackbarDuration.Short,
                                    withDismissAction = true
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
                else -> { /* Handle other events if needed */ }
            }
        }
    }
    
    Scaffold(
        topBar = {
            ProductListTopBar(
                selectedSort = state.selectedSort,
                onSelectSort = { sort -> onIntent(ProductListIntent.ChangeSort(sort)) },
                onSelectComboSort = { combo -> onIntent(ProductListIntent.ChangeSortRaw(combo)) },
                onAddClick = onAddClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { onIntent(ProductListIntent.Refresh) }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // ---- Controls: Page / Size ----
                PageSizeControls(
                    page = state.page,
                    size = state.size,
                    onNextPage = { 
                        // Sadece mevcut sayfa size'dan küçükse next page'e git
                        if (state.items.size >= state.size) {
                            onIntent(ProductListIntent.ChangePage(state.page + 1))
                        }
                    },
                    onCycleSize = {
                        val options = listOf(10, 20, 50)
                        val idx = options.indexOf(state.size).takeIf { it >= 0 } ?: 0
                        onIntent(ProductListIntent.ChangeSize(options[(idx + 1) % options.size]))
                    },
                    hasMorePages = state.hasMorePages // Sonraki sayfa var mı?
                )

                // ---- Content ----
                when {
                    state.isLoading && !state.isRefreshing -> LoadingStateCard()
                    state.error != null -> ErrorStateCard(
                        message = state.error,
                        onRetry = { onIntent(ProductListIntent.Retry) }
                    )
                    state.items.isEmpty() -> EmptyStateCard()
                    else -> ProductList(
                        items = state.items,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
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

// =====================================================
// ====================  TOP BAR  ======================
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListTopBar(
    selectedSort: ProductSort,
    onSelectSort: (ProductSort) -> Unit,
    onSelectComboSort: (List<String>) -> Unit,
    onAddClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Products") },
        actions = {
            SortMenu(
                selected = selectedSort,
                onSelect = onSelectSort,
                onSelectCombo = onSelectComboSort
            )
            Button(
                onClick = onAddClick,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("+ Add")
            }
        }
    )
}

// =====================================================
// =================  SORT DROPDOWN  ===================
// =====================================================

@Composable
fun SortMenu(
    selected: ProductSort,
    onSelect: (ProductSort) -> Unit,        // tekli sıralama (enum)
    onSelectCombo: (List<String>) -> Unit   // hibrit sıralama (çoklu query)
) {
    // Menü açık/kapalı durumu
    var expanded by remember { mutableStateOf(false) }

    // Tekli (basic) sıralama seçenekleri
    val basicOptions = remember {
        listOf(
           // ProductSort.NAME_ASC,
            //ProductSort.NAME_DESC,
            ProductSort.PRICE_ASC,
            ProductSort.PRICE_DESC,
            ProductSort.ACTIVE_FIRST,
            ProductSort.PASSIVE_FIRST
        )
    }

    Box {
        // Şu anki seçimi gösteren buton
        TextButton(onClick = { expanded = true }
        )
        {
            Text(
                text = selected.label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Açılır menü
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Tekli seçenekler
            basicOptions.forEach { option->
                SortMenuItem(
                    text = option.label,
                    checked = option == selected,
                    onClick = {
                        expanded = false
                        onSelect(option) // ViewModel → ChangeSort(ProductSort)
                    }
                )
            }

            Divider()//----

            // Hibrit presetler (çoklu sort)
            SortMenuItem(
                text = ProductSort.ACTIVE_AND_CHEAP.label,
                checked = false,
                onClick = {
                    expanded = false
                    onSelectCombo(ProductSort.ACTIVE_AND_CHEAP.query)
                }
            )
            SortMenuItem(
                text = ProductSort.ACTIVE_AND_EXPENSIVE.label,
                checked = false,
                onClick = {
                    expanded = false
                    onSelectCombo(ProductSort.ACTIVE_AND_EXPENSIVE.query)
                }
            )
        }
    }
}

@Composable
private fun SortMenuItem(
    text: String,
    checked: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(text) },
        trailingIcon = { if (checked) Icon(Icons.Default.Check, contentDescription = null) },
        onClick = onClick
    )
}

// =====================================================
// ===================  LIST & ITEM  ===================
// =====================================================

@Composable
private fun ProductList(
    items: List<ProductSummary>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(
            items = items,
            key = { it.id } // performans/animasyon için Listeyi çizdiğinde her elemanın kimliğini belirtiyorsun.

        ) { product ->
            ProductListItem(product)
        }
        item { Spacer(Modifier.height(64.dp)) }
    }
}

@Composable
fun ProductListItem(
    productSummary: ProductSummary,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top row: Image and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = Constants.BASE_URL.trimEnd('/') + "/" + (productSummary.image
                        ?: "").trimStart('/'),
                    contentDescription = productSummary.name,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                StatusBadge(productSummary.isActive)
            }

            Spacer(Modifier.height(16.dp))

            // Product name - full width, no restrictions
            Text(
                text = productSummary.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = Int.MAX_VALUE, // No line limit
                overflow = TextOverflow.Visible, // Show all text
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Price row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (productSummary.price == productSummary.price.toLong().toDouble()) {
                        productSummary.price.toLong().toString() + " TL"
                    } else {
                        String.format("%.2f TL", productSummary.price)
                    },
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


// =====================================================
// ================  SMALL UTIL COMPOSABLES  ===========
// =====================================================

@Composable
private fun PageSizeControls(
    page: Int,
    size: Int,
    onNextPage: () -> Unit,
    onCycleSize: () -> Unit,
    hasMorePages: Boolean = true
) {
    Row(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = false,
            onClick = onNextPage,
            enabled = hasMorePages,
            label = { Text("Page ${page + 1}") }
        )
        PillChip(
            label = "Size $size",
            onClick = onCycleSize
        )
    }
}


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
import com.tayyipgunay.firststajproject.presentation.common.events.UiEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.emptyFlow
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState


// =====================================================
// ================  PUBLIC API: SCREEN  ===============
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    state: ProductListState,
    onIntent: (ProductListIntent) -> Unit,
    onAddClick: () -> Unit,
    events: SharedFlow<ProductListEvent>,           // (Şimdilik kullanılmıyor ama public API için bırakıldı)
    uiEvents: SharedFlow<UiEvent>
) {
    //val swipeRefreshState = rememberPullRefreshState(state.isRefreshing, onRefresh =)
    val pullToRefreshState = rememberPullToRefreshState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var showInfoDialog by remember { mutableStateOf<String?>(null) }

    // UI Event'leri tek bir launch bloğunda topla
    LaunchedEffect(Unit) {
        uiEvents.collect { uiEvents ->
            println("UI Event: ${uiEvents}")
            when (uiEvents) {
                is UiEvent.ShowMessage -> {
                    when (uiEvents.channel) {
                        MessageChannel.Snackbar -> {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = uiEvents.text,
                                    duration = SnackbarDuration.Long,

                                )
                            }
                        }
                        MessageChannel.Toast -> {
                            Toast.makeText(context, uiEvents.text, Toast.LENGTH_LONG).show()
                        }
                        MessageChannel.Dialog -> {
                            showInfoDialog = uiEvents.text
                        }
                    }
                }
                is UiEvent.ShowConfirmDialog -> {
                    // Gerekirse burada confirm dialog tetiklenir
                }
            }
        }
    }

    Scaffold(
        topBar = {
            ProductListTopBar(
                selectedSort = state.selectedSort,
                onSelectSort = { sort -> onIntent(ProductListIntent.ChangeSort(sort))
                               },
                onSelectComboSort = { combo -> onIntent(ProductListIntent.ChangeSortRaw(combo)) },
                onAddClick = onAddClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = { onIntent(ProductListIntent.Refresh) },
            state = pullToRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    //.padding(padding)
            ) {
                // ---- Controls: Page / Size ----
                PageSizeControls(
                    page = state.page,
                    size = state.size,
                    onNextPage = {
                        // Sayfada en az "size" kadar öğe varsa ve hasMorePages true ise ileri git
                        if (state.items.size >= state.size && state.hasMorePages) {
                            onIntent(ProductListIntent.ChangePage(state.page + 1))
                        }
                    },
                    onCycleSize = {
                        val options = listOf(10, 20, 50)
                        val idx = options.indexOf(state.size).takeIf { it >= 0 } ?: 0
                        onIntent(ProductListIntent.ChangeSize(options[(idx + 1) % options.size]))
                    },
                    hasMorePages = state.hasMorePages
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
            TextButton(
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
    var expanded by remember { mutableStateOf(false) }

    val basicOptions = remember {
        listOf(
            ProductSort.PRICE_ASC,
            ProductSort.PRICE_DESC,
            ProductSort.ACTIVE_FIRST,
            ProductSort.PASSIVE_FIRST
        )
    }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(
                text = selected.label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Tekli seçenekler
            basicOptions.forEach { option ->
                SortMenuItem(
                    text = option.label,
                    checked = option == selected,
                    onClick = {
                        expanded = false
                        onSelect(option)
                    }
                )
            }

            Divider()

            // Hibrit presetler
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
        trailingIcon = { if (checked) androidx.compose.material3.Icon(Icons.Default.Check, null) },
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
            key = {
                it.id
            }
        ) { product ->
            ProductListItem(product)
        }
        item {
            Spacer(Modifier.height(64.dp))

        }
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
                    model = buildImageUrl(Constants.BASE_URL, productSummary.image),
                    contentDescription = productSummary.name,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )

                StatusBadge(productSummary.isActive)
            }

            Spacer(Modifier.height(16.dp))

            // Product name
            Text(
                text = productSummary.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium
                ),
                maxLines = Int.MAX_VALUE,
                overflow = TextOverflow.Visible,
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
                val priceText = remember(productSummary.price) {
                    formatPriceTRY(productSummary.price)
                }
                Text(
                    text = priceText,
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

// =====================================================
// ======================  UTILS  ======================
// =====================================================

private fun buildImageUrl(baseUrl: String, path: String?): String {
    val safeBase = baseUrl.trimEnd('/')
    val safePath = (path ?: "").trimStart('/')
    return "$safeBase/$safePath"
}

private fun formatPriceTRY(value: Double): String {
    // Türkiye yereli ile her zaman 2 ondalık göster
    val nf = NumberFormat.getCurrencyInstance(Locale("tr", "TR"))
    // Varsayılan olarak "₺" öne gelir. " TL" tercih ediyorsan:
    // return String.format(Locale("tr", "TR"), "%.2f TL", value)
    return nf.format(value)
}
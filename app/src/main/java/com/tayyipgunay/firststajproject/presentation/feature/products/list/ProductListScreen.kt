package com.tayyipgunay.firststajproject.presentation.feature.products.list

import androidx.compose.ui.platform.LocalContext
import com.tayyipgunay.firststajproject.presentation.common.events.MessageChannel

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil.compose.AsyncImage

import com.tayyipgunay.firststajproject.core.util.Constants
import com.tayyipgunay.firststajproject.domain.model.ProductSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen2(
    state: ProductListContract.State,
    lazyPagingItems: LazyPagingItems<ProductSummary>,
    onIntent: (ProductListContract.Intent) -> Unit,
    effectFlow: Flow<ProductListContract.Effect>,
    navController: NavController
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var dialogMessage by remember { mutableStateOf<String?>(null) }

    // ---- EFFECT LISTENER (ViewModel → UI tek seferlik olaylar) ----
    LaunchedEffect(Unit) {
        effectFlow.collect { effect ->
            when (effect) {

                is ProductListContract.Effect.ShowMessage -> {
                    when (effect.channel) {

                        MessageChannel.Snackbar ->
                            snackbarHostState.showSnackbar(effect.text)

                        MessageChannel.Toast ->
                            Toast.makeText(context, effect.text, Toast.LENGTH_SHORT).show()

                        MessageChannel.Dialog ->
                            dialogMessage = effect.text
                    }
                }

                ProductListContract.Effect.NavigateToAdd ->
                    navController.navigate(Constants.ADD_PRODUCT)
            }
        }
    }

    // ---- PAGING LOADSTATE OBSERVER (refresh hatası → VM Intent) ----
    LaunchedEffect(lazyPagingItems) {
        snapshotFlow { lazyPagingItems.loadState.refresh }
            .distinctUntilChanged()
            .collect { refreshState ->
                if (refreshState is LoadState.Error) {
                    onIntent(ProductListContract.Intent.PagingError(refreshState.error))
                }
            }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        topBar = {
            ProductListTopBar(
                selectedSort = state.selectedSort,
                onIntent = onIntent
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            ProductListContent(
                lazyPagingItems = lazyPagingItems,
                errorMessage = state.error,
                onIntent = onIntent
            )

            dialogMessage?.let { msg ->
                InfoDialog(
                    message = msg,
                    onDismiss = { dialogMessage = null }
                )
            }
        }
    }
}

// =======================================================================
//  CONTENT
// =======================================================================
@Composable
private fun ProductListContent(
    lazyPagingItems: LazyPagingItems<ProductSummary>,
    errorMessage: String?,
    onIntent: (ProductListContract.Intent) -> Unit
) {
    val refresh = lazyPagingItems.loadState.refresh
    val append = lazyPagingItems.loadState.append
    val itemCount = lazyPagingItems.itemCount

    // ---- REFRESH (İLK SAYFA) DURUMLARI ----
    when {
        refresh is LoadState.Error && itemCount == 0 -> {
            CenteredError(
                error = errorMessage ?: "Bir hata oluştu",
                onRetry = { lazyPagingItems.retry() }
            )
            return
        }

        refresh is LoadState.Loading && itemCount == 0 -> {
            CenteredLoading()
            return
        }

        refresh is LoadState.NotLoading && itemCount == 0 -> {
            CenteredEmpty()
            return
        }
    }

    // ---- LIST ----
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(itemCount) { index ->
            val product = lazyPagingItems[index]

            if (product != null) {
                ProductListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize()
                        .clickable {
                            onIntent(ProductListContract.Intent.ItemClick(product.id))
                        },
                    productSummary = product
                )
            } else {
                ProductListItemPlaceholder()
            }
        }

        // ---- APPEND STATE (SONRAKİ SAYFA) ----
        item {
            when (append) {
                is LoadState.Loading ->
                    CenteredAppendLoading()

                is LoadState.Error ->
                    AppendError(
                        error = append.error.localizedMessage ?: "Daha fazla ürün yüklenemedi",
                        onRetry = { lazyPagingItems.retry() }
                    )

                else -> Unit
            }
        }
    }
}


// =======================================================================
//  TOP BAR
// =======================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListTopBar(
    selectedSort: ProductSort,
    onIntent: (ProductListContract.Intent) -> Unit
) {
    Surface(
        color = Color(0xFFF2F2F7),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "Products",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        color = Color(0xFF1C1C1E)
                    )

                    Text(
                        "Manage your inventory",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                        color = Color(0xFF8E8E93)
                    )
                }

                Surface(
                    onClick = { onIntent(ProductListContract.Intent.AddClick) },
                    shape = RoundedCornerShape(50),
                    color = Color(0xFF007AFF)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Add",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            SortMenu(
                selected = selectedSort,
                onSelect = { onIntent(ProductListContract.Intent.ChangeSort(it)) }
            )
        }
    }
}

@Composable
fun SortMenu(
    selected: ProductSort,
    onSelect: (ProductSort) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        onClick = { expanded = true },
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceContainerHighest,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                selected.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        ProductSort.values().forEach { sort ->
            DropdownMenuItem(
                text = { Text(sort.label, style = MaterialTheme.typography.bodyLarge) },
                onClick = {
                    expanded = false
                    onSelect(sort)
                },
                leadingIcon = if (sort == selected) {
                    {
                        Icon(
                            Icons.Default.Check, null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else null
            )
        }
    }
}

// =======================================================================
//  PRODUCT ITEM
// =======================================================================

@Composable
fun ProductListItem(
    modifier: Modifier = Modifier,
    productSummary: ProductSummary
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.Top
        ) {

            val imageUrl = productSummary.image?.let {
                "http://37.156.246.102:9082/${it.trimStart('/')}"
            }

            AsyncImage(
                model = imageUrl,
                contentDescription = productSummary.name,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFF2F2F7)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = productSummary.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "₺${productSummary.price}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 17.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    val isActive = productSummary.isActive ?: false
                    val (bg, textClr, label) =
                        if (isActive) {
                            Triple(
                                Color(0xFF34C759).copy(alpha = 0.18f),
                                Color(0xFF34C759),
                                "Active"
                            )
                        } else {
                            Triple(
                                Color(0xFFFF3B30).copy(alpha = 0.18f),
                                Color(0xFFFF3B30),
                                "Passive"
                            )
                        }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = bg
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = textClr
                        )
                    }
                }
            }
        }
    }
}

// =======================================================================
//  DIALOG & HELPERS
// =======================================================================

@Composable
private fun InfoDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bilgi", style = MaterialTheme.typography.titleLarge) },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tamam")
            }
        }
    )
}

@Composable
private fun CenteredLoading() =
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }

@Composable
private fun CenteredError(error: String, onRetry: () -> Unit) =
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            FilledTonalButton(onClick = onRetry) {
                Text("Tekrar Dene")
            }
        }
    }

@Composable
private fun CenteredEmpty() =
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outlineVariant
            )
            Text(
                "No products yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Add your first product to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

@Composable
private fun CenteredAppendLoading() =
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(32.dp))
    }

@Composable
private fun AppendError(error: String, onRetry: () -> Unit) =
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
            OutlinedButton(
                onClick = onRetry,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onErrorContainer)
            ) {
                Text("Tekrar Dene")
            }
        }
    }

@Composable
private fun ProductListItemPlaceholder() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHighest,
                        MaterialTheme.shapes.medium
                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainerHighest,
                            MaterialTheme.shapes.extraSmall
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .height(16.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                            MaterialTheme.shapes.extraSmall
                        )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 28.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerHighest,
                        MaterialTheme.shapes.small
                    )
            )
        }
    }
}

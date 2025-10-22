package com.tayyipgunay.firststajproject.presentation.products.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tayyipgunay.firststajproject.core.error.userMessageFor
import com.tayyipgunay.firststajproject.core.mvi.MVIComponent
import com.tayyipgunay.firststajproject.core.util.Constants
import com.tayyipgunay.firststajproject.core.util.Resource
import com.tayyipgunay.firststajproject.domain.repository.ProductRepository
import com.tayyipgunay.firststajproject.domain.usecase.GetProductUseCase
import com.tayyipgunay.firststajproject.presentation.common.events.MessageType
import com.tayyipgunay.firststajproject.presentation.common.events.MessageChannel
import com.tayyipgunay.firststajproject.presentation.common.events.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductUseCase,
) : ViewModel(),MVIComponent<ProductListIntent, ProductListState, ProductListEvent, UiEvent> {

    private val _state = MutableStateFlow(ProductListState())
 override   val state = _state.asStateFlow()

    // ✅ Event (tek seferlik olay) - EKLENECEK
    private val _event = MutableSharedFlow<ProductListEvent>()
    override val event: SharedFlow<ProductListEvent> = _event.asSharedFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    override val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()





    init {
       loadProducts()
    }

   override fun onIntent(intent: ProductListIntent) {
        when (intent) {
            is ProductListIntent.Load -> loadProducts()
            is ProductListIntent.Retry -> loadProducts()
            is ProductListIntent.Refresh -> refreshProducts()
           is ProductListIntent.ChangePage -> changePage(intent.page)
           is ProductListIntent.ChangeSize -> changeSize(intent.size)
            is ProductListIntent.ChangeSort -> changeSort(intent.sort)
           is ProductListIntent.ChangeSortRaw -> changeSortRaw(intent.sort)

           // is ProductListIntent.AddClicked -> ProductListEvent.NavigateToAddProduct

        }
    }



    private fun loadProducts(isNewPage: Boolean = false) {
        val currentState = state.value
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    // Yeni sayfa değilse items'ı koru
                    items = if (isNewPage) it.items else emptyList()
                )
            }

            println("🔍 Loading products: page=${currentState.page}, size=${currentState.size}, isNewPage=$isNewPage")

            getProductsUseCase.Execute(
                page = currentState.page,
                size = currentState.size,
                sort = currentState.sort
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val newItems = result.data ?: emptyList()
                        println("✅ Products loaded: ${newItems.size} items")

                        _state.update {
                            val updatedItems = if (isNewPage && currentState.page > 0) {
                                // Yeni sayfa ekle
                                it.items + newItems
                            } else {
                                // Yeni liste (ilk sayfa veya refresh)
                                newItems
                            }

                            it.copy(
                                isLoading = false,
                                items = updatedItems,
                                error = null,
                                hasMorePages = newItems.size >= currentState.size // Sonraki sayfa var mı?
                            )
                        }

                        _uiEvent.emit(
                            UiEvent.ShowMessage(
                                text = "Ürünler yüklendi",
                                type = MessageType.Success,
                                channel = MessageChannel.Toast
                            )
                        )
                    }

                    is Resource.Error -> {
                        val msg = userMessageFor(result.error)  // <<< BURASI KRİTİK

                        println("❌ Error loading products: " +msg)

                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = msg
                                // items'ı koru, sıfırlama
                            )
                        }

                        _uiEvent.emit(
                            UiEvent.ShowMessage(
                                text = "Ürünler yüklenemedi: ${result.message}",
                                type = MessageType.Error,
                                channel = MessageChannel.Snackbar
                            )
                        )
                    }

                    is Resource.Loading -> {
                        _state.update { it.copy(isLoading = true, error = null) }
                    }
                }
            }



        }
    }

    private fun changePage(page: Int) {
        val currentPage = state.value.page
        _state.update { it.copy(page = page) }
        // Yeni sayfa ise items'ı koru, ilk sayfaya dönüyorsa sıfırla
        loadProducts(isNewPage = page > currentPage)
    }

    private fun changeSize(size: Int) {
        _state.update { it.copy(size = size, page = 0) }
        // Size değişince ilk sayfaya dön, items'ı sıfırla
        loadProducts(isNewPage = false)
    }

    private fun changeSort(sort: ProductSort) {

        _state.update {
            it.copy(selectedSort = sort, sort = sort.query, page = 0)
        }

        loadProducts(isNewPage = false)
    }

    private fun changeSortRaw(sort: List<String>) {
        _state.update { it.copy(sort = sort, page = 0)         }
        loadProducts(isNewPage = false)
    }

    private fun refreshProducts() {
        val currentState = state.value
        viewModelScope.launch {
            try {
                _state.update { 
                    it.copy(
                        isRefreshing = true, 
                        error = null
                    ) 
                }
                
                println("🔄 Refreshing products: page=0, size=${currentState.size}")
                
                getProductsUseCase.Execute(
                    page = 0,
                    size = currentState.size,
                    sort = currentState.sort
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val newItems = result.data ?: emptyList()
                            println("✅ Products refreshed: ${newItems.size} items")
                            
                            _state.update {
                                it.copy(
                                    isRefreshing = false,
                                    items = newItems,
                                    error = null,
                                    page = 0,
                                    hasMorePages = newItems.size >= currentState.size
                                )
                            }
                            
                            _uiEvent.emit(
                                UiEvent.ShowMessage(
                                    text = "Yenilendi",
                                    type = MessageType.Success,
                                    channel = MessageChannel.Toast
                                )
                            )
                        }
                        
                        is Resource.Error -> {
                            println("❌ Error refreshing products: ${result.message}")
                            
                            _state.update {
                                it.copy(
                                    isRefreshing = false,
                                    error = result.message
                                )
                            }
                            
                            _uiEvent.emit(
                                UiEvent.ShowMessage(
                                    text = "Yenileme başarısız: ${result.message}",
                                    type = MessageType.Error,
                                    channel = MessageChannel.Snackbar
                                )
                            )
                        }
                        
                        is Resource.Loading -> {
                            _state.update { it.copy(isRefreshing = true, error = null) }
                        }
                    }
                }
            } catch (e: Exception) {
                println("❌ Exception in refreshProducts: ${e.message}")
                _state.update {
                    it.copy(
                        isRefreshing = false,
                        error = "İnternet bağlantısı hatası"
                    )
                }
                _uiEvent.emit(
                    UiEvent.ShowMessage(
                        text = "İnternet bağlantısı hatası",
                        type = MessageType.Error,
                        channel = MessageChannel.Snackbar
                    )
                )
            }
        }
    }
}
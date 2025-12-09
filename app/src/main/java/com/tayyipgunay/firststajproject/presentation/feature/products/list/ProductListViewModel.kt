package com.tayyipgunay.firststajproject.presentation.feature.products.list

import com.tayyipgunay.firststajproject.domain.usecase.GetProductUseCase2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.tayyipgunay.firststajproject.core.error.AppException
import com.tayyipgunay.firststajproject.core.error.toUserMessage
import com.tayyipgunay.firststajproject.presentation.common.events.MessageChannel
import com.tayyipgunay.firststajproject.presentation.common.events.MessageType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductUseCase2,
    private val reducer: ProductListReducer
) : ViewModel() {

    private val _state = MutableStateFlow(ProductListContract.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ProductListContract.Effect>(
        replay = 0,
        extraBufferCapacity = 1
    )
    val effect = _effect.asSharedFlow()

    private val _sortTrigger = MutableStateFlow(ProductSort.ACTIVE_FIRST)

    val products = _sortTrigger
        .flatMapLatest { sort ->
            getProductsUseCase.execute(sort.query)
        }
        .cachedIn(viewModelScope)

    fun onIntent(intent: ProductListContract.Intent) {
        when (intent) {

            is ProductListContract.Intent.ChangeSort -> {
                _state.update {
                    println("ProductListViewModel2 intent.sort : "+intent.sort)
                    reducer.reduce(it, ProductListReducer.Result.SortChanged(intent.sort))
                }
                _sortTrigger.value = intent.sort
            }

            ProductListContract.Intent.EndReached -> {
                viewModelScope.launch {
                    _effect.emit(
                        ProductListContract.Effect.ShowMessage(
                            text = "Tüm ürünler yüklendi ✓",
                            type = MessageType.Success,
                            channel = MessageChannel.Snackbar
                        )
                    )
                }
            }

            is ProductListContract.Intent.PagingError -> {
                println("ProductListViewModel2 intent.error : "+intent.error)
                handleError(intent.error)
            }

            ProductListContract.Intent.AddClick -> {
                viewModelScope.launch {
                    _effect.emit(ProductListContract.Effect.NavigateToAdd)
                }
            }

            ProductListContract.Intent.Refresh -> {
                _state.update {
                    reducer.reduce(it, ProductListReducer.Result.ErrorCleared)
                }
            }

            is ProductListContract.Intent.ItemClick -> {

            }
        }
    }

    private fun handleError(throwable: Throwable) {

        val appError = (throwable as? AppException)?.appError
        // ← KRİTİK SATIR

        val userMessage = appError?.toUserMessage() ?: "Bir hata oluştu"

        _state.update {
            reducer.reduce(it, ProductListReducer.Result.ErrorOccurred(userMessage))
        }

        viewModelScope.launch {
            _effect.emit(
                ProductListContract.Effect.ShowMessage(
                    text = userMessage,
                    type = MessageType.Error,
                    channel = MessageChannel.Snackbar
                )
            )
        }
    }
}


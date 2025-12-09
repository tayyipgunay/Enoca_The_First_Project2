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


/*
@HiltViewModel
class ProductListViewModel2 @Inject constructor(
    private val getProductsUseCase: GetProductUseCase2,
    private val reducer: ProductListReducer2,
    private val errorMapper: HttpErrorMapper
) : ViewModel() {

    private val _state = MutableStateFlow(ProductListContract2.State())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ProductListContract2.Effect>()
    val effect = _effect.asSharedFlow()

    private val _sortTrigger = MutableStateFlow(ProductSort.ACTIVE_FIRST)

    val products = _sortTrigger
        .flatMapLatest { sort ->
            getProductsUseCase.execute(sort.query)
        }
        .cachedIn(viewModelScope)

    fun onIntent(intent: ProductListContract2.Intent) {
        when (intent) {

            is ProductListContract2.Intent.ChangeSort -> {
                _state.update { reducer.reduce(it, ProductListReducer2.Result.SortChanged(intent.sort)) }
                _sortTrigger.value = intent.sort
            }

            is ProductListContract2.Intent.ChangeSortRaw -> {
                val matched = ProductSort.entries.find { it.query == intent.sort }
                    ?: _state.value.selectedSort

                _state.update {
                    reducer.reduce(it, ProductListReducer2.Result.SortChanged(matched))
                }
                _sortTrigger.value = matched
            }

            ProductListContract2.Intent.EndReached -> {
                _state.update { reducer.reduce(it, ProductListReducer2.Result.EndReached) }

                viewModelScope.launch {
                    _effect.emit(
                        ProductListContract2.Effect.ShowMessage(
                            text = "Tüm ürünler yüklendi ✓",
                            type = MessageType.Success,
                            channel = MessageChannel.Snackbar
                        )
                    )
                }
            }

            is ProductListContract2.Intent.PagingError -> {
                // val appError=intent.error.toAppError()
                val appError = (intent.error as? AppException)?.appError
                    ?: AppError.Unknown(intent.error)
                val userMessage = appError.toUserMessage()
                //val userMessage = userMessageFor(appError)

                println("PagingErrorviewmodelusermessage : $userMessage")

                _state.update {
                    reducer.reduce(it, ProductListReducer2.Result.ErrorOccurred(userMessage))
                }

                viewModelScope.launch {
                    _effect.emit(
                        ProductListContract2.Effect.ShowMessage(
                            text = userMessage ?: "Bir hata oluştu",
                            type = MessageType.Error,
                            channel = MessageChannel.Snackbar
                        )
                    )
                }
            }

            ProductListContract2.Intent.AddClick -> {
                viewModelScope.launch {
                    _effect.emit(ProductListContract2.Effect.NavigateToAdd)
                }
            }

            ProductListContract2.Intent.Refresh -> Unit
            is ProductListContract2.Intent.ItemClick -> Unit
        }
    }
}*/


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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductUseCase,
    private val reducer: ProductListReducer
) : ViewModel(),
    MVIComponent<ProductListContract.Intent,
            ProductListContract.State,
        ProductListContract.Effect>
{

    private val _state = MutableStateFlow(ProductListContract.State())
    override val state: StateFlow<ProductListContract.State> = _state.asStateFlow()

    // ❗️Tek one-shot hat: Effect
    private val _effect = MutableSharedFlow<ProductListContract.Effect>(replay = 0)
    override val effect: SharedFlow<ProductListContract.Effect> = _effect.asSharedFlow()



    init {
        onIntent(ProductListContract.Intent.Load)
    }

    override fun onIntent(intent: ProductListContract.Intent) {
        when (intent) {
            ProductListContract.Intent.Load -> load(isNewPage = false)
            ProductListContract.Intent.Refresh -> refresh()

            is ProductListContract.Intent.ChangePage -> {
                _state.update { state->
                    reducer.reduce(state, ProductListReducer.Result.PageChanged(intent.page))
                }
                load(isNewPage = intent.page > 0)
            }
            is ProductListContract.Intent.ChangeSize -> {
                _state.update { reducer.reduce(it, ProductListReducer.Result.SizeChanged(intent.size)) }
                load(isNewPage = false)
            }
            is ProductListContract.Intent.ChangeSort -> {
                _state.update { reducer.reduce(it, ProductListReducer.Result.SortChanged(intent.sort)) }
                load(isNewPage = false)
            }
            is ProductListContract.Intent.ChangeSortRaw -> {
                _state.update { reducer.reduce(it, ProductListReducer.Result.SortRawChanged(intent.sort)) }
                load(isNewPage = false)
            }

            // örnek: item tıklama / add butonu
            is ProductListContract.Intent.ItemClick ->
                viewModelScope.launch { _effect.emit(ProductListContract.Effect.NavigateToDetail(intent.id)) }
            ProductListContract.Intent.AddClick ->
                viewModelScope.launch { _effect.emit(ProductListContract.Effect.NavigateToAdd) }
        }
    }

    private fun load(isNewPage: Boolean) {
        viewModelScope.launch {
            _state.update {
                reducer.reduce(it, ProductListReducer.Result.Loading)
            }

            // ⚠️ En güncel snapshot burada alınır
            //val snap = state.value
            getProductsUseCase.Execute(
                page = _state.value.page,
                size = _state.value.size,
                sort = _state.value.sort
            ).collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        _state.update {
                            reducer.reduce(it, ProductListReducer.Result.Loading)
                        }
                    }
                    is Resource.Success -> {
                        val items = res.data.orEmpty()
                        println("gelen ürünler viewmodela : $items")
                        _state.update {
                            reducer.reduce(it, ProductListReducer.Result.Success(items, isNewPage))
                        }
                        _effect.emit(
                            ProductListContract.Effect.ShowMessage(
                                text = "Ürünler yüklendi",
                                channel = MessageChannel.Toast
                            )
                        )
                    }
                    is Resource.Error -> {
                        println(res.error)
                        val msg = userMessageFor(res.error) // tek mesaj kaynağı
                        _state.update {
                            reducer.reduce(it, ProductListReducer.Result.Failure(msg))
                        }
                        _effect.emit(
                            ProductListContract.Effect.ShowMessage(
                                text = "Yüklenemedi: $msg",
                                channel = MessageChannel.Snackbar
                            )
                        )
                    }
                }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _state.update { reducer.reduce(it, ProductListReducer.Result.Refreshing) }

            val snap = state.value
            getProductsUseCase.Execute(
                page = 0,
                size = snap.size,
                sort = snap.sort
            ).collect { res ->
                when (res) {
                    is Resource.Loading ->
                        _state.update { reducer.reduce(it, ProductListReducer.Result.Refreshing) }

                    is Resource.Success -> {
                        val items = res.data.orEmpty()
                        _state.update { cur ->
                            val reset = cur.copy(page = 0)
                            reducer.reduce(reset, ProductListReducer.Result.Success(items, isNewPage = false))
                        }
                        _effect.emit(
                            ProductListContract.Effect.ShowMessage(
                                text = "Yenilendi",
                                channel = MessageChannel.Toast
                            )
                        )
                    }

                    is Resource.Error -> {
                        val msg = userMessageFor(res.error)
                        _state.update { reducer.reduce(it, ProductListReducer.Result.Failure(msg)) }
                        _effect.emit(
                            ProductListContract.Effect.ShowMessage(
                                text = "Yenileme başarısız: $msg",
                                channel = MessageChannel.Snackbar
                            )
                        )
                    }
                }
            }
        }
    }
}
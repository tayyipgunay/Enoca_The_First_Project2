package com.tayyipgunay.firststajproject.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tayyipgunay.firststajproject.core.mvi.MVIComponent
import com.tayyipgunay.firststajproject.core.util.Resource
import com.tayyipgunay.firststajproject.domain.usecase.AddProductUseCase
import com.tayyipgunay.firststajproject.domain.usecase.GetCategoriesUseCase
import com.tayyipgunay.firststajproject.presentation.common.ConfirmId
import com.tayyipgunay.firststajproject.presentation.common.events.MessageType
import com.tayyipgunay.firststajproject.presentation.common.events.MessageChannel
import com.tayyipgunay.firststajproject.presentation.common.events.UiEvent
import com.tayyipgunay.firststajproject.presentation.products.list.ProductListContract
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
class AddProductViewModel @Inject constructor(
    private val addProductUseCase: AddProductUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val reducer: AddProductReducer
) : ViewModel(), MVIComponent<
        AddProductContract.Intent,
        AddProductContract.State,
        AddProductContract.Effect> {

    private val _state = MutableStateFlow(AddProductContract.State())
    override val state: StateFlow<AddProductContract.State> = _state.asStateFlow()

    // ✅ Tek one-shot hat: Effect
    private val _effect = MutableSharedFlow<AddProductContract.Effect>()
    override val effect: SharedFlow<AddProductContract.Effect> = _effect.asSharedFlow()

    override fun onIntent(intent: AddProductContract.Intent) {
        when (intent) {
            // ---- Init / Kategori yükleme
            AddProductContract.Intent.Init,
            AddProductContract.Intent.LoadCategories -> loadCategories()

            // ---- Alan değişimleri
            is AddProductContract.Intent.Name ->
                _state.update {
                    reducer.reduce(
                        it,
                        AddProductReducer.Result.FieldUpdated(name = intent.name)
                    )
                }

            is AddProductContract.Intent.Details ->
                _state.update {
                    reducer.reduce(
                        it,
                        AddProductReducer.Result.FieldUpdated(details = intent.details)
                    )
                }

            is AddProductContract.Intent.IsActive ->
                _state.update {
                    reducer.reduce(
                        it,
                        AddProductReducer.Result.FieldUpdated(isActive = intent.isActive)
                    )
                }

            is AddProductContract.Intent.Price ->
                _state.update {
                    reducer.reduce(
                        it,
                        AddProductReducer.Result.FieldUpdated(price = intent.price)
                    )
                }

            is AddProductContract.Intent.Category ->
                _state.update {
                    reducer.reduce(
                        it,
                        AddProductReducer.Result.FieldUpdated(categoryId = intent.categoryId)
                    )
                }

            is AddProductContract.Intent.ModelType ->
                _state.update {
                    reducer.reduce(
                        it,
                        AddProductReducer.Result.FieldUpdated(modelTypeId = intent.modelType.toString().toInt())
                    )
                }

            is AddProductContract.Intent.Image ->
                _state.update {
                    reducer.reduce(
                        it,
                        AddProductReducer.Result.FieldUpdated(imageUri = intent.uri)
                    )
                }

            is AddProductContract.Intent.ArFile ->
                _state.update {
                    reducer.reduce(
                        it,
                        AddProductReducer.Result.FieldUpdated(arUri = intent.uri)
                    )
                }

            // ---- Hata temizleme / reset
            is AddProductContract.Intent.ClearError ->
                _state.update {
                    reducer.reduce(
                        it,
                        AddProductReducer.Result.FieldErrorCleared(intent.field)
                    )
                }

            AddProductContract.Intent.Reset ->
                _state.update { reducer.reduce(it, AddProductReducer.Result.ResetSaved) }

            // ---- Akışlar
            AddProductContract.Intent.Submit -> submit()

            AddProductContract.Intent.Back ->
                viewModelScope.launch { _effect.emit(AddProductContract.Effect.NavigateBack) }

            is AddProductContract.Intent.Confirm -> {
                when (intent.id) {
                    ConfirmId.SaveProduct -> if (intent.confirmed) submit()
                    else -> { /* diğer confirm türleri burada ele alınır */
                    }
                }
            }

            AddProductContract.Intent.Save -> {

            }
        }
    }


    private fun loadCategories() {
        viewModelScope.launch {
            _state.update { reducer.reduce(it, AddProductReducer.Result.CategoriesLoading) }

            getCategoriesUseCase.Execute(
                page = 0,
                size = 100,
                sort = listOf("id,asc")
            ).collect { res ->
                when (res) {
                    is Resource.Loading ->
                        _state.update { reducer.reduce(it, AddProductReducer.Result.CategoriesLoading) }

                    is Resource.Success ->
                        _state.update {
                            reducer.reduce(it, AddProductReducer.Result.CategoriesSuccess(res.data.orEmpty()))
                        }

                    is Resource.Error ->
                        _state.update {
                            reducer.reduce(
                                it,
                                AddProductReducer.Result.CategoriesFailure(res.message ?: "Kategori yüklenemedi")
                            )
                        }
                }
            }
        }
    }

    private fun submit() {
        val s = state.value

        // Basit validasyon → State.fieldErrors + Effect.ShowMessage
        val price = s.priceInput.toDoubleOrNull()
        when {
            s.name.isBlank() -> {
                flagFieldError(AddProductContract.FieldId.Name, "Lütfen ürün adını girin")
                return
            }
            price == null -> {
                flagFieldError(AddProductContract.FieldId.Price, "Lütfen geçerli bir fiyat girin")
                return
            }
            s.selectedCategoryId.isNullOrBlank() -> {
                flagFieldError(AddProductContract.FieldId.Category, "Lütfen bir kategori seçin")
                return
            }
            s.imageUri == null -> {
                flagFieldError(AddProductContract.FieldId.Image, "Lütfen bir görsel seçin")
                return
            }
            s.arUri == null -> {
                flagFieldError(AddProductContract.FieldId.Image, "Lütfen bir AR dosyası seçin")
                return
            }
        }

        viewModelScope.launch {
            _state.update { reducer.reduce(it, AddProductReducer.Result.SubmitLoading) }

            addProductUseCase.Execute(
                name = s.name,
                details = s.details.ifBlank { null },
                isActive = s.isActive,
                imagePath = s.imageUri?.toString(),
                arFilePath = s.arUri?.toString(),
                price = price!!,
                categoryId = s.selectedCategoryId!!,
                modelType = s.selectedModelTypeId ?: 0,
            ).collect { res ->
                when (res) {
                    is Resource.Loading ->
                        _state.update { reducer.reduce(it, AddProductReducer.Result.SubmitLoading) }

                    is Resource.Success -> {
                        _state.update { reducer.reduce(it, AddProductReducer.Result.SubmitSuccess) }
                        _effect.emit(AddProductContract.Effect.ShowMessage("Ürün eklendi"))
                        _effect.emit(AddProductContract.Effect.NavigateBack) // veya NavigateToProductList
                    }

                    is Resource.Error -> {
                        val msg = res.message ?: "Kayıt başarısız"
                        _state.update { reducer.reduce(it, AddProductReducer.Result.SubmitFailure(msg)) }
                        _effect.emit(AddProductContract.Effect.ShowMessage("Hata: $msg"))
                    }
                }
            }
        }
    }

    private fun flagFieldError(field: AddProductContract.FieldId, message: String) {
        _state.update { reducer.reduce(it, AddProductReducer.Result.FieldErrorSet(field, message)) }
        viewModelScope.launch { _effect.emit(AddProductContract.Effect.ShowMessage(message)) }
    }
}

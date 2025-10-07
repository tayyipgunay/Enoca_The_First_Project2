package com.tayyipgunay.firststajproject.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tayyipgunay.firststajproject.core.mvi.MVIComponent
import com.tayyipgunay.firststajproject.core.util.Resource
import com.tayyipgunay.firststajproject.domain.usecase.AddProductUseCase
import com.tayyipgunay.firststajproject.domain.usecase.GetCategoriesUseCase
import com.tayyipgunay.firststajproject.presentation.common.ConfirmId
import com.tayyipgunay.firststajproject.presentation.common.events.MessageType
import com.tayyipgunay.firststajproject.presentation.products.list.ProductListEvent
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
class AddProductViewModel @Inject constructor(
    private val addProductUseCase: AddProductUseCase,private val GetcategoriesUseCase: GetCategoriesUseCase
) : ViewModel() , MVIComponent<AddProductIntent, AddProductState, AddProductEvent> {

    private val _state = MutableStateFlow(AddProductState())
  override  val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<AddProductEvent>()
    override val event: SharedFlow<AddProductEvent> = _event.asSharedFlow()




    // ---------- INTENT REDUCER ----------
   override fun onIntent(intent: AddProductIntent) {
        println("🎯 AddProductViewModel.onIntent() çağrıldı: $intent")
        when (intent) {
            is AddProductIntent.Name -> {
                println("📝 Name değiştiriliyor: '${intent.name}'")
                _state.update { it.copy(name = intent.name) }
            }

            is AddProductIntent.Details -> {
                println("📝 Details değiştiriliyor: '${intent.details}'")
                _state.update { it.copy(details = intent.details) }
            }

            is AddProductIntent.IsActive -> {
                println("📝 IsActive değiştiriliyor: ${intent.isActive}")
                _state.update { it.copy(isActive = intent.isActive) }
            }

            is AddProductIntent.Price -> {
                println("📝 Price değiştiriliyor: '${intent.price}'")
                _state.update { it.copy(priceInput = intent.price) }
            }

            is AddProductIntent.Image -> {
                println("🖼️ Image seçildi: $intent.uri")
                _state.update { it.copy(imageUri = intent.uri) }
            }

            is AddProductIntent.ArFile -> {
                println("📱 AR dosyası seçildi: $intent.uri")
                _state.update { it.copy(arUri = intent.uri) }
            }

            AddProductIntent.Submit -> {
                println("🚀 Submit intent alındı!")
                submit()
            }

            AddProductIntent.Reset -> {
                println("🔄 Form sıfırlanıyor")
                _state.update { it.copy(saved = false) }
            }

            is AddProductIntent.Category -> {
                println("📝 Category değiştiriliyor: '${intent.categoryId}'")
                _state.update { 
                    val newState = it.copy(selectedCategoryId = intent.categoryId)
                    println("📝 Yeni state - selectedCategoryId: ${newState.selectedCategoryId}")
                    newState
                }
            }

            is AddProductIntent.LoadCategories -> {
                println("🔄 Categories yükleniyor")
                println("🔄 LoadCategories çağrıldı - mevcut selectedCategoryId: ${_state.value.selectedCategoryId}")
                loadCategories()
            }
            
            is AddProductIntent.ModelType -> {
                println("📝 ModelType değiştiriliyor: '${intent.modelType}'")
                _state.update { it.copy(selectedModelTypeId = intent.modelType.toIntOrNull()) }
            }

            AddProductIntent.Back -> {
                println("⬅️ Geri gitme")
                // TODO: Navigate back implementation
            }
            
            AddProductIntent.Save -> {
                println("💾 Kaydetme onayı istendi")
                viewModelScope.launch {
                    _event.emit(AddProductEvent.ShowConfirmDialog(
                        id = ConfirmId.SaveProduct,
                        title = "Ürün Kaydet",
                        message = "Bu ürünü kaydetmek istediğinizden emin misiniz?",
                        confirmText = "Evet, Kaydet",
                        cancelText = "İptal"
                    ))
                }
            }
            
            AddProductIntent.Init -> {
                println("🚀 Initialize - Kategoriler yükleniyor...")
                loadCategories()
            }
            
            is AddProductIntent.Confirm -> {
                println("✅ Confirm: ${intent.id} = ${intent.confirmed}")
                when (intent.id) {
                    ConfirmId.SaveProduct -> {
                        if (intent.confirmed) {
                            println("✅ Kaydetme onaylandı, submit çağrılıyor")
                            submit()
                        } else {
                            println("❌ Kaydetme iptal edildi")
                        }
                    }
                    ConfirmId.DeleteProduct -> {
                        // TODO: Delete product handling
                    }
                    ConfirmId.Logout -> {
                        // TODO: Logout handling
                    }
                }
            }
            
            is AddProductIntent.ClearError -> {
                println("🧹 Clear error for field: ${intent.field}")
                // TODO: Clear field error
            }
        }
    }
        fun loadCategories() {
            println("🔄 Categories yükleniyor")
            viewModelScope.launch {
                println("🎯 UseCase çağrılıyor...")
                GetcategoriesUseCase.Execute(
                    page = 0,
                    size = 100,
                    sort = listOf("id,asc")


                ).collect { res ->
                    println("📨 UseCase'den yanıt alındı: $res")
                    when (res) {
                        is Resource.Loading -> {
                            println("⏳ Loading state")
                            _state.update { it.copy(categoriesLoading = true) }
                        }
                        is Resource.Success -> {
                            println("✅ Success state - Categories: ${res.data}")
                            println("✅ Kategoriler yüklendi - count: ${res.data?.size ?: 0}")
                            _state.update { it.copy(categoriesLoading = false, categories = res.data ?: emptyList()) }
                        }
                        is Resource.Error -> {
                            println("❌ Error state - Message: ${res.message}")
                            _state.update { it.copy(categoriesLoading = false, categoriesError = res.message) }
                        }


                    }


                }

            }
        }



    // ---------- SIDE EFFECT: SUBMIT ----------
     private fun submit() {
        println("🚀 AddProductViewModel.submit() BAŞLADI")
        val s = state.value
        println("📊 Mevcut state:")
        println("   - name: '${s.name}'")
        println("   - details: '${s.details}'")
        println("   - isActive: ${s.isActive}")
        println("   - priceInput: '${s.priceInput}'")
        println("   - selectedCategoryId: '${s.selectedCategoryId}'")
        println("   - selectedModelTypeId: '${s.selectedModelTypeId}'")
        println("   - imageUri: ${s.imageUri}")
        println("   - arUri: ${s.arUri}")

        // basit validasyon
        println("🔍 Validasyon yapılıyor...")
        val priceDouble = s.priceInput.toDoubleOrNull()
        val modelTypeInt = s.selectedModelTypeId ?: 0
        println("🔍 Price double: $priceDouble")
        println("🔍 ModelType int: $modelTypeInt")
        
        when {
            s.name.isBlank() -> {
                println("❌ Validasyon hatası: Name boş")
                viewModelScope.launch {
                    _event.emit(AddProductEvent.ShowValidationError(FieldId.Name, "Name is required"))
                }
                return _state.update { it.copy(error = "Name is required") }
            }
            priceDouble == null -> {
                println("❌ Validasyon hatası: Price geçersiz")
                viewModelScope.launch {
                    _event.emit(
                        AddProductEvent.ShowValidationError(
                            FieldId.Price,
                            "Price must be a number"
                        )
                    )
                }
                return _state.update { it.copy(error = "Price must be a number") }
            }
            s.selectedCategoryId.isNullOrBlank() -> {
                println("❌ Validasyon hatası: Category boş - selectedCategoryId: ${s.selectedCategoryId}")
                println("❌ State detayı: name='${s.name}', priceInput='${s.priceInput}', selectedCategoryId=${s.selectedCategoryId}")
                viewModelScope.launch {
                    _event.emit(
                        AddProductEvent.ShowValidationError(
                            FieldId.Category,
                            "Category is required"
                        )
                    )
                }
                return _state.update { it.copy(error = "Category is required") }
            }
        }
        println("✅ Validasyon başarılı!")

        viewModelScope.launch {
            println("🔄 Coroutine başlatıldı")
            _state.update {
                it.copy(isSaving = true, error = null)
            }
            println("📊 State güncellendi: isSaving = true")

            if (priceDouble != null && !s.selectedCategoryId.isNullOrBlank()) {
                println("🎯 UseCase çağrılıyor...")
                addProductUseCase.Execute(
                    name = s.name,
                    details = s.details.ifBlank { null },
                    isActive = s.isActive,
                    imagePath = s.imageUri?.toString(),   // Uri'yi string olarak gönder
                    arFilePath = s.arUri?.toString(),
                    price = priceDouble,
                    categoryId = s.selectedCategoryId,
                    modelType = modelTypeInt,

                ).collect { res ->
                    println("📨 UseCase'den yanıt alındı: $res")
                    when (res) {
                        is Resource.Loading -> {
                            println("⏳ Loading state")
                            _state.update { it.copy(isSaving = true) }
                        }

                        is Resource.Success -> {
                            println("✅ Success state - Product: ${res.data}")
                            _event.emit(AddProductEvent.ShowMessage("Başarıyla kaydedildi!", MessageType.Success))
                            _event.emit(AddProductEvent.NavigateBack)
                            _state.update { it.copy(isSaving = false, saved = true) }
                        }

                        is Resource.Error -> {
                            println("❌ Error state - Message: ${res.message}")
                            _event.emit(AddProductEvent.ShowMessage("Error: ${res.message}", MessageType.Error))
                            _state.update { it.copy(isSaving = false, error = res.message) }
                        }
                    }
                }
            } else {
                println("❌ priceDouble null veya selectedCategoryId boş, UseCase çağrılmıyor")
                println("❌ priceDouble: $priceDouble, selectedCategoryId: ${s.selectedCategoryId}")
            }
        }
    }
}
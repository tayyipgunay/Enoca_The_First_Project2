package com.tayyipgunay.firststajproject.presentation.add

import android.net.Uri
import com.tayyipgunay.firststajproject.domain.model.Category
import com.tayyipgunay.firststajproject.domain.model.ModelTypeUi
import com.tayyipgunay.firststajproject.presentation.common.ConfirmId
import com.tayyipgunay.firststajproject.presentation.common.events.MessageChannel
import com.tayyipgunay.firststajproject.presentation.common.events.MessageType

object AddProductContract {
    // --- Alan kimlikleri (validasyon için type-safe) ---
    enum class FieldId { Name, Price, Image, Category }

    // --- Kullanıcı niyetleri (INPUT) ---
    sealed interface Intent {
        // Yaşam döngüsü / yüklemeler
        data object Init : Intent
        data object LoadCategories : Intent

        // Form değişimleri
        data class Name(val name: String) : Intent
        data class Details(val details: String) : Intent
        data class IsActive(val isActive: Boolean) : Intent
        data class Price(val price: String) : Intent

        // Seçimler
        data class Category(val categoryId: String) : Intent
        data class ModelType(val modelType: String) : Intent  // UI'dan string gelir; VM int'e çevirir

        // Medya
        data class Image(val uri: Uri?) : Intent
        data class ArFile(val uri: Uri?) : Intent

        // İşlemler / akış
        data object Submit : Intent
        data object Reset : Intent
        data object Back : Intent
        data object Save : Intent  // onay diyaloğunu tetiklemek için
        data class Confirm(val id: ConfirmId, val confirmed: Boolean) : Intent

        // Validasyon temizleme
        data class ClearError(val field: FieldId) : Intent
    }

    // --- Ekranın durumu (STATE) ---
    data class State(
        val name: String = "",
        val details: String = "",
        val isActive: Boolean = true,

        // Fiyat input değeri
        val priceInput: String = "",

        // Medya
        val imageUri: Uri? = null,
        val arUri: Uri? = null,

        // Kayıt akışı
        val isSaving: Boolean = false,
        val savedProductId: String? = null, // backend döndürürse set edebilirsin
        val saved: Boolean = false,         // kayıt tamamlandı sinyali

        // Validasyon
        val fieldErrors: Map<FieldId, String> = emptyMap(),
        val error: String? = null,          // genel hata (örn. ağ)

        // Kategoriler
        val categories: List<Category> = emptyList(),
        val categoriesLoading: Boolean = false,
        val categoriesError: String? = null,
        val selectedCategoryId: String? = null,

        // Model Tipleri (UI)
        val modelTypes: List<ModelTypeUi> = emptyList(),
        val modelTypesLoading: Boolean = false,
        val modelTypesError: String? = null,
        val selectedModelTypeId: Int? = null
    )

    // --- Tek seferlik etkiler (OUTPUT) ---
    sealed interface Effect {
        // Mesajlar
        data class ShowMessage(
            val text: String,
            val type: MessageType = MessageType.Info,
            val channel: MessageChannel = MessageChannel.Snackbar
            ) : Effect


        data class ShowFileError(val message: String) : Effect

        // Onay diyaloğu
        data class ShowConfirmDialog(
            val id: ConfirmId,
            val title: String,
            val message: String,
            val confirmText: String,
            val cancelText: String
        ) : Effect

        // Navigasyon
        data object NavigateBack : Effect
        data object NavigateToProductList : Effect
    }
}

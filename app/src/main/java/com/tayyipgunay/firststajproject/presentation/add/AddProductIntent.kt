package com.tayyipgunay.firststajproject.presentation.add

import android.net.Uri
import com.tayyipgunay.firststajproject.presentation.common.ConfirmId

/*sealed interface AddProductIntent {
    data object Init : AddProductIntent

    // Form değişimleri
    data class Name(val name: String) : AddProductIntent
    data class Details(val details: String) : AddProductIntent
    data class IsActive(val isActive: Boolean) : AddProductIntent
    data class Price(val price: String) : AddProductIntent

    // Seçimler
    data class Category(val categoryId: String) : AddProductIntent
    data class ModelType(val modelType: String) : AddProductIntent

    // Medya
    data class Image(val uri: Uri?) : AddProductIntent
    data class ArFile(val uri: Uri?) : AddProductIntent

    // İşlemler
    data object Save : AddProductIntent
    data class Confirm(val id: ConfirmId, val confirmed: Boolean) : AddProductIntent

    // Hata temizleme
    data class ClearError(val field: AddProductContract.FieldId) : AddProductIntent
    
    // Veri yükleme
    data object LoadCategories : AddProductIntent

    // Form işlemleri
    data object Submit : AddProductIntent
    data object Reset : AddProductIntent

    // Navigasyon
    data object Back : AddProductIntent
}*/
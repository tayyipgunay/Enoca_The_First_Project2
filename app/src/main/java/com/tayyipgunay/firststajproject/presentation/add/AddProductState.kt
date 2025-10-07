package com.tayyipgunay.firststajproject.presentation.add

import android.net.Uri
import com.tayyipgunay.firststajproject.domain.model.Category
import com.tayyipgunay.firststajproject.domain.model.ModelTypeUi

data class AddProductState(
    /* val name: String = "",
     val details: String = "",
     val isActive: Boolean = true,
     val price: String = "",
     val imageUri: Uri? = null,
     val arUri: Uri? = null,
     val isSaving: Boolean = false,
     val error: String? = null,
     val saved: Boolean = false,

     // Kategori UI/Veri
     val categories: List<Category> = emptyList(),
     val categoriesLoading: Boolean = false,
     val categoriesError: String? = null,
     val selectedCategoryId: String = "",

     //Model TYPE UI/VERİ
     val modelTypes: List<Category> = emptyList(),
     val modelTypesLoading: Boolean = false,
     val modelTypesError: String? = null,
     val modelType: String="",

     val selectedModelTypeId: String = ""

   */


   val name: String = "",
    val details: String = "",
    val isActive: Boolean = true,

    // Fiyat: input + parse edilmiş değer
    val priceInput: String = "",
   // val priceValue: Double? = null,

    // Medya
    val imageUri: Uri? = null,
    val arUri: Uri? = null,

    // Yükleme / sonuç
    val isSaving: Boolean = false,
    val savedProductId: String? = null,

    val saved: Boolean = false,

    // Validasyon
    val validationErrors: Map<FieldId, String> = emptyMap(),

    // Kategori
    val categories: List<Category> = emptyList(),
    val categoriesLoading: Boolean = false,
    val categoriesError: String? = null,
    val selectedCategoryId: String? = null,

    // Model Type
    val modelTypes: List<ModelTypeUi> = emptyList(),
    val modelTypesLoading: Boolean = false,
    val modelTypesError: String? = null,
    val selectedModelTypeId: Int? = null,
    val error: String? = null,

    )


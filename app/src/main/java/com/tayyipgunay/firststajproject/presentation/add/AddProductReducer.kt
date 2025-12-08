package com.tayyipgunay.firststajproject.presentation.add

import android.net.Uri
import com.tayyipgunay.firststajproject.domain.model.Category
import javax.inject.Inject

class AddProductReducer @Inject constructor() {

    fun reduce(
        s: AddProductContract.State,
        r: Result
    ): AddProductContract.State = when (r) {

        // --- Kategoriler ---
        Result.CategoriesLoading ->
            s.copy(categoriesLoading = true, categoriesError = null)

        is Result.CategoriesSuccess ->
            s.copy(categoriesLoading = false, categories = r.items, categoriesError = null)

        is Result.CategoriesFailure ->
            s.copy(categoriesLoading = false, categoriesError = r.msg)

        // --- Submit ---
        Result.SubmitLoading ->
            s.copy(isSaving = true, error = null)

        Result.SubmitSuccess ->
            s.copy(isSaving = false, saved = true)

        is Result.SubmitFailure ->
            s.copy(isSaving = false, error = r.msg)

        // --- Alan güncellemeleri ---
        is Result.FieldUpdated -> s.copy(
            name = r.name ?: s.name,
            details = r.details ?: s.details,
            isActive = r.isActive ?: s.isActive,
            priceInput = r.price ?: s.priceInput,
            selectedCategoryId = r.categoryId ?: s.selectedCategoryId,
            selectedModelTypeId = r.modelTypeId ?: s.selectedModelTypeId,
            imageUri = r.imageUri ?: s.imageUri,
            arUri = r.arUri ?: s.arUri
        )

        // --- Alan bazlı hata yönetimi ---
        is Result.FieldErrorSet ->
            s.copy(fieldErrors = s.fieldErrors + (r.field to r.message))

        is Result.FieldErrorCleared ->
            s.copy(fieldErrors = s.fieldErrors - r.field)

        // --- Saved bayrağını temizle (örn. form reset sonrası) ---
        Result.ResetSaved ->
            s.copy(saved = false)
    }

    // ViewModel'in Reducer'a ilettiği "ara sonuç" tipleri
    sealed interface Result {
        // Kategori yükleme
        data object CategoriesLoading : Result
        data class CategoriesSuccess(val items: List<Category>) : Result
        data class CategoriesFailure(val msg: String) : Result

        // Submit akışı
        data object SubmitLoading : Result
        data object SubmitSuccess : Result
        data class SubmitFailure(val msg: String) : Result

        // Form alanları
        data class FieldUpdated(
            val name: String? = null,
            val details: String? = null,
            val isActive: Boolean? = null,
            val price: String? = null,
            val categoryId: String? = null,
            val modelTypeId: Int? = null,
            val imageUri: Uri? = null,
            val arUri: Uri? = null
        ) : Result

        // Alan bazlı hatalar (ör. “Name boş”)
        data class FieldErrorSet(val field: AddProductContract.FieldId, val message: String) : Result
        data class FieldErrorCleared(val field: AddProductContract.FieldId) : Result

        // Kaydetme sonrası saved=true’yu sıfırlamak için
        data object ResetSaved : Result
    }
}

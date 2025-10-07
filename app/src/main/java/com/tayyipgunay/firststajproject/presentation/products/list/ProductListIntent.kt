package com.tayyipgunay.firststajproject.presentation.products.list




sealed interface ProductListIntent {
    // Data Loading
    data object Load : ProductListIntent
    data object Retry : ProductListIntent
    data object Refresh : ProductListIntent

    // Pagination
    data class ChangePage(val page: Int) : ProductListIntent
    data class ChangeSize(val size: Int) : ProductListIntent

    // Sorting
    data class ChangeSort(val sort: ProductSort) : ProductListIntent
    data class ChangeSortRaw(val sort: List<String>) : ProductListIntent

    // Navigation
    //data object AddClicked : ProductListIntent

    // Item Actions

}
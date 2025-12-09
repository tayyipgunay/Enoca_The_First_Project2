package com.tayyipgunay.firststajproject.presentation.feature.products.list


import javax.inject.Inject

class ProductListReducer @Inject constructor() {

    fun reduce(
        state: ProductListContract.State,
        result: Result
    ): ProductListContract.State =
        when (result) {
            is Result.SortChanged -> state.copy(
                selectedSort = result.sort,
                error = null,

            )

            is Result.ErrorOccurred -> state.copy(
                error = result.message
            )

            Result.ErrorCleared -> state.copy(
                error = null
            )
        }

    sealed interface Result {
        data class SortChanged(val sort: ProductSort) : Result
        data class ErrorOccurred(val message: String?) : Result
        data object ErrorCleared : Result
    }
}


/*class ProductListReducer2 @Inject constructor() {

    fun reduce(
        state: ProductListContract2.State,
        result: Result
    ): ProductListContract2.State =
        when (result) {
            is Result.SortChanged -> state.copy(
                selectedSort = result.sort,
                error = null,
                isEndReached = false
            )
            is Result.ErrorOccurred ->{
                println("ProductListReducer2 error : "+result.message)
                state.copy(error = result.message)
            }
            is Result.EndReached -> state.copy(isEndReached = true)
            Result.ErrorCleared -> state.copy(error = null)
        }

    sealed interface Result {
        data class SortChanged(val sort: ProductSort) : Result
        data class ErrorOccurred(val message: String) : Result
        data object EndReached : Result
        data object ErrorCleared : Result
    }
}*/


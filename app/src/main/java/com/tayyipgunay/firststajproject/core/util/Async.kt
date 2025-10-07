package com.tayyipgunay.firststajproject.core.util

sealed interface Async<out T> {
    data object Idle : Async<Nothing>
    data object Loading : Async<Nothing>
    data class Success<T>(val data: T) : Async<T>
    data class Fail(val message: String) : Async<Nothing>
}

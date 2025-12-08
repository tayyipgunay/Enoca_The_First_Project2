package com.tayyipgunay.firststajproject.core.error


sealed interface AppError {
    data class Http(val status: Int,val message: String? = null, val raw: String? = null) : AppError
    data class Network(val cause: Throwable? = null) : AppError
    data class Serialization(val cause: Throwable? = null) : AppError
    data class Local(val reason: String, val cause: Throwable? = null) : AppError
    data class Unknown(val cause: Throwable? = null) : AppError
}
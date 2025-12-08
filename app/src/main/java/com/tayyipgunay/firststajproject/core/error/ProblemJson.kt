package com.tayyipgunay.firststajproject.core.error

data class ProblemJson(
    val type: String? = null,
    val title: String? = null,
    val status: Int? = null,
    val detail: String? = null,
    val instance: String? = null,
    val message: String? = null,
    val path: String? = null,
    val fieldErrors: List<FieldError>? = null
)
data class FieldError(
    val field: String? = null,
    val message: String? = null,
    val objectName: String? = null
)
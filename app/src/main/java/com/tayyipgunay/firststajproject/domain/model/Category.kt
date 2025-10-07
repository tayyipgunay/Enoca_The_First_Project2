package com.tayyipgunay.firststajproject.domain.model


data class Category(
    val id: String,
    val name: String,
    val description: String?,
    val isActive: Boolean
)

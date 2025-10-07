package com.tayyipgunay.firststajproject.domain.model


data class Product(
    val id: String,
    val name: String,
    val details: String?,
    val isActive: Boolean,
    val image: String?,
    val arFilePath: String?,
    val price: Double,
    val modelType: Int,
    val category: Category?
)

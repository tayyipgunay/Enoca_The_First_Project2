package com.tayyipgunay.firststajproject.domain.model

data class ProductSummary(
    val id: String,
    val name: String,
    val price: Double,
    val isActive: Boolean,
    val image: String?
)

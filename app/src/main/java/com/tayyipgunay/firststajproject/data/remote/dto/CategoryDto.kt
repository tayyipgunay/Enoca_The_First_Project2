package com.tayyipgunay.firststajproject.data.remote.dto

import com.tayyipgunay.firststajproject.domain.model.Category


data class CategoryDto(
    val id: String,
    val name: String,
    val description: String?,
    val isActive: Boolean?
)
fun CategoryDto.toDomain(): Category =
    Category(
        id = id,
        name = name,
        description = description,
        isActive = isActive ?: true  // null ise true olarak varsayılan değer
    )
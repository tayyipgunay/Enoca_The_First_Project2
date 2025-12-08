package com.tayyipgunay.firststajproject.data.remote.dto

import com.squareup.moshi.JsonClass
import com.tayyipgunay.firststajproject.domain.model.Product
import com.tayyipgunay.firststajproject.domain.model.ProductSummary

@JsonClass(generateAdapter = true)
data class ProductDto(
    val id: String,
    val name: String?,
    val details: String?,
    val isActive: Boolean?,
    val image: String?,
    val arFilePath: String?,
    val price: Double,
    val modelType: Int?,
    val category: CategoryDto?
)
fun ProductDto.toDomain(): Product =
    Product(
        id = id,
        name = name?:"a",
        details = details,
        isActive = isActive?:false,
        image = image,
        arFilePath = arFilePath,
        price = price,
        modelType = modelType?:1,
        category = category?.toDomain()//?
    )

fun ProductDto.toSummaryDomain(): ProductSummary {
    return  ProductSummary(
        id = id,
        name = name?:"a",
        price = price,
        isActive = isActive?:false,
        image =image
    )
}

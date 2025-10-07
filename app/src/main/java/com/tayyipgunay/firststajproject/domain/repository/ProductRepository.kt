package com.tayyipgunay.firststajproject.domain.repository


import com.tayyipgunay.firststajproject.core.util.Resource
import com.tayyipgunay.firststajproject.data.remote.dto.ProductDto
import com.tayyipgunay.firststajproject.domain.model.Category
import com.tayyipgunay.firststajproject.domain.model.Product
import com.tayyipgunay.firststajproject.domain.model.ProductSummary
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    suspend fun addProduct(
        name: String,
        details: String?,
        isActive: Boolean,
        imagePath: String?,      // cihazdan seçilen dosya yolu
        arFilePath: String?,     // cihazdan seçilen dosya yolu
        price: Double,
        categoryId: String,
        modelType: Int
    ):Flow<Resource<Product>>


    suspend fun getProducts(
        page: Int,
        size: Int,
        sort: List<String>
    ): Flow<Resource<List<ProductSummary>>>


    suspend fun getCategories(
        page: Int,
        size: Int,
        sort: List<String>
    ): Flow<Resource<List<Category>>>

}


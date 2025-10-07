package com.tayyipgunay.firststajproject.domain.usecase

import com.tayyipgunay.firststajproject.core.util.Resource
import com.tayyipgunay.firststajproject.domain.model.Category
import com.tayyipgunay.firststajproject.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(private val productRepository: ProductRepository) {
    suspend fun Execute(
        page: Int,
        size: Int,
        sort: List<String>
    ): Flow<Resource<List<Category>>> {
        return productRepository.getCategories(page, size, sort)
    }
}
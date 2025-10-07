package com.tayyipgunay.firststajproject.domain.usecase

import com.tayyipgunay.firststajproject.core.util.Resource
import com.tayyipgunay.firststajproject.domain.model.Product
import com.tayyipgunay.firststajproject.domain.model.ProductSummary
import com.tayyipgunay.firststajproject.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetProductUseCase @Inject constructor(private val repository: ProductRepository) {

        suspend fun Execute(
            page: Int,
            size: Int,
            sort: List<String>
        ): Flow<Resource<List<ProductSummary>>> {
            return repository.getProducts(
                page = page,
                size = size,
                sort = sort
            )
        }
}

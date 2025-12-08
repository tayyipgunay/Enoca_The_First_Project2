package com.tayyipgunay.firststajproject.domain.usecase

import androidx.paging.PagingData
import com.tayyipgunay.firststajproject.domain.model.ProductSummary
import com.tayyipgunay.firststajproject.domain.repository.ProductRepository
import com.tayyipgunay.firststajproject.domain.repository.ProductRepository2
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductUseCase2 @Inject constructor(
    private val repository: ProductRepository2
)
    {

        fun execute(
            sort: List<String> = emptyList()):
                Flow<PagingData<ProductSummary>> {
            return repository.getProductsPaging(sort)
        }
    }

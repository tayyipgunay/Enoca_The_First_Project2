package com.tayyipgunay.firststajproject.domain.repository

import androidx.paging.PagingData
import com.tayyipgunay.firststajproject.domain.model.ProductSummary
import kotlinx.coroutines.flow.Flow


interface ProductRepository2 {
        fun getProductsPaging(
            sort: List<String>
        ): Flow<PagingData<ProductSummary>>
    }



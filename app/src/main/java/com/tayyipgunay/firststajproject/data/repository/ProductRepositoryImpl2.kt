package com.tayyipgunay.firststajproject.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult
import com.tayyipgunay.firststajproject.core.error.AppError
import com.tayyipgunay.firststajproject.core.error.AppException
import com.tayyipgunay.firststajproject.core.error.toAppError
import com.tayyipgunay.firststajproject.data.network.HttpErrorMapper
import com.tayyipgunay.firststajproject.data.paging.PagingConstants
import com.tayyipgunay.firststajproject.data.paging.ProductPagingSource
import com.tayyipgunay.firststajproject.data.remote.ProductApi
import com.tayyipgunay.firststajproject.data.remote.dto.toDomain
import com.tayyipgunay.firststajproject.data.remote.dto.toSummaryDomain
import com.tayyipgunay.firststajproject.domain.model.ProductSummary
import com.tayyipgunay.firststajproject.domain.repository.ProductRepository
import com.tayyipgunay.firststajproject.domain.repository.ProductRepository2
import com.tayyipgunay.firststajproject.presentation.ui.state.LoadingStateCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import retrofit2.HttpException
import java.lang.Error
import javax.inject.Inject


class ProductRepositoryImpl2 @Inject constructor(
    private val api: ProductApi,
    private val errorMapper: HttpErrorMapper
) : ProductRepository2 {

    override fun getProductsPaging(sort: List<String>): Flow<PagingData<ProductSummary>> {

        println("🔶 REPO: getProductsPaging(sort=$sort) çağrıldı")

        return Pager(
            config = PagingConfig(
                pageSize = PagingConstants.PAGE_SIZE,
                prefetchDistance = PagingConstants.PREFETCH_DISTANCE,
                initialLoadSize = PagingConstants.INITIAL_LOAD_SIZE,
                enablePlaceholders = false,
                maxSize = PagingConstants.MAX_SIZE,
                jumpThreshold = Int.MIN_VALUE
            ),
            pagingSourceFactory = {

                println("🔶 REPO: Yeni PagingSource oluşturuldu")

                ProductPagingSource { page, size ->

                    println("🟦 REPO: loader() çalıştı → page=$page size=$size")

                    val response = api.getProducts(page, size, sort)

                    println("🟦 REPO: API cevabı geldi → code=${response.code()}")

                    if (!response.isSuccessful) {
                        println("❌ REPO: HTTP ERROR! code=${response.code()}")
                        println("responseBody "+response.body())
                        println("responseErrorBody "+response.errorBody())
                        println("resonseerrorbody.String "+response.errorBody()?.string()?:"  Stroke Error")
                        println("responseerrorbody.Tostring")
                        val appError = errorMapper.map(response)
                        println("❌ REPO: AppError üretildi → ${appError::class.simpleName}")
                        throw AppException(appError)
                    }

                    println("🟩 REPO: Response başarılı → body map ediliyor")

                    response.body()
                        .orEmpty()
                        .map { dto ->
                            println("🟩 REPO: Map edilen ürün → ${dto}")
                            dto.toSummaryDomain()
                        }
                }
            }
        ).flow
    }
}

package com.tayyipgunay.firststajproject.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tayyipgunay.firststajproject.core.error.AppError
import com.tayyipgunay.firststajproject.core.error.AppException
import com.tayyipgunay.firststajproject.core.error.toAppError
import com.tayyipgunay.firststajproject.core.util.Resource
import com.tayyipgunay.firststajproject.data.network.HttpErrorMapper
import com.tayyipgunay.firststajproject.data.remote.ProductApi
import com.tayyipgunay.firststajproject.data.remote.dto.toSummaryDomain
import com.tayyipgunay.firststajproject.domain.model.ProductSummary
import com.tayyipgunay.firststajproject.domain.repository.ProductRepository2
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class ProductPagingSource(
    private val loader: suspend (page: Int, size: Int) -> List<ProductSummary>
) : PagingSource<Int, ProductSummary>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductSummary> {
        val page = params.key ?: PagingConstants.INITIAL_PAGE
        val size = PagingConstants.PAGE_SIZE

        return try {

            val data = loader(page, size)
            val nextKey = if (data.size < size) null else page + 1

            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = nextKey
            )

        } catch (e: Throwable) {   // ✔️ Exception değil Throwable
         println("Product Paging Source catch geldi  "+e)
            // Eğer e zaten AppException ise tekrar sarmalamaya gerek yok
            val appException = when (e)
            {
                is AppException -> e

                else -> AppException(e.toAppError())   // ✔️ Network / JSON vb burada dönüşür
            }

            LoadResult.Error(appException)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProductSummary>): Int? =
        state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
}

/*class ProductPagingSource(
    private val loader: suspend (page: Int, size: Int) -> List<ProductSummary>
) : PagingSource<Int, ProductSummary>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductSummary> {

        val page = params.key ?: PagingConstants.INITIAL_PAGE
        val size = PagingConstants.PAGE_SIZE

        println("⬇️ PAGING: load() başladı → page=$page size=$size")

        return try {

            val data = loader(page, size)

            println("🟩 PAGING: loader() başarıyla döndü → dataSize=${data.size}")

            val nextKey = if (data.size < size) null else page + 1

            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = nextKey
            )

        } catch (e: Exception) {

            println("🛑 PAGING: CATCH çalıştı")
            println("🛑 PAGING: e::class = ${e::class.qualifiedName}")
            println("🛑 PAGING: e.message = ${e.message}")

            if (e is AppException) {
                println("🟧 PAGING: Bu zaten AppException!   "+e)
                println("🟧 PAGING: İç AppError tipi = ${e.appError::class.simpleName}")
            } else {
                println("")
                println("🟥 PAGING: Bu AppException değil → toAppError() uygulanacak  "+e)
            }

            val converted = AppException(e.toAppError())

            println("📦 PAGING: LoadResult.Error ile AppException gönderiliyor → ${converted.appError}")

            LoadResult.Error(converted)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProductSummary>): Int? =
        state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }
}*/

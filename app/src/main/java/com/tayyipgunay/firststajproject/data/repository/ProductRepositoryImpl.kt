package com.tayyipgunay.firststajproject.data.repository

import android.content.Context
import android.net.Uri
import com.tayyipgunay.firststajproject.core.error.toAppError
import com.tayyipgunay.firststajproject.core.util.Resource
import com.tayyipgunay.firststajproject.core.util.toPlainBody
import com.tayyipgunay.firststajproject.core.util.uriToPart
import com.tayyipgunay.firststajproject.data.network.HttpErrorMapper
import com.tayyipgunay.firststajproject.data.remote.ProductApi
import com.tayyipgunay.firststajproject.data.remote.dto.ProductDto
import com.tayyipgunay.firststajproject.data.remote.dto.toDomain
import com.tayyipgunay.firststajproject.data.remote.dto.toSummaryDomain
import com.tayyipgunay.firststajproject.domain.model.Category
import com.tayyipgunay.firststajproject.domain.model.Product
import com.tayyipgunay.firststajproject.domain.model.ProductSummary
import com.tayyipgunay.firststajproject.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject


class ProductRepositoryImpl @Inject constructor(
    private val productApi: ProductApi,
    private val context: Context,
    private val httpErrorMapper: HttpErrorMapper
): ProductRepository {
    override suspend fun addProduct(
        name: String,
        details: String?,
        isActive: Boolean,
        imagePath: String?,
        arFilePath: String?,
        price: Double,
        categoryId: String,
        modelType: Int
    ): Flow<Resource<Product>> = flow {


        try {
            val imagePart = uriToPart("image", imagePath?.let(Uri::parse), context)
            val arPart = uriToPart("arFilePath", arFilePath?.let(Uri::parse), context)

            val resp = productApi.addProduct(
                name = name.toPlainBody(),
                details = details?.toPlainBody(),
                isActive = isActive.toPlainBody(),
                image = imagePart,          // null ise Retrofit bu part’ı göndermez
                arFilePath = arPart,             // null ise göndermez
                price = price.toPlainBody(),
                categoryId = categoryId.toPlainBody(),
                modelType = modelType.toPlainBody()
            )

            if (resp.isSuccessful) {
                println("✅ API çağrısı başarılı!")

                println("✅ Response code: ${resp.code()}")

                resp.body()?.let { dto ->
                    emit(Resource.Success(dto.toDomain()))
                } //?: emit(Resource.Error("Empty response body"))
            } else {

                emit(Resource.Error(httpErrorMapper.map(resp)))



                println("❌ API çağrısı başarısız!")
                println("❌ Response code: ${resp.code()}")
                println("❌ Response message: ${resp.message()}")
                println("❌ Response error body: ${resp.errorBody()?.string()}")

                // emit(Resource.Error("Sunucu hatası (${resp.code()})"))


            }

        }
        catch (e: Exception) {
            // emit(Resource.Error(e.message ?: "Ürün eklenemedi"))

            emit(Resource.Error(e.toAppError()))

        }!!
    }.flowOn(Dispatchers.IO)

override suspend fun getCategories(
 page: Int,
 size: Int,
 sort: List<String>
): Flow<Resource<List<Category>>> = flow {
 try {
     val response = productApi.getCategories(page, size, sort)
     if (response.isSuccessful) {
         val body = response.body().orEmpty()
         val Category = body.map { categoryDto ->
             categoryDto.toDomain()
         }
         emit(Resource.Success(Category))

     }
     else {
         val err = response.errorBody()?.string()
         println("❌ API çağrısı başarısız!    " + err)
        //  emit(Resource.Error("Sunucu hatası (${response.code()})"))
         emit(Resource.Error(httpErrorMapper.map(response)))

     }


 }
 catch (e: Exception) {

     emit(Resource.Error(e.toAppError()))

}
}.onStart {
 emit(Resource.Loading())
}
 .flowOn(Dispatchers.IO)
}












/*
override suspend fun getProducts(
        page: Int,
        size: Int,
        sort: List<String>
    ): Flow<Resource<List<ProductSummary>>> =
        flow {
            val response = productApi.getProducts(page, size, sort)

            if (response.isSuccessful) {
                println("✅ API çağrısı başarılı!")
                println("✅ Response code: ${response.code()}")

                val responseBody = response.body()
                responseBody?.forEach{
                    println("name: ${it.name}")
                }?: println("Empty response body")

                // DTO -> Summary domain map
                val summariess = responseBody?.map { it.toSummaryDomain() }
                 emit(Resource.Success(summariess ?: emptyList()))

            }
            else {
                println("response code  " + response.code())
                emit(Resource.Error(httpErrorMapper.map(response)))
            }
        }
            .catch { throwable ->
                emit(Resource.Error(throwable.toAppError()))

            }

            .onStart {
                emit(Resource.Loading())
            }.flowOn(Dispatchers.IO)

 */

                          /*suspend fun getProducts(
                             page: Int,
                            size: Int,
                             sort: List<String>

                           ): Flow<Resource<List<ProductSummary>>>
*/


package com.tayyipgunay.firststajproject.domain.usecase

import com.tayyipgunay.firststajproject.core.util.Resource
import com.tayyipgunay.firststajproject.data.remote.dto.toDomain
import com.tayyipgunay.firststajproject.domain.model.Product
import com.tayyipgunay.firststajproject.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AddProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
/**
 * AddProductUseCase:
 * - Repository'yi çağırır.
 * - Repository zaten Resource<Product> (domain) döndürür.
 * */


        suspend fun Execute(
            name: String,
            details: String?,
            isActive: Boolean,
            imagePath: String?,     // opsiyonel
            arFilePath: String?,    // opsiyonel
            price: Double,
            categoryId: String,
            modelType: Int
        ): Flow<Resource<Product>> {
            // ✅ Artık map yok; repository domain model döndürüyor
            return productRepository.addProduct(
                name = name,
                details = details,
                isActive = isActive,
                imagePath = imagePath,
                arFilePath = arFilePath,
                price = price,
                categoryId = categoryId,
                modelType = modelType
            )
        }
    }














/*suspend fun execute(
        name: String,
        details: String?,
        isActive: Boolean,
        imagePath: String?,     // null olabilir (opsiyonel image)
        arFilePath: String?,    // null olabilir (opsiyonel AR dosyası)
        price: Double,
        categoryId: String,
        modelType: Int
    ): Flow<Resource<Product>> {

        // Repository’den gelen Flow’u map ile dönüştürüyoruz:
        // - Resource.Success<ProductDto> -> Resource.Success<Product>
        // - Resource.Error<ProductDto>   -> Resource.Error<Product>
        // - Resource.Loading<ProductDto> -> Resource.Loading<Product>
        return productRepository.addProduct(
            name = name,
            details = details,
            isActive = isActive,
            imagePath = imagePath,
            arFilePath = arFilePath,
            price = price,
            categoryId = categoryId,
            modelType = modelType
        ).map { resultDto ->
            when (resultDto) {
                is Resource.Success -> {
                    val domain = resultDto.data?.toDomain()
                    if (domain != null) Resource.Success(domain)
                    else Resource.Error("Empty body")
                }
                is Resource.Error -> Resource.Error(resultDto.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }
    }
}
        return productRepository.addProduct(
            name = name,
            details = details,
            isActive = isActive,
            imagePath = imagePath,
            arFilePath = arFilePath,
            price = price,
            categoryId = categoryId,
            modelType = modelType
        ).map {
            when (it) {
                is Resource.Success -> {
                    Resource.Success(it.data!!.toDomain())
                }

                is Resource.Error -> {
                    Resource.Error(it.message!!)
                }

                is Resource.Loading -> {
                    Resource.Loading()
                }
                }
        }
    }
 */

package com.tayyipgunay.firststajproject.data.remote

import com.tayyipgunay.firststajproject.core.util.Constants
import com.tayyipgunay.firststajproject.data.remote.dto.CategoryDto
import com.tayyipgunay.firststajproject.data.remote.dto.ProductDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface ProductApi {

    // Yeni ürün ekleme
    @Multipart
    @POST("/api/products")
    suspend fun addProduct(
        @Part("name") name: RequestBody,
        @Part("details") details: RequestBody?,
        @Part("isActive") isActive: RequestBody,
        @Part image: MultipartBody.Part?,        // MultipartBody.Part için isim belirtme!
        @Part arFilePath: MultipartBody.Part?,   // MultipartBody.Part için isim belirtme!
        @Part("price") price: RequestBody,
        @Part("categoryId") categoryId: RequestBody,
        @Part("modelType") modelType: RequestBody
    ): retrofit2.Response<ProductDto>


    // Ürünleri listeleme
    @GET("/api/products")
    suspend fun getProducts(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: List<String> // örn: ["name,asc", "price,desc"]
    ):retrofit2.Response<List<ProductDto>>

    @GET("/api/categories")
    suspend fun getCategories(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100,
        @Query("sort") sort: List<String> = listOf("name,asc")
    ): retrofit2.Response<List<CategoryDto>>
}


package com.tayyipgunay.firststajproject.data.repository

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.squareup.moshi.Moshi
import com.tayyipgunay.firststajproject.core.util.Resource
import com.tayyipgunay.firststajproject.core.util.toPlainBody
import com.tayyipgunay.firststajproject.core.util.uriToPart
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject


class ProductRepositoryImpl @Inject constructor(
    private val productApi: ProductApi,
    private val context: Context): ProductRepository {
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
                } ?: emit(Resource.Error("Empty response body"))
            } else {
                //val err = runCatching { resp.errorBody()?.string() }.getOrNull()
                println("❌ API çağrısı başarısız!")
                println("❌ Response code: ${resp.code()}")
                println("❌ Response message: ${resp.message()}")
                println("❌ Response error body: ${resp.errorBody()?.string()}")

                emit(Resource.Error("HTTP ${resp.code()} • ${resp.message()}"))
            }

        } catch (io: IOException) {
            emit(Resource.Error("Network error: ${io.message}"))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "AddProduct failed"))
        }
    }.flowOn(Dispatchers.IO)


    override suspend fun getProducts(
        page: Int,
        size: Int,
        sort: List<String>
    ): Flow<Resource<List<ProductSummary>>> = flow {
        emit(Resource.Loading())
        try {
            val response = productApi.getProducts(page, size, sort)

            if (response.isSuccessful) {
                println("✅ API çağrısı başarılı!")
                println("✅ Response code: ${response.code()}")

                val body = response.body().orEmpty()

                // İstersen logla:
                body.forEach {
                    // println("ProductDto: $it")

                }

                // DTO -> Summary domain map
                val summaries = body.map { it.toSummaryDomain() }

                emit(Resource.Success(summaries))
            } else {
                val err = response.errorBody()?.string()
                println("❌ API çağrısı başarısız!    " + err)



                emit(Resource.Error("HTTP ${response.code()} • ${response.message()}${err?.let { " • $it" } ?: ""}"))
            }
        } catch (e: Exception) {
            println("❌ Network error: ${e.message}")
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }
    .onStart { emit(Resource.Loading()) }

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

            }    else {
                val err = response.errorBody()?.string()
                println("❌ API çağrısı başarısız!    " + err)
                 emit(Resource.Error("HTTP ${response.code()} • ${response.message()}${err?.let { " • $it" } ?: ""}"))
            }
            //bu parantez koyunca unrecahbel code okuyor neden


        } catch (e: Exception) {
            println("❌  error: ${e.message}")
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }.onStart {
        emit(Resource.Loading())
    }
        .flowOn(Dispatchers.IO)
}






/*
class ProductRepositoryImpl @Inject constructor(
    private val productApi: ProductApi,
    private val context: Context
): ProductRepository {
    override suspend fun addProduct(
        name: String,
        details: String?,
        isActive: Boolean,
        imagePath: String?,      // cihazdan seçilen dosya yolu
        arFilePath: String?,     // cihazdan seçilen dosya yolu
        price: Double,
        categoryId: String,
        modelType: Int
    ): Flow<Resource<Product>> = flow {

        println("🚀 ProductRepositoryImpl.addProduct() BAŞLADI")
        println("📝 Parametreler:")
        println("   - name: '$name'")
        println("   - details: '$details'")
        println("   - isActive: $isActive")
        println("   - imagePath: '$imagePath'")
        println("   - arFilePath: '$arFilePath'")
        println("   - price: $price")
        println("   - categoryId: '$categoryId'")
        println("   - modelType: $modelType")

            try {
                println("🔧 Yardımcı fonksiyonlar tanımlanıyor...")
                // ---- Yalnızca bu metod içinde geçerli yardımcılar ----
                fun String.toPlainBody(): RequestBody =
                    this.toRequestBody("text/plain".toMediaTypeOrNull())



                // Boolean'ı "true"/"false" stringine çevirip RequestBody yapar
                fun Boolean.toPlainBody(): RequestBody =
                    (if (this) "true" else "false").toPlainBody()

                // Double'ı stringe çevirip RequestBody yapar (ör: 12.99 -> "12.99")
                fun Double.toPlainBody(): RequestBody =
                    this.toString().toPlainBody()

                // Int'i stringe çevirip RequestBody yapar (ör: 5 -> "5")
                fun Int.toPlainBody(): RequestBody =
                    this.toString().toPlainBody()

                println("✅ Yardımcı fonksiyonlar tanımlandı")

                fun getFileName(uri: Uri, contentResolver: ContentResolver): String? {
                    println("📁 getFileName() çağrıldı - URI: $uri")
                    val cursor = contentResolver.query(uri, null, null, null, null)
                    val fileName = cursor?.use {cursor->
                        if (cursor.moveToFirst()) {
                            val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                            if (nameIndex != -1) cursor.getString(nameIndex) else null
                        } else null
                    }
                    println("📁 getFileName() sonucu: '$fileName'")
                    return fileName
                }

                fun uriToPart(field: String, uri: Uri?): MultipartBody.Part? {
                    println("🔄 uriToPart() çağrıldı - field: '$field', uri: $uri")
                    if (uri == null) {
                        println("❌ uriToPart() - URI null, null döndürülüyor")
                        return null
                    }

                    try {
                        println("🔍 ContentResolver ile URI açılıyor...")
                        val contentResolver = context.contentResolver
                        val inputStream: InputStream? = contentResolver.openInputStream(uri)
                        if (inputStream == null) {
                            println("❌ uriToPart() - InputStream null, null döndürülüyor")
                            return null
                        }
                        println("✅ InputStream başarıyla açıldı")

                        // Geçici dosya oluştur
                        println("📄 Geçici dosya oluşturuluyor...")
                        val tempFile = File.createTempFile("upload_", ".tmp", context.cacheDir)
                        val outputStream = FileOutputStream(tempFile)
                        println("📄 Geçici dosya oluşturuldu: ${tempFile.absolutePath}")

                        println("📤 Dosya içeriği kopyalanıyor...")
                        inputStream.use { input ->
                            outputStream.use { output ->
                                input.copyTo(output)
                            }
                        }
                        println("✅ Dosya içeriği başarıyla kopyalandı")

                        val mime = contentResolver.getType(uri) ?: "application/octet-stream"
                        val mediaType = mime.toMediaTypeOrNull()
                        println("📋 MIME type: $mime")

                        val fileName = getFileName(uri, contentResolver) ?: "file"
                        println("📁 Dosya adı: '$fileName'")

                        val part = MultipartBody.Part.createFormData(
                            field,
                            fileName,
                            tempFile.asRequestBody(mediaType)
                        )
                        println("✅ MultipartBody.Part başarıyla oluşturuldu")
                        return part
                    } catch (e: Exception) {
                        println("❌ uriToPart() HATA: ${e.message}")
                        e.printStackTrace()
                        return null
                    }
                }
                // ------------------------------------------------------

                println("🖼️ Image part oluşturuluyor...")
                val imagePart = uriToPart("image", imagePath?.let { Uri.parse(it) })
                println("🖼️ Image part sonucu: ${if (imagePart != null) "BAŞARILI" else "NULL"}")

                println("📱 AR part oluşturuluyor...")
                val arPart = uriToPart("arFilePath", arFilePath?.let { Uri.parse(it) })
                println("📱 AR part sonucu: ${if (arPart != null) "BAŞARILI" else "NULL"}")

                println("🌐 API çağrısı yapılıyor...")
                // Eğer ProductApi.addProduct() dönüşü Response<ProductDto> ise:
                val resp = productApi.addProduct(
                    name = name.toPlainBody(),
                    details = details?.toPlainBody(),   // null ise part gönderilmez
                    isActive = isActive.toPlainBody(),
                   image = imagePart,
                    arFilePath = arPart,
                    price = price.toPlainBody(),
                    categoryId = categoryId.toPlainBody(),
                    modelType = modelType.toPlainBody()
                )
                println("🌐 API çağrısı tamamlandı")

                println("📊 API yanıtı kontrol ediliyor...")
                println("📊 Response code: ${resp.code()}")
                println("📊 Response message: ${resp.message()}")
                println("📊 Response isSuccessful: ${resp.isSuccessful}")

                if (resp.isSuccessful) {
                    println("✅ API çağrısı başarılı!")
                    val body = resp.body()
                    if (body != null) {
                        println("✅ Response body alındı: $body")
                        val domainProduct = body.toDomain()
                        println("✅ Domain'e dönüştürüldü: $domainProduct")

                        emit(Resource.Success(domainProduct))
                    } else {
                        println("❌ Response body null!")
                        emit(Resource.Error("Empty response body"))
                    }
                } else {
                    println("❌ API çağrısı başarısız!")
                    val errBody = try { resp.errorBody()?.string() } catch (_: Throwable) { null }
                    println("❌ Error body: ${errBody ?: "<empty error body>"}")
                    emit(Resource.Error("HTTP ${resp.code()} • ${resp.message()}"))
                }
            } catch (io: IOException) {
                println("❌ IO ERROR: ${io.message}")
                io.printStackTrace()
                emit(Resource.Error("Network error: ${io.message}"))
            } catch (t: Throwable) {
                println("❌ GENEL HATA: ${t.message}")
                t.printStackTrace()
                emit(Resource.Error(t.message ?: "AddProduct failed"))
            }
        }.flowOn(Dispatchers.IO)
*/

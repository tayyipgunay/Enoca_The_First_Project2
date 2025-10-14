package com.tayyipgunay.firststajproject.di

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tayyipgunay.firststajproject.core.util.Constants
import com.tayyipgunay.firststajproject.data.auth.TokenStore
import com.tayyipgunay.firststajproject.data.network.AuthInterceptor
import com.tayyipgunay.firststajproject.data.remote.ProductApi
import com.tayyipgunay.firststajproject.data.repository.ProductRepositoryImpl
import com.tayyipgunay.firststajproject.domain.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideTokenStore(@ApplicationContext context: Context): TokenStore {
        val tokenStore = TokenStore(context)
        // Token'ı otomatik olarak kaydet
        tokenStore.saveAccessToken(Constants.TOKEN)
        println("🔑 Token kaydedildi: ${Constants.TOKEN}")
        return tokenStore
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenStore: TokenStore): AuthInterceptor =
        AuthInterceptor {
            val token = tokenStore.getAccessToken()
            println("🔑 AuthInterceptor token: $token")
            token
        }

    @Provides
    @Singleton
    fun provideOkHttp(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logger)
            .build()


    // Hilt module
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory()) // 🔴 şart
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi =
        retrofit.create(ProductApi::class.java)


    @Provides
    @Singleton
    fun provideProductRepository(
        api: ProductApi,
        @ApplicationContext context: Context
    ): ProductRepository =
        ProductRepositoryImpl(api, context)
}

    /*@Provides
    @Singleton
    fun provideGetProductRepository(
        @ApplicationContext context: Context
    ): GetProductRepositoryIMPL =
*/






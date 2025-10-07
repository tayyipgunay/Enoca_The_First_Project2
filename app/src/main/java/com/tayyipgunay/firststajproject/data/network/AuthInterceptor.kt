package com.tayyipgunay.firststajproject.data.network

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenProvider: () -> String?) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        println("🌐 AuthInterceptor - URL: ${originalRequest.url}")
        
        val token = tokenProvider()
        println("🔑 AuthInterceptor - Token: ${token?.take(20)}...")
        
        val request = originalRequest.newBuilder()
            .header("Accept", "application/json")
            .apply {
                token?.takeIf { it.isNotBlank() }?.let { validToken ->
                    addHeader("Authorization", "Bearer $validToken")
                    println("✅ Authorization header eklendi")
                } ?: println("❌ Token boş veya null, Authorization header eklenmedi")
            }
            .build()
            
        println("🌐 AuthInterceptor - Final headers: ${request.headers}")
        return chain.proceed(request)
    }
}
package com.tayyipgunay.firststajproject.core.util

import com.tayyipgunay.firststajproject.BuildConfig

object Constants {
    // BASE_URL ve TOKEN artık local.properties'ten BuildConfig aracılığıyla geliyor
    // Güvenlik için hassas bilgiler kodda tutulmuyor
  val BASE_URL: String = BuildConfig.BASE_URL
    val TOKEN: String = BuildConfig.API_TOKEN
    
    // Route constants
    const val PRODUCTS = "products"
    const val ADD_PRODUCT = "addProduct"
}

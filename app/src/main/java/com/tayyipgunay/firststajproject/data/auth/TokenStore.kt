package com.tayyipgunay.firststajproject.data.auth

import android.content.Context
import androidx.core.content.edit

class TokenStore(context: Context) {
    private val sp = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun saveAccessToken(token: String) = sp.edit { putString("access_token", token) }
    fun getAccessToken(): String? = sp.getString("access_token", null)
    fun clear() = sp.edit { remove("access_token") }
}
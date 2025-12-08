package com.tayyipgunay.firststajproject.core.error

import android.net.http.HttpException
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException


fun Throwable.toAppError(): AppError {
   return when  (this) {


        is CancellationException -> throw this

        is UnknownHostException,
        is SocketTimeoutException,
        is IOException -> AppError.Network(this)

        is JsonDataException,
        is JsonEncodingException -> AppError.Serialization(this)

        else -> AppError.Unknown(this)
    }
}



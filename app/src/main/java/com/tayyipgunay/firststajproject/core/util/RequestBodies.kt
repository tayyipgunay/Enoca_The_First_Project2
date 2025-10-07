package com.tayyipgunay.firststajproject.core.util

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private val PLAIN = "text/plain".toMediaType()

internal fun String.toPlainBody(): RequestBody = this.toRequestBody(PLAIN)
internal fun Boolean.toPlainBody(): RequestBody = (if (this) "true" else "false").toRequestBody(PLAIN)
internal fun Int.toPlainBody(): RequestBody = this.toString().toRequestBody(PLAIN)
internal fun Double.toPlainBody(): RequestBody = this.toString().toRequestBody(PLAIN)
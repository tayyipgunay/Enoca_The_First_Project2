package com.tayyipgunay.firststajproject.core.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

internal fun uriToPart(
    field: String,
    uri: Uri?,
    context: Context
): MultipartBody.Part? {
    if (uri == null) return null
    val cr: ContentResolver = context.contentResolver
    val input = cr.openInputStream(uri) ?: return null

    val tmp = File.createTempFile("upload_", ".tmp", context.cacheDir)
    FileOutputStream(tmp).use { out -> input.use { it.copyTo(out) } }

    val mime = cr.getType(uri) ?: "application/octet-stream"
    val mediaType = mime.toMediaTypeOrNull()
    val fileName = getFileName(uri, cr) ?: "file"

    return MultipartBody.Part.createFormData(field, fileName, tmp.asRequestBody(mediaType))
}

private fun getFileName(uri: Uri, cr: ContentResolver): String? =
    cr.query(uri, null, null, null, null)?.use { c ->
        val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (idx != -1 && c.moveToFirst()) c.getString(idx) else null
    }
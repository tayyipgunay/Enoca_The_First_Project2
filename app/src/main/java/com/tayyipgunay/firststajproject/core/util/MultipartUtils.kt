package com.tayyipgunay.firststajproject.core.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

internal fun uriToPart(
    field: String,
    uri: Uri?,
    context: Context
): MultipartBody.Part? {
    if (uri == null) return null
    val contentResolver: ContentResolver = context.contentResolver
    val mime: String = contentResolver.getType(uri) ?: "application/octet-stream"
    val mediaType: MediaType? = mime.toMediaTypeOrNull()
    val fileName: String = getFileName(uri, contentResolver) ?: "file"

    // Image ise: downscale + compress; değilse: doğrudan stream ederek kopyala
    val tmp = File.createTempFile("upload_", ".tmp", context.cacheDir)

    if (mime.startsWith("image/")) {
        // 1) Boyutları öğren (bounds)
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, boundsOptions)
        }

        // 2) Hedef uzun kenar: 1280 px (ayarlanabilir)
        val maxSize = 1280
        val inSample = calculateInSampleSize(
            boundsOptions.outWidth,
            boundsOptions.outHeight,
            maxSize,
            maxSize
        )

        // 3) Gerçek decode (inSampleSize ile)
        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = inSample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }

        val bitmap = contentResolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, decodeOptions)
        }

        // 4) JPEG kalite: 85 (ayarlanabilir)
        FileOutputStream(tmp).use { out ->
            // bitmap null ise fallback olarak orijinali kopyala
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            } else {
                contentResolver.openInputStream(uri)?.use { input -> input.copyTo(out) }
            }
        }
    } else {
        // Non-image: doğrudan stream kopyası (RAM'e tümünü almadan)
        contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(tmp).use { out -> input.copyTo(out) }
        } ?: return null
    }

    return MultipartBody.Part.createFormData(field, fileName, tmp.asRequestBody(mediaType))
}

private fun calculateInSampleSize(
    width: Int,
    height: Int,
    reqWidth: Int,
    reqHeight: Int
): Int {
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        var halfHeight = height / 2
        var halfWidth = width / 2
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize.coerceAtLeast(1)
}

private fun getFileName(uri: Uri, cr: ContentResolver): String? =
    cr.query(uri, null, null, null, null)?.use { c ->
        val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (idx != -1 && c.moveToFirst()) c.getString(idx) else null
    }
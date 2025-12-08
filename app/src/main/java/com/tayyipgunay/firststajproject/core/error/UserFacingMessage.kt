package com.tayyipgunay.firststajproject.core.error

fun AppError.toUserMessage(): String = when (this) {

    is AppError.Http -> when (status) {
        400 -> message ?: "Geçersiz istek."
        401 -> "Oturumunuz geçersiz. Lütfen yeniden giriş yapın."
        403 -> "Bu işlem için yetkiniz yok."
        404 -> "Kayıt bulunamadı."
        in 500..599 -> "Sunucu hatası. Daha sonra tekrar deneyin."
        else -> message ?: "Beklenmeyen bir hata oluştu."
    }

    is AppError.Network ->
        "İnternet bağlantısında sorun var."

    is AppError.Serialization ->
        "Veri işlenirken hata oluştu."

    is AppError.Local ->
        "Cihazda işlem yapılamadı: $reason"

    is AppError.Unknown ->
        "Beklenmeyen bir hata oluştu."
}

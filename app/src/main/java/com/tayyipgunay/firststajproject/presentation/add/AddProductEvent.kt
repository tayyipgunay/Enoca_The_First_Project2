package com.tayyipgunay.firststajproject.presentation.add

import com.tayyipgunay.firststajproject.presentation.common.ConfirmId
import com.tayyipgunay.firststajproject.presentation.common.events.MessageType
import com.tayyipgunay.firststajproject.presentation.common.events.MessageChannel

sealed interface AddProductEvent {
}



   /* data class ShowMessage(
        val text: String,
        val type: MessageType = MessageType.Info,
        val channel: MessageChannel = MessageChannel.Snackbar
    ) : AddProductEvent

    // Onay dialog'u
    data class ShowConfirmDialog(
        val id: ConfirmId,
        val title: String,
        val message: String,
        val confirmText: String = "Evet",
        val cancelText: String = "Hayır"
    ) : AddProductEvent*/

    // Sadece "global" validasyon mesajı göstermek istediğinde event kullan
    // (Alan bazlı hataları state'te tut)
   /* data class ShowValidationError(val field: FieldId, val message: String) : AddProductEvent

    // Navigasyon
    data object NavigateBack : AddProductEvent
    data object NavigateToProductList : AddProductEvent

    // Dosya/picker hataları da mesaj üzerinden çözülebilir
    data class ShowFileError(val message: String) : AddProductEvent
}

// Alan kimlikleri (enum/ sealed ile type-safe)
enum class FieldId { Name, Price, Image, Category }*/
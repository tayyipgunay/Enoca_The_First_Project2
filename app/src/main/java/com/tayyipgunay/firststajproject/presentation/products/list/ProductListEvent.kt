package com.tayyipgunay.firststajproject.presentation.products.list

import com.tayyipgunay.firststajproject.presentation.common.events.MessageType
import com.tayyipgunay.firststajproject.presentation.common.events.MessageChannel

sealed interface ProductListEvent {
    // Mesaj gösterimi (Snackbar/Toast/Dialog)
    /*data class ShowMessage(
        val text: String,
        val type: MessageType = MessageType.Info,
        val channel: MessageChannel = MessageChannel.Snackbar
    ) : ProductListEvent*/

    // Navigation Events
   // data object NavigateToAddProduct : ProductListEvent


    // Refresh Events
}
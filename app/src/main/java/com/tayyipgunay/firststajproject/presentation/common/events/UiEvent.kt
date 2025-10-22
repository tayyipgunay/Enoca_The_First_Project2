package com.tayyipgunay.firststajproject.presentation.common.events

import com.tayyipgunay.firststajproject.presentation.common.ConfirmId

sealed interface UiEvent {

    data class ShowMessage(
        val text: String,
        val type: MessageType = MessageType.Info,
        val channel: MessageChannel = MessageChannel.Snackbar
    ) : UiEvent

    data class ShowConfirmDialog(
        val id: ConfirmId,
        val title: String,
        val message: String,
        val confirmText: String = "Evet",
        val cancelText: String = "Hayır"
    ) : UiEvent

}
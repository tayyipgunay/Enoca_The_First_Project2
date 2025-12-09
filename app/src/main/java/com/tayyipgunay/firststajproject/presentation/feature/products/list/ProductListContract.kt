package com.tayyipgunay.firststajproject.presentation.feature.products.list

import com.tayyipgunay.firststajproject.presentation.common.events.MessageChannel
import com.tayyipgunay.firststajproject.presentation.common.events.MessageType

object ProductListContract {

    // ───────────── STATE (Sadece kalıcı UI verisi) ─────────────
    data class State(
        val selectedSort: ProductSort = ProductSort.ACTIVE_FIRST,
        val error: String? = null
    )

    // ───────────── INTENT (Kullanıcı / Sistem aksiyonları) ─────────────
    sealed interface Intent {

        // UI aksiyonları
        data object Refresh : Intent
        data class ChangeSort(val sort: ProductSort) : Intent
        data class ItemClick(val id: String) : Intent
        data object AddClick : Intent

        // Paging - sistemden gelenler
        data object EndReached : Intent
        data class PagingError(val error: Throwable) : Intent
    }

    // ───────────── EFFECT (Tek seferlik olaylar) ─────────────
    sealed interface Effect {

        data object NavigateToAdd : Effect

        data class ShowMessage(
            val text: String,
            val type: MessageType = MessageType.Info,
            val channel: MessageChannel = MessageChannel.Snackbar
        ) : Effect
    }
}
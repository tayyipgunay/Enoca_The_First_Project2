package com.tayyipgunay.firststajproject.core.mvi

import com.tayyipgunay.firststajproject.presentation.common.events.UiEvent
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface MVIComponent<I, S, FE, UE> {
    val state: StateFlow<S>
    val event: SharedFlow<FE>
    val uiEvent: SharedFlow<UE>   // UI Event (ör. UiEvent)
    fun onIntent(intent: I)

}

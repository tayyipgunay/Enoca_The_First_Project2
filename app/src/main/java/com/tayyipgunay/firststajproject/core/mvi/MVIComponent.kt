package com.tayyipgunay.firststajproject.core.mvi


import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface MVIComponent<I, S,E> {
    val state: StateFlow<S>

    fun onIntent(intent: I)


    val effect: SharedFlow<E>


}

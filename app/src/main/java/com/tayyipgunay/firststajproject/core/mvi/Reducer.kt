package com.tayyipgunay.firststajproject.core.mvi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

inline fun <S> MutableStateFlow<S>.reduce(block: (S) -> S) { update(block) }

package com.tayyipgunay.firststajproject.presentation.ui.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PillChip(
    label: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}
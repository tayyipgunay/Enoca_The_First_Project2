package com.tayyipgunay.firststajproject.presentation.ui.components


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*@Composable
fun StatusBadge(isActive: Boolean) {
    AssistChip(
        onClick = {},
        enabled = false,
        label = { Text(if (isActive) "Active" else "Passive") }
    )
}*/
@Composable
fun StatusBadge(isActive: Boolean) {
    val backgroundColor = if (isActive) Color(0xFFE8F5E9) else Color(0xFFF5F5F5)
    val textColor = if (isActive) Color(0xFF2E7D32) else Color(0xFF757575)
    val labelText = if (isActive) "Active" else "Passive"

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(50),
        border = BorderStroke(
            width = 1.dp,
            color = if (isActive) Color(0xFF4CAF50) else Color(0xFFBDBDBD)
        )
    ) {
        Text(
            text = labelText,
            color = textColor,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
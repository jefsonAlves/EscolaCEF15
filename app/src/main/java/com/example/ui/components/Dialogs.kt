package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CustomAlertDialog(
    title: String,
    message: String,
    icon: ImageVector? = null,
    confirmText: String = "Confirmar",
    dismissText: String? = "Cancelar",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = icon?.let { 
            { Icon(it, contentDescription = null, tint = MaterialTheme.colorScheme.primary) } 
        },
        title = {
            Text(text = title, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        },
        text = {
            Text(text = message, textAlign = TextAlign.Center)
        },
        confirmButton = {
            AnimatedButton(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(confirmText)
            }
        },
        dismissButton = dismissText?.let {
            {
                TextButton(onClick = onDismiss) {
                    Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

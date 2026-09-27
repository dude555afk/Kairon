package com.inspiredandroid.kai.ui.chat.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * One compact action menu shared by user and assistant bubbles. The popup may be opened
 * by long-pressing the bubble or by its accessible More button.
 */
@Composable
internal fun MessageActionMenu(
    onBranch: (() -> Unit)?,
    onRetry: (() -> Unit)?,
    onThinking: (() -> Unit)?,
    onWebSearch: (() -> Unit)?,
    onEdit: (() -> Unit)? = null,
    modelId: String? = null,
    externalOpen: Boolean = false,
    onExternalDismiss: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Message actions")
        }
        DropdownMenu(expanded = expanded || externalOpen, onDismissRequest = { expanded = false; onExternalDismiss() }) {
            funActionItem("Branch in new chat", onBranch) { expanded = false; onExternalDismiss() }
            funActionItem("Edit prompt", onEdit) { expanded = false; onExternalDismiss() }
            modelId?.takeIf { it.isNotBlank() }?.let { id ->
                DropdownMenuItem(text = { Text("Used $id") }, onClick = {}, enabled = false)
            }
            funActionItem("Retry", onRetry) { expanded = false; onExternalDismiss() }
            funActionItem("Use Thinking", onThinking) { expanded = false; onExternalDismiss() }
            funActionItem("Search the web", onWebSearch) { expanded = false; onExternalDismiss() }
        }
    }
}

@Composable
private fun funActionItem(label: String, action: (() -> Unit)?, dismiss: () -> Unit) {
    if (action != null) {
        DropdownMenuItem(text = { Text(label) }, onClick = {
            dismiss()
            action()
        })
    }
}

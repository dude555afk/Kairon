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
import androidx.compose.ui.Modifier

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
    modifier: Modifier = Modifier,
    content: @Composable ((() -> Unit) -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        if (content != null) {
            content { expanded = true }
        } else {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Message actions")
            }
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            funActionItem("Branch in new chat", onBranch) { expanded = false }
            funActionItem("Edit prompt", onEdit) { expanded = false }
            modelId?.takeIf { it.isNotBlank() }?.let { id ->
                DropdownMenuItem(text = { Text("Used $id") }, onClick = {}, enabled = false)
            }
            funActionItem("Retry", onRetry) { expanded = false }
            funActionItem("Use Thinking", onThinking) { expanded = false }
            funActionItem("Search the web", onWebSearch) { expanded = false }
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

package com.inspiredandroid.kai.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Kelivo-style message editor. Saving preserves the edited branch without generating;
 * Save & send immediately continues from it.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditMessageDialog(
    initialContent: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onSaveAndSend: (String) -> Unit,
) {
    var editedContent by remember(initialContent) { mutableStateOf(initialContent) }
    val value = editedContent.trim()
    val changed = value.isNotBlank() && value != initialContent.trim()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f),
                        RoundedCornerShape(999.dp),
                    ),
            )
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    enabled = changed,
                    onClick = { onSaveAndSend(value) },
                ) {
                    Text("Save & send")
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Edit message",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.weight(1f))
                TextButton(
                    enabled = changed,
                    onClick = { onSave(value) },
                ) {
                    Text("Save")
                }
            }

            Spacer(Modifier.height(10.dp))
            TextField(
                value = editedContent,
                onValueChange = { editedContent = it },
                modifier = Modifier.fillMaxWidth().heightIn(min = 220.dp, max = 520.dp),
                minLines = 8,
                shape = RoundedCornerShape(18.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
        }
    }
}

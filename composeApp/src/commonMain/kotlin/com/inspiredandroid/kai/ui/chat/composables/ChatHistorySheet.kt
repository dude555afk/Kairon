package com.inspiredandroid.kai.ui.chat.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.inspiredandroid.kai.ui.chat.ChatActions
import com.inspiredandroid.kai.ui.chat.ConversationSummary
import com.inspiredandroid.kai.ui.components.VerticalScrollbarForList
import com.inspiredandroid.kai.ui.components.animatedGradientBorder
import com.inspiredandroid.kai.ui.handCursor
import kai.composeapp.generated.resources.Res
import kai.composeapp.generated.resources.chat_history_delete_content_description
import kai.composeapp.generated.resources.chat_history_empty
import kai.composeapp.generated.resources.chat_history_heartbeat_label
import kai.composeapp.generated.resources.chat_history_title
import kai.composeapp.generated.resources.ic_add
import kai.composeapp.generated.resources.ic_history
import kai.composeapp.generated.resources.kairon_conversation_options
import kai.composeapp.generated.resources.new_chat_content_description
import kai.composeapp.generated.resources.snackbar_conversation_deleted
import kai.composeapp.generated.resources.snackbar_undo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents.Companion.Format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

private val dateFormat = Format {
    day()
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    year()
}

@Composable
internal fun ChatHistorySheet(
    conversations: ImmutableList<ConversationSummary>,
    currentConversationId: String?,
    pendingConversationDeletion: String?,
    actions: ChatActions,
    onDismiss: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onConversationSelected: () -> Unit = {},
) {
    // Material 3 owns drag tracking, dismissal, scrim and drawer animation.
    // This composable supplies content only, so the chat shell controls gestures.
    ModalDrawerSheet(
        // About 30% narrower than the old 88%-width sheet. Inset the entire
        // surface to evoke Zen's detached vertical-tabs panel.
        modifier = Modifier.fillMaxWidth(0.62f).widthIn(max = 252.dp)
            .padding(start = 10.dp, top = 12.dp, bottom = 12.dp)
            .fillMaxHeight(),
        drawerShape = RoundedCornerShape(22.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        val snackbarHostState = remember { SnackbarHostState() }
        var renamingId by remember { mutableStateOf<String?>(null) }
        var renameText by remember { mutableStateOf("") }
        val deletedMessage = stringResource(Res.string.snackbar_conversation_deleted)
        val undoLabel = stringResource(Res.string.snackbar_undo)

        LaunchedEffect(pendingConversationDeletion) {
            if (pendingConversationDeletion == null) return@LaunchedEffect
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = deletedMessage,
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) {
                actions.undoDeleteConversation()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize().padding(top = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.chat_history_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(
                        onClick = {
                            actions.startNewChat()
                            onDismiss()
                        },
                    ) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_add),
                            contentDescription = stringResource(Res.string.new_chat_content_description),
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))

                if (conversations.isEmpty()) {
                    Text(
                        text = stringResource(Res.string.chat_history_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp),
                    )
                } else {
                    val historyListState = rememberLazyListState()
                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = historyListState,
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            items(conversations, key = { it.id }) { conversation ->
                                val isActive = conversation.id == currentConversationId
                                val animatedRowColor by animateColorAsState(
                                    if (isActive) MaterialTheme.colorScheme.surfaceContainerHigh else Color.Transparent,
                                    label = "historySelection",
                                )
                                val borderModifier = if (conversation.isInteractive) {
                                    Modifier.animatedGradientBorder(
                                        cornerRadius = 12.dp,
                                        backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    )
                                } else {
                                    Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            animatedRowColor,
                                        )
                                }
                                Row(
                                    modifier = borderModifier
                                        .fillMaxWidth()
                                        .handCursor()
                                        .clickable {
                                            onConversationSelected()
                                            actions.loadConversation(conversation.id)
                                            onDismiss()
                                        }
                                        .padding(vertical = 8.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        if (conversation.isHeartbeat) {
                                            Row(
                                                modifier = Modifier
                                                    .padding(bottom = 4.dp)
                                                    .background(
                                                        color = MaterialTheme.colorScheme.tertiaryContainer,
                                                        shape = RoundedCornerShape(4.dp),
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Icon(
                                                    imageVector = vectorResource(Res.drawable.ic_history),
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                                    modifier = Modifier.size(12.dp),
                                                )
                                                Spacer(Modifier.width(4.dp))
                                                Text(
                                                    text = stringResource(Res.string.chat_history_heartbeat_label),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                                                )
                                            }
                                        }
                                        if (conversation.title.isNotEmpty()) {
                                            Text(
                                                text = conversation.title,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = if (isActive) {
                                                    MaterialTheme.colorScheme.primary
                                                } else {
                                                    MaterialTheme.colorScheme.onBackground
                                                },
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                            )
                                        }
                                        Text(
                                            text = formatDate(conversation.updatedAt),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    var menuExpanded by remember { mutableStateOf(false) }
                                    Box {
                                        IconButton(
                                            modifier = Modifier.handCursor(),
                                            onClick = { menuExpanded = true },
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = stringResource(Res.string.kairon_conversation_options),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = menuExpanded,
                                            onDismissRequest = { menuExpanded = false },
                                        ) {
                                            if (!conversation.isHeartbeat) {
                                                DropdownMenuItem(
                                                    text = { Text("Rename chat") },
                                                    onClick = {
                                                        menuExpanded = false
                                                        renamingId = conversation.id
                                                        renameText = conversation.title
                                                    },
                                                )
                                            }
                                            DropdownMenuItem(
                                                text = { Text(stringResource(Res.string.chat_history_delete_content_description)) },
                                                onClick = {
                                                    menuExpanded = false
                                                    actions.deleteConversation(conversation.id)
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                Spacer(Modifier.height(64.dp))
                            }
                        }
                        VerticalScrollbarForList(
                            listState = historyListState,
                            modifier = Modifier.align(CenterEnd).fillMaxHeight(),
                        )
                    }
                }
            }

            // Pinned bottom-right; it stays visible even when the history list scrolls.
            IconButton(
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 12.dp, bottom = 12.dp),
                onClick = {
                    onDismiss()
                    onNavigateToSettings()
                },
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (renamingId != null) {
                AlertDialog(
                    onDismissRequest = { renamingId = null },
                    title = { Text("Rename chat") },
                    text = {
                        OutlinedTextField(
                            value = renameText,
                            onValueChange = { renameText = it },
                            label = { Text("Chat name") },
                            singleLine = true,
                        )
                    },
                    confirmButton = {
                        TextButton(
                            enabled = renameText.isNotBlank(),
                            onClick = {
                                renamingId?.let { actions.renameConversation(it, renameText) }
                                renamingId = null
                            },
                        ) { Text("Save") }
                    },
                    dismissButton = {
                        TextButton(onClick = { renamingId = null }) { Text("Cancel") }
                    },
                )
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            ) { data ->
                Snackbar(snackbarData = data)
            }
        }
    }
}

private fun formatDate(epochMillis: Long): String = try {
    kotlin.time.Instant.fromEpochMilliseconds(epochMillis).format(dateFormat)
} catch (_: Exception) {
    ""
}

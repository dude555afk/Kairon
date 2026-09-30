package com.inspiredandroid.kai.ui.chat.composables

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.inspiredandroid.kai.ui.handCursor
import kai.composeapp.generated.resources.Res
import kai.composeapp.generated.resources.chat_history_delete_content_description
import kai.composeapp.generated.resources.chat_history_empty
import kai.composeapp.generated.resources.chat_history_heartbeat_label
import kai.composeapp.generated.resources.kairon_conversation_options
import kai.composeapp.generated.resources.snackbar_conversation_deleted
import kai.composeapp.generated.resources.snackbar_undo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents.Companion.Format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.stringResource

private val dateFormat = Format {
    day()
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED)
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
    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxWidth(0.78f)
            .widthIn(max = 360.dp)
            .fillMaxHeight(),
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
    ) {
        val snackbarHostState = remember { SnackbarHostState() }
        var renamingId by remember { mutableStateOf<String?>(null) }
        var renameText by remember { mutableStateOf("") }
        var searchText by remember { mutableStateOf("") }

        val matchingConversations = remember(conversations, searchText) {
            val query = searchText.trim()
            if (query.isEmpty()) {
                conversations
            } else {
                conversations.filter {
                    it.title.contains(query, ignoreCase = true) ||
                        it.searchContent.any { message -> message.contains(query, ignoreCase = true) }
                }
            }
        }

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
            if (result == SnackbarResult.ActionPerformed) actions.undoDeleteConversation()
        }

        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 18.dp, top = 18.dp, end = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Kairon",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = "Chats",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    IconButton(
                        onClick = {
                            actions.startNewChat()
                            onDismiss()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New chat",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                    singleLine = true,
                    placeholder = { Text("Search chats") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                )

                if (matchingConversations.isEmpty()) {
                    Text(
                        text = if (searchText.isNotBlank()) "No matching chats" else stringResource(Res.string.chat_history_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
                    )
                } else {
                    val listState = rememberLazyListState()
                    Box(modifier = Modifier.weight(1f)) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = listState,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            items(matchingConversations, key = { it.id }) { conversation ->
                                val isActive = conversation.id == currentConversationId
                                val background by animateColorAsState(
                                    if (isActive) {
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f)
                                    } else {
                                        Color.Transparent
                                    },
                                    label = "conversationSelection",
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(background)
                                        .handCursor()
                                        .clickable {
                                            onConversationSelected()
                                            actions.loadConversation(conversation.id)
                                            onDismiss()
                                        }
                                        .padding(start = 12.dp, top = 10.dp, bottom = 10.dp, end = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        ) {
                                            if (conversation.isPinned) {
                                                Icon(
                                                    imageVector = Icons.Default.PushPin,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(12.dp),
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                )
                                            }
                                            Text(
                                                text = conversation.title.ifBlank { "New chat" },
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false),
                                            )
                                            if (conversation.isHeartbeat) {
                                                Surface(
                                                    shape = RoundedCornerShape(999.dp),
                                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                                ) {
                                                    Text(
                                                        text = stringResource(Res.string.chat_history_heartbeat_label),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = formatDate(conversation.updatedAt),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                                        )
                                    }

                                    var menuExpanded by remember { mutableStateOf(false) }
                                    Box {
                                        IconButton(
                                            modifier = Modifier.size(38.dp),
                                            onClick = { menuExpanded = true },
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MoreVert,
                                                contentDescription = stringResource(Res.string.kairon_conversation_options),
                                                modifier = Modifier.size(18.dp),
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = menuExpanded,
                                            onDismissRequest = { menuExpanded = false },
                                        ) {
                                            if (!conversation.isHeartbeat) {
                                                DropdownMenuItem(
                                                    text = { Text(if (conversation.isPinned) "Unpin" else "Pin") },
                                                    onClick = {
                                                        menuExpanded = false
                                                        actions.setConversationPinned(conversation.id, !conversation.isPinned)
                                                    },
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Rename") },
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
                            item { Spacer(Modifier.height(10.dp)) }
                        }
                        VerticalScrollbarForList(
                            listState = listState,
                            modifier = Modifier.align(CenterEnd).fillMaxHeight(),
                        )
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.38f),
                )
                Surface(
                    onClick = {
                        onDismiss()
                        onNavigateToSettings()
                    },
                    color = Color.Transparent,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
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

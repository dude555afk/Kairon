package com.inspiredandroid.kai.ui.chat.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.inspiredandroid.kai.ui.chat.ChatActions
import com.inspiredandroid.kai.ui.handCursor
import kai.composeapp.generated.resources.Res
import kai.composeapp.generated.resources.chat_history_content_description
import kai.composeapp.generated.resources.ic_settings
import kai.composeapp.generated.resources.ic_volume_off
import kai.composeapp.generated.resources.ic_volume_up
import kai.composeapp.generated.resources.kairon_chat_more_options
import kai.composeapp.generated.resources.new_chat_content_description
import kai.composeapp.generated.resources.sandbox_content_description
import kai.composeapp.generated.resources.settings_content_description
import kai.composeapp.generated.resources.toggle_speech_output_content_description
import nl.marc_apps.tts.TextToSpeechInstance
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

/**
 * Kelivo-style chat app bar: transparent canvas, navigation on the left, conversation/model
 * identity in the middle, and only the most useful actions permanently visible.
 */
@Composable
internal fun TopBar(
    title: String,
    modelLabel: String?,
    textToSpeech: TextToSpeechInstance? = null,
    isSpeechOutputEnabled: Boolean,
    isSpeaking: Boolean,
    actions: ChatActions,
    isChatHistoryEmpty: Boolean,
    hasSavedConversations: Boolean,
    onNavigateToSettings: () -> Unit,
    isSandboxAvailable: Boolean,
    isSandboxOpen: Boolean,
    isShellExecuting: Boolean,
    onToggleSandbox: () -> Unit,
    onShowHistory: () -> Unit,
    navigationTabBar: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 58.dp)
            .padding(start = 6.dp, end = 6.dp, top = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            modifier = Modifier.size(46.dp).handCursor(),
            onClick = onShowHistory,
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = stringResource(Res.string.chat_history_content_description),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
        ) {
            Text(
                text = title.ifBlank { "Kairon" },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!modelLabel.isNullOrBlank()) {
                Text(
                    text = modelLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        IconButton(
            modifier = Modifier.size(44.dp).handCursor(),
            onClick = {
                if (isSpeechOutputEnabled && isSpeaking) {
                    actions.setIsSpeaking(false, "")
                    textToSpeech?.stop()
                }
                actions.startNewChat()
            },
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(Res.string.new_chat_content_description),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        var menuExpanded by remember { mutableStateOf(false) }
        Box {
            IconButton(
                modifier = Modifier.size(44.dp).handCursor(),
                onClick = { menuExpanded = true },
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(Res.string.kairon_chat_more_options),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                if (isSandboxAvailable) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.sandbox_content_description)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = null,
                                tint = if (isSandboxOpen || isShellExecuting) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onToggleSandbox()
                        },
                    )
                }
                if (textToSpeech != null) {
                    DropdownMenuItem(
                        text = { Text(stringResource(Res.string.toggle_speech_output_content_description)) },
                        leadingIcon = {
                            Icon(
                                imageVector = vectorResource(
                                    if (isSpeechOutputEnabled) Res.drawable.ic_volume_up else Res.drawable.ic_volume_off,
                                ),
                                contentDescription = null,
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            if (isSpeechOutputEnabled && isSpeaking) {
                                actions.setIsSpeaking(false, "")
                                textToSpeech.stop()
                            }
                            actions.toggleSpeechOutput()
                        },
                    )
                }
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.settings_content_description)) },
                    leadingIcon = {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_settings),
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        menuExpanded = false
                        onNavigateToSettings()
                    },
                )
            }
        }
    }
}

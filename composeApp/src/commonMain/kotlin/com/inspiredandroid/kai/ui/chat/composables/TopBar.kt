package com.inspiredandroid.kai.ui.chat.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
internal fun TopBar(
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
    // Keep the navigation tab bar available on large-screen platforms, but never
    // turn the phone's three navigation actions into a full-width toolbar.
    if (navigationTabBar != null) {
        Row(
            modifier = modifier.fillMaxWidth().defaultMinSize(minHeight = 56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onShowHistory) {
                Icon(Icons.Default.Menu, contentDescription = stringResource(Res.string.chat_history_content_description))
            }
            NewChatAction(actions, textToSpeech, isSpeechOutputEnabled, isSpeaking)
            if (isSandboxAvailable) {
                IconButton(onClick = onToggleSandbox) {
                    Icon(
                        Icons.Default.Dns,
                        contentDescription = stringResource(Res.string.sandbox_content_description),
                        tint = if (isSandboxOpen || isShellExecuting) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) { navigationTabBar() }
            if (textToSpeech != null) {
                SpeechAction(textToSpeech, isSpeechOutputEnabled, isSpeaking, actions)
            }
        }
        return
    }

    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.94f),
            shadowElevation = 3.dp,
        ) {
            IconButton(
                modifier = Modifier.size(48.dp).handCursor(),
                onClick = onShowHistory,
            ) {
                Icon(
                    Icons.Default.Menu,
                    contentDescription = stringResource(Res.string.chat_history_content_description),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        // One small floating action capsule. Settings, sandbox and speech live in
        // the overflow rather than consuming permanent space in the header.
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.94f),
            shadowElevation = 3.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NewChatAction(actions, textToSpeech, isSpeechOutputEnabled, isSpeaking)
                var menuExpanded by remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        modifier = Modifier.size(48.dp).handCursor(),
                        onClick = { menuExpanded = true },
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
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
                                        Icons.Default.Dns,
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
                                        vectorResource(
                                            if (isSpeechOutputEnabled) {
                                                Res.drawable.ic_volume_up
                                            } else {
                                                Res.drawable.ic_volume_off
                                            },
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
                    }
                }
            }
        }
    }
}

@Composable
private fun NewChatAction(
    actions: ChatActions,
    textToSpeech: TextToSpeechInstance?,
    isSpeechOutputEnabled: Boolean,
    isSpeaking: Boolean,
) {
    IconButton(
        modifier = Modifier.size(48.dp).handCursor(),
        onClick = {
            if (isSpeechOutputEnabled && isSpeaking) {
                actions.setIsSpeaking(false, "")
                textToSpeech?.stop()
            }
            actions.startNewChat()
        },
    ) {
        // Square-pencil is the only always-visible action next to the overflow.
        Box(
            modifier = Modifier.size(25.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier.size(22.dp),
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface),
                color = androidx.compose.ui.graphics.Color.Transparent,
            ) {}
            Icon(
                Icons.Default.Edit,
                contentDescription = stringResource(Res.string.new_chat_content_description),
                modifier = Modifier.size(17.dp),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun SpeechAction(
    textToSpeech: TextToSpeechInstance,
    isSpeechOutputEnabled: Boolean,
    isSpeaking: Boolean,
    actions: ChatActions,
) {
    IconButton(onClick = {
        if (isSpeechOutputEnabled && isSpeaking) {
            actions.setIsSpeaking(false, "")
            textToSpeech.stop()
        }
        actions.toggleSpeechOutput()
    }) {
        Icon(
            vectorResource(if (isSpeechOutputEnabled) Res.drawable.ic_volume_up else Res.drawable.ic_volume_off),
            contentDescription = stringResource(Res.string.toggle_speech_output_content_description),
        )
    }
}

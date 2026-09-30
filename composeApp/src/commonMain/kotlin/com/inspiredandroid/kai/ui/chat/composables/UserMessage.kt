package com.inspiredandroid.kai.ui.chat.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.inspiredandroid.kai.data.Attachment
import com.inspiredandroid.kai.data.ChatAppearance
import com.inspiredandroid.kai.data.ChatMessageLayout
import com.inspiredandroid.kai.data.ChatSurfaceStyle
import com.inspiredandroid.kai.decodeToImageBitmap
import com.inspiredandroid.kai.ui.components.LocalShowFullScreenImage
import com.inspiredandroid.kai.ui.handCursor
import kai.composeapp.generated.resources.Res
import kai.composeapp.generated.resources.ic_file
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class, ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun UserMessage(
    message: String,
    attachments: ImmutableList<Attachment> = persistentListOf(),
    appearance: ChatAppearance = ChatAppearance(),
    onBranch: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onRetry: (() -> Unit)? = null,
    onThinking: (() -> Unit)? = null,
    onWebSearch: (() -> Unit)? = null,
) {
    val showFullScreen = LocalShowFullScreenImage.current
    var actionMenuOpen by remember { mutableStateOf(false) }
    val bubbleShape = RoundedCornerShape(appearance.bubbleRadiusDp.dp)
    val bubbleAlpha = if (appearance.surfaceStyle == ChatSurfaceStyle.SOLID) 1f else appearance.bubbleOpacity
    val verticalSpacing = if (appearance.compactSpacing) 4.dp else 8.dp
    val bubbleModifier = if (appearance.messageLayout == ChatMessageLayout.BUBBLES) {
        Modifier
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = bubbleAlpha), bubbleShape)
            .then(
                if (appearance.borderWidthDp > 0f) {
                    Modifier.border(
                        appearance.borderWidthDp.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
                        bubbleShape,
                    )
                } else {
                    Modifier
                },
            )
    } else {
        Modifier
    }

    Column(modifier = Modifier.fillMaxWidth()) {
    SelectionContainer {
        Row(Modifier.padding(horizontal = 16.dp, vertical = verticalSpacing).combinedClickable(onClick = {}, onLongClick = { actionMenuOpen = true })) {
            Spacer(Modifier.weight(1f))
            Column(
                modifier = bubbleModifier.padding(horizontal = 14.dp, vertical = 11.dp),
                horizontalAlignment = Alignment.End,
            ) {
                val images = attachments.filter { it.mimeType.startsWith("image/") }
                val others = attachments.filter { !it.mimeType.startsWith("image/") }
                for (att in images) {
                    val imageBitmap = remember(att.data) {
                        try {
                            decodeToImageBitmap(Base64.decode(att.data))
                        } catch (_: Exception) {
                            null
                        }
                    }
                    if (imageBitmap != null) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = null,
                            modifier = Modifier
                                .widthIn(max = 200.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .handCursor()
                                .clickable { showFullScreen(imageBitmap) },
                            contentScale = ContentScale.FillWidth,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
                if (others.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        for (att in others) {
                            SuggestionChip(
                                onClick = {},
                                icon = {
                                    Icon(
                                        modifier = Modifier.size(16.dp),
                                        painter = painterResource(Res.drawable.ic_file),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onBackground,
                                    )
                                },
                                label = { Text(truncateFileName(att.fileName ?: att.mimeType)) },
                            )
                        }
                    }
                    if (message.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                    }
                }
                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = if (appearance.messageLayout == ChatMessageLayout.BUBBLES) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }
    }
    if (onBranch != null || onEdit != null || onRetry != null || onThinking != null || onWebSearch != null) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onEdit != null) {
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit prompt",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            MessageActionMenu(
                onBranch = onBranch,
                onEdit = null,
                onRetry = onRetry,
                onThinking = onThinking,
                onWebSearch = onWebSearch,
                externalOpen = actionMenuOpen,
                onExternalDismiss = { actionMenuOpen = false },
            )
        }
    }
}
}

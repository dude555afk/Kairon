package com.inspiredandroid.kai.ui.chat.composables


import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.background

import androidx.compose.foundation.border

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.layout.size

import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.default.Mic

import androidx.compose.material.icons.default.Stop

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text

import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue

import androidx.compose.runtime.remember

import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.SolidColor

import androidx.compose.ui.unit.dp

import com.inspiredandroid.kai.data.ServiceEntry

import com.inspiredandroid.kai.ui.handCursor

import kotlinx.collections.immutable.ImmutableList

import kotlinx.collections.immutable.persistentListOf

import org.jetbrains.compose.resources.stringResource

import org.jetbrains.compose.resources.vectorResource


@Composable

fun QuestionInput(
    onSend: () -> Unit,
    onVoiceToggle: () -> Unit,
    text: String,
    onTextChange: (String) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val isStreaming = isLoading
    val showSend = text.isNotBlank() && !isStreaming

    Column(
        modifier = modifier
            .padding(12.dp)
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (MaterialTheme.colorScheme.isDark) Color(0xFF171717) else Color(0xFFF4F4F6),
                    RoundedCornerShape(24.dp),
                )
                .border(
                    BorderStroke(1.dp, if (MaterialTheme.colorScheme.isDark) Color(0xFF2F2F2F) else Color(0xFFE5E7EB)),
                    RoundedCornerShape(24.dp),
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                placeholder = {
                    Text(
                        text = stringResource(org.jetbrains.compose.resources.Res.string.ask_anything),
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        ),
                    )
                },
                leadingIcon = { },
                trailingIcon = { },
                singleLine = false,
                maxLines = 4,
                minLines = 1,
                shape = RoundedCornerShape(0.dp),
                colors = androidx.compose.material3.TextFieldDefaults.colors(
                    backgroundColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                ),
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left: attachment + model pill
            Row(
                modifier = Modifier.verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (MaterialTheme.colorScheme.isDark) Color(0xFF212121) else Color(0xFFE5E7EB), CircleShape)
                        .clickable { },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach file",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(20.dp),
                    )
                }

                if (text.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .height(32.dp)
                            .padding(horizontal = 8.dp)
                            .background(if (MaterialTheme.colorScheme.isDark) Color(0xFF2F2F2F) else Color(0xFFECEEFF), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "GPT-4o",
                            style = androidx.compose.ui.text.TextStyle(
                                fontSize = 14.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onBackground,
                            ),
                        )
                    }
                }
            }

            // Right: mic + morphing primary action
            Row(
                modifier = Modifier.verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(voiceButtonBg(isStreaming), CircleShape)
                        .clickable { onVoiceToggle() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (isStreaming) Stop else Mic,
                        contentDescription = if (isStreaming) "Stop voice" else "Voice input",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(if (showSend) MaterialTheme.colorScheme.primary else if (MaterialTheme.colorScheme.isDark) Color(0xFF212121) else Color(0xFFE5E7EB), CircleShape)
                        .clickable { if (showSend) onSend() else onVoiceToggle() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (showSend) Icons.Default.Send else if (isStreaming) Stop else Mic,
                        contentDescription = if (showSend) "Send" else if (isStreaming) "Stop" else "Voice",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}


private fun voiceButtonBg(isStreaming: Boolean): Color =
    if (isStreaming) Color(0xFFDC2626) else Color(0xFF1D63FF)

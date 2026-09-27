package com.inspiredandroid.kai.ui.chat.composables

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.inspiredandroid.kai.Platform
import com.inspiredandroid.kai.currentPlatform
import com.inspiredandroid.kai.data.ReasoningEffort
import com.inspiredandroid.kai.data.ServiceEntry
import com.inspiredandroid.kai.data.imageExtensions
import com.inspiredandroid.kai.data.supportedReasoningEfforts
import com.inspiredandroid.kai.skills.SkillManifest
import com.inspiredandroid.kai.ui.handCursor
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.name
import kai.composeapp.generated.resources.Res
import kai.composeapp.generated.resources.ic_file
import kai.composeapp.generated.resources.ic_image
import kai.composeapp.generated.resources.ic_stop
import kai.composeapp.generated.resources.ic_up
import kai.composeapp.generated.resources.kairon_add_attachment
import kai.composeapp.generated.resources.prompt_ask_question
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuestionInput(
    files: ImmutableList<PlatformFile>,
    addFile: (PlatformFile) -> Unit,
    removeFile: (PlatformFile) -> Unit,
    ask: (String) -> Unit,
    supportedFileExtensions: ImmutableList<String>,
    textState: TextFieldValue,
    onTextStateChange: (TextFieldValue) -> Unit,
    isLoading: Boolean = false,
    cancel: () -> Unit = {},
    availableServices: ImmutableList<ServiceEntry> = persistentListOf(),
    onSelectService: (String) -> Unit = {},
    installedSkills: ImmutableList<SkillManifest> = persistentListOf(),
    modifier: Modifier = Modifier,
    reasoningEffort: ReasoningEffort = ReasoningEffort.AUTO,
    onSelectReasoningEffort: (ReasoningEffort) -> Unit = {},
) {
    Column(modifier = modifier) {
        // Slash autocomplete: shown when the user is typing the first token and it starts
        // with `/`. Selecting an entry rewrites the first token to the canonical skill id
        // so the ViewModel can match it at send time.
        if (installedSkills.isNotEmpty()) {
            val slashQuery = remember(textState.text, textState.selection) {
                detectSlashQuery(textState.text, textState.selection.start)
            }
            if (slashQuery != null) {
                SkillAutocomplete(
                    skills = installedSkills,
                    query = slashQuery,
                    onSelect = { skill ->
                        val text = textState.text
                        val firstSpace = text.indexOfFirst { it.isWhitespace() }
                        val rest = if (firstSpace < 0) "" else text.substring(firstSpace)
                        val newText = "/${skill.id}$rest"
                        val cursor = ("/" + skill.id + " ").length
                        onTextStateChange(
                            TextFieldValue(
                                text = if (rest.isEmpty()) "/${skill.id} " else newText,
                                selection = TextRange(cursor.coerceAtMost(if (rest.isEmpty()) cursor else newText.length)),
                            ),
                        )
                    },
                )
                Spacer(Modifier.padding(top = 4.dp))
            }
        }

        if (files.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                for (file in files) {
                    val icon = if (file.extension.lowercase() in imageExtensions) {
                        Res.drawable.ic_image
                    } else {
                        Res.drawable.ic_file
                    }
                    SuggestionChip(
                        modifier = Modifier.handCursor(),
                        onClick = { removeFile(file) },
                        icon = {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                painter = painterResource(icon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground,
                            )
                        },
                        label = {
                            DisableSelection {
                                Text(
                                    modifier = Modifier.handCursor(),
                                    text = truncateFileName(file.name),
                                )
                            }
                        },
                    )
                }
            }
        }

        fun submitQuestion() {
            val text = textState.text
            if (text.isNotBlank()) {
                ask(text.trim())
                onTextStateChange(TextFieldValue(""))
            }
        }

        val allowFileAttachment = supportedFileExtensions.isNotEmpty()
        val filePickerLauncher = if (allowFileAttachment) {
            rememberFilePickerLauncher(
                type = FileKitType.File(extensions = supportedFileExtensions),
            ) { file ->
                if (file != null) addFile(file)
            }
        } else {
            null
        }

        // Image uses the native gallery/photo picker; Files retains the document picker.
        // Keep launchers in the stable composer scope rather than inside the popup.
        val imagePickerLauncher = if (supportedFileExtensions.any { it.lowercase() in imageExtensions }) {
            rememberFilePickerLauncher(type = FileKitType.Image) { image ->
                if (image != null) addFile(image)
            }
        } else {
            null
        }
        var attachmentMenuExpanded by remember { mutableStateOf(false) }

        val focusRequester = remember { FocusRequester() }
        val maxComposerHeight = 120.dp * LocalDensity.current.fontScale
        val activeService = availableServices.firstOrNull()
        val effortLevels = activeService?.let { supportedReasoningEfforts(it.serviceId, it.modelId) }.orEmpty()

        // The entire composer is one floating surface, not a TextField with actions
        // squeezed into its trailing slot. Provider selection sits immediately by +.
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(28.dp))
                .animateContentSize(animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)),
        ) {
            TextField(
                value = textState,
                onValueChange = onTextStateChange,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .heightIn(max = maxComposerHeight)
                    .fillMaxWidth()
                    .onPreviewKeyEvent { event ->
                        // Only handle hardware keyboard on desktop/web platforms
                        if (currentPlatform !is Platform.Mobile && event.key.keyCode == Key.Enter.keyCode && event.type == KeyEventType.KeyDown) {
                            if (event.isShiftPressed) {
                                // Shift+Enter -> manually insert newline
                                val currentText = textState.text
                                val selection = textState.selection
                                val start = minOf(selection.start, selection.end).coerceIn(0, currentText.length)
                                val end = maxOf(selection.start, selection.end).coerceIn(0, currentText.length)

                                val newText = currentText.replaceRange(start, end, "\n")
                                onTextStateChange(
                                    TextFieldValue(
                                        text = newText,
                                        selection = TextRange(start + 1),
                                    ),
                                )
                                return@onPreviewKeyEvent true
                            } else {
                                // Enter without Shift -> send message and consume event
                                submitQuestion()
                                return@onPreviewKeyEvent true
                            }
                        }
                        return@onPreviewKeyEvent false
                    },
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                placeholder = {
                    Text(
                        stringResource(Res.string.prompt_ask_question),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
                keyboardActions = if (currentPlatform !is Platform.Mobile) {
                    KeyboardActions(onSend = { submitQuestion() })
                } else {
                    KeyboardActions()
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = if (currentPlatform is Platform.Mobile) ImeAction.Default else ImeAction.Send,
                ),
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 6.dp, end = 10.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (filePickerLauncher != null || imagePickerLauncher != null) {
                    Box {
                        IconButton(onClick = { attachmentMenuExpanded = true }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(Res.string.kairon_add_attachment),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        DropdownMenu(
                            expanded = attachmentMenuExpanded,
                            onDismissRequest = { attachmentMenuExpanded = false },
                            shape = RoundedCornerShape(22.dp),
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            shadowElevation = 8.dp,
                        ) {
                            if (imagePickerLauncher != null) {
                                DropdownMenuItem(
                                    text = { Text("Photos") },
                                    leadingIcon = { Icon(Icons.Default.Image, contentDescription = null) },
                                    onClick = {
                                        attachmentMenuExpanded = false
                                        imagePickerLauncher.launch()
                                    },
                                )
                            }
                            if (filePickerLauncher != null) {
                                DropdownMenuItem(
                                    text = { Text("Files") },
                                    leadingIcon = { Icon(Icons.Default.InsertDriveFile, contentDescription = null) },
                                    onClick = {
                                        attachmentMenuExpanded = false
                                        filePickerLauncher.launch()
                                    },
                                )
                            }
                        }
                    }
                }
                if (availableServices.isNotEmpty()) {
                    ServiceSelector(
                        services = availableServices,
                        onSelectService = onSelectService,
                    )
                }
                Spacer(Modifier.weight(1f))
                if (activeService != null) {
                    ReasoningEffortSelector(
                        levels = effortLevels,
                        selected = reasoningEffort,
                        onSelect = onSelectReasoningEffort,
                    )
                }
                if (isLoading) {
                    TrailingIcon(icon = Res.drawable.ic_stop, onClick = cancel, isPulsing = true)
                } else if (textState.text.isNotBlank()) {
                    TrailingIcon(icon = Res.drawable.ic_up, onClick = { submitQuestion() })
                }
            }
        }
        val inInspection = LocalInspectionMode.current
        // Mobile welcome should not open the keyboard before a deliberate tap.
        LaunchedEffect(Unit) {
            if (!inInspection && currentPlatform !is Platform.Mobile) focusRequester.requestFocus()
        }
    }
}

/**
 * Returns the slash-command query string the user is currently typing, or null if
 * the cursor isn't inside a leading `/<token>`. Examples:
 *  - `"/su"` cursor at 3 → `"su"`
 *  - `"/summarize https://…"` cursor at 4 → `"sum"`
 *  - `"hello /foo"` (slash not at start) → `null`
 *  - `"/foo bar"` cursor at 6 → `null` (cursor past first space)
 */
internal fun detectSlashQuery(text: String, cursor: Int): String? {
    if (!text.startsWith('/')) return null
    val firstSpace = text.indexOfFirst { it.isWhitespace() }
    val tokenEnd = if (firstSpace < 0) text.length else firstSpace
    if (cursor > tokenEnd) return null
    return text.substring(1, tokenEnd).lowercase()
}

/**
 * Shortens a filename that is too long to display in a chip. Returns the first [maxChars]
 * characters of the base name followed by `…` and the original extension, so the user still
 * recognizes the file type. Short names are returned unchanged.
 */
internal fun truncateFileName(name: String, maxChars: Int = 16): String {
    if (name.length <= maxChars) return name
    val dotIndex = name.lastIndexOf('.')
    return if (dotIndex > 0 && dotIndex < name.length - 1) {
        val base = name.substring(0, dotIndex)
        val ext = name.substring(dotIndex) // includes the dot
        val keep = (maxChars - ext.length - 1).coerceAtLeast(1)
        "${base.take(keep)}…$ext"
    } else {
        "${name.take(maxChars - 1)}…"
    }
}

@Composable
internal fun TrailingIcon(
    icon: org.jetbrains.compose.resources.DrawableResource = Res.drawable.ic_up,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPulsing: Boolean = false,
) {
    val pulseModifier = if (isPulsing) {
        val infiniteTransition = rememberInfiniteTransition()
        val pulseScale by infiniteTransition.animateFloat(
            initialValue = 0.92f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        )
        val pulseAlpha by infiniteTransition.animateFloat(
            initialValue = 0.7f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        )
        Modifier.graphicsLayer {
            scaleX = pulseScale
            scaleY = pulseScale
            alpha = pulseAlpha
        }
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
            .handCursor()
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            vectorResource(icon),
            modifier = Modifier.size(32.dp).then(pulseModifier),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
internal fun CircleIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(CircleShape)
            .clickable { onClick() }
            .handCursor(),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            modifier = Modifier.size(24.dp),
            contentDescription = null,
            tint = tint,
        )
    }
}

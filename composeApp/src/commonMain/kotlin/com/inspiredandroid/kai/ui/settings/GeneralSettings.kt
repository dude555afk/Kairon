package com.inspiredandroid.kai.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalUriHandler
import com.inspiredandroid.kai.data.AccentPreset
import com.inspiredandroid.kai.data.ChatAppearance
import com.inspiredandroid.kai.data.ChatMessageLayout
import com.inspiredandroid.kai.data.ChatSurfaceStyle
import com.inspiredandroid.kai.data.ThemeMode
import com.inspiredandroid.kai.ui.KaiOutlinedTextField
import com.inspiredandroid.kai.ui.components.KaiSlider
import com.inspiredandroid.kai.ui.handCursor
import kai.composeapp.generated.resources.Res
import kai.composeapp.generated.resources.ic_arrow_drop_down
import kai.composeapp.generated.resources.settings_daemon_mode
import kai.composeapp.generated.resources.settings_daemon_mode_description
import kai.composeapp.generated.resources.settings_dynamic_ui
import kai.composeapp.generated.resources.settings_dynamic_ui_description
import kai.composeapp.generated.resources.settings_theme
import kai.composeapp.generated.resources.settings_theme_dark
import kai.composeapp.generated.resources.settings_theme_description
import kai.composeapp.generated.resources.settings_theme_light
import kai.composeapp.generated.resources.settings_theme_oled
import kai.composeapp.generated.resources.settings_theme_system
import kai.composeapp.generated.resources.settings_ui_scale
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import kotlin.math.roundToInt

@Composable
internal fun GeneralContent(uiState: SettingsUiState, actions: SettingsActions) {
    // Related preferences form one calm settings group instead of stacked giant cards.
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SettingsCard(innerPadding = false) {
            if (uiState.showDaemonToggle) {
                Box(Modifier.fillMaxWidth().padding(16.dp)) {
                    DaemonModeToggle(
                        isDaemonEnabled = uiState.isDaemonEnabled,
                        onToggleDaemon = actions.onToggleDaemon,
                    )
                }
                GeneralDivider()
            }
            Box(Modifier.fillMaxWidth().padding(16.dp)) {
                DynamicUiToggle(
                    isDynamicUiEnabled = uiState.isDynamicUiEnabled,
                    onToggleDynamicUi = actions.onToggleDynamicUi,
                )
            }
            GeneralDivider()
            Box(Modifier.fillMaxWidth().padding(16.dp)) {
                ToggleableHeadline(
                    title = "Show Thinking header",
                    description = "Show or hide the reasoning disclosure in chat. This only changes its appearance.",
                    checked = uiState.isThinkingHeaderVisible,
                    onCheckedChange = actions.onToggleThinkingHeader,
                )
            }
            GeneralDivider()
            Box(Modifier.fillMaxWidth().padding(16.dp)) {
                ThemeModePicker(
                    themeMode = uiState.themeMode,
                    onChangeThemeMode = actions.onChangeThemeMode,
                )
            }
            GeneralDivider()
            Box(Modifier.fillMaxWidth().padding(16.dp)) {
                Column {
                    Text("Accent", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Lightweight colour presets. No wallpapers or extra assets.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        KaiOutlinedTextField(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            value = uiState.accentPreset.label,
                            onValueChange = {},
                            readOnly = true,
                        )
                        Box(Modifier.matchParentSize().clickable { expanded = true })
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            AccentPreset.entries.forEach { preset ->
                                DropdownMenuItem(
                                    text = { Text(preset.label) },
                                    onClick = {
                                        expanded = false
                                        actions.onChangeAccentPreset(preset)
                                    },
                                )
                            }
                        }
                    }
                }
            }
            if (uiState.showUiScale) {
                GeneralDivider()
                Box(Modifier.fillMaxWidth().padding(16.dp)) {
                    UiScaleSection(
                        uiScale = uiState.uiScale,
                        onChangeUiScale = actions.onChangeUiScale,
                    )
                }
            }
        }
        SettingsCard {
            ChatAppearanceSection(
                appearance = uiState.chatAppearance,
                onChange = actions.onChangeChatAppearance,
            )
        }
        SettingsCard {
            val uriHandler = LocalUriHandler.current
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Experimental online voices (Android)", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Kairon uses your phone's selected Android TTS engine. For keyless Edge Neural voices, install the third-party Edge TTS Android engine, select it in Android Settings > Text-to-speech output, then reopen Kairon. Online requests go to Microsoft's service, which can see your IP; availability and unlimited use are not guaranteed.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    "Open Edge TTS engine project ↗",
                    modifier = Modifier.padding(top = 8.dp).clickable {
                        uriHandler.openUri("https://github.com/Initsnow/edge-tts-android")
                    },
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
        SettingsCard {
            ExportImportSection(
                onExportSettings = actions.onExportSettings,
                onPrepareExport = actions.onPrepareExport,
                onImportSettings = actions.onImportSettings,
            )
        }
    }
}

@Composable
private fun GeneralDivider() {
    androidx.compose.material3.HorizontalDivider(
        modifier = Modifier.padding(start = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
    )
}

@Composable
private fun DaemonModeToggle(
    isDaemonEnabled: Boolean,
    onToggleDaemon: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ToggleableHeadline(
            title = stringResource(Res.string.settings_daemon_mode),
            description = stringResource(Res.string.settings_daemon_mode_description),
            checked = isDaemonEnabled,
            onCheckedChange = onToggleDaemon,
        )
    }
}

@Composable
private fun DynamicUiToggle(
    isDynamicUiEnabled: Boolean,
    onToggleDynamicUi: (Boolean) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ToggleableHeadline(
            title = stringResource(Res.string.settings_dynamic_ui),
            description = stringResource(Res.string.settings_dynamic_ui_description),
            checked = isDynamicUiEnabled,
            onCheckedChange = onToggleDynamicUi,
        )
    }
}

@Composable
private fun ThemeModePicker(
    themeMode: ThemeMode,
    onChangeThemeMode: (ThemeMode) -> Unit,
) {
    val options = listOf(
        ThemeMode.System to stringResource(Res.string.settings_theme_system),
        ThemeMode.Light to stringResource(Res.string.settings_theme_light),
        ThemeMode.Dark to stringResource(Res.string.settings_theme_dark),
        ThemeMode.OledBlack to stringResource(Res.string.settings_theme_oled),
    )
    val selectedLabel = options.first { it.first == themeMode }.second
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.settings_theme),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(Res.string.settings_theme_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            KaiOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = selectedLabel,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        modifier = Modifier.handCursor(),
                        imageVector = vectorResource(Res.drawable.ic_arrow_drop_down),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                },
            )
            // Transparent overlay to capture clicks reliably on all platforms
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .handCursor()
                    .clickable { expanded = true },
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                shape = RoundedCornerShape(16.dp),
            ) {
                options.forEach { (mode, label) ->
                    val isSelected = mode == themeMode
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                            )
                        },
                        onClick = {
                            expanded = false
                            onChangeThemeMode(mode)
                        },
                        modifier = Modifier
                            .handCursor()
                            .then(
                                if (isSelected) {
                                    Modifier
                                        .padding(horizontal = 4.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(12.dp),
                                        )
                                } else {
                                    Modifier
                                },
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun UiScaleSection(
    uiScale: Float,
    onChangeUiScale: (Float) -> Unit,
) {
    var sliderValue by remember(uiScale) { mutableStateOf(uiScale) }
    val steps = 14 // 16 snap points from 50% to 200% in 10% increments (14 intermediate)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.settings_ui_scale),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "${(sliderValue * 100).roundToInt()}%",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        KaiSlider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            onValueChangeFinished = { onChangeUiScale(sliderValue) },
            valueRange = 0.5f..2.0f,
            steps = steps,
        )
    }
}


@Composable
private fun ChatAppearanceSection(
    appearance: ChatAppearance,
    onChange: (ChatAppearance) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Chat appearance", style = MaterialTheme.typography.titleMedium)
        Text(
            "Kelivo-style presentation controls. Dynamic UI and message data are unchanged.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        var layoutExpanded by remember { mutableStateOf(false) }
        Box {
            KaiOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = when (appearance.messageLayout) {
                    ChatMessageLayout.BUBBLES -> "Bubbles"
                    ChatMessageLayout.FLAT -> "Flat"
                },
                onValueChange = {},
                label = { Text("Message layout") },
                readOnly = true,
            )
            Box(Modifier.matchParentSize().clickable { layoutExpanded = true })
            DropdownMenu(expanded = layoutExpanded, onDismissRequest = { layoutExpanded = false }) {
                ChatMessageLayout.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.name.lowercase().replaceFirstChar { it.uppercaseChar() }) },
                        onClick = {
                            layoutExpanded = false
                            onChange(appearance.copy(messageLayout = option))
                        },
                    )
                }
            }
        }

        var surfaceExpanded by remember { mutableStateOf(false) }
        Box {
            KaiOutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = when (appearance.surfaceStyle) {
                    ChatSurfaceStyle.SOLID -> "Solid"
                    ChatSurfaceStyle.TRANSLUCENT -> "Translucent"
                },
                onValueChange = {},
                label = { Text("Bubble surface") },
                readOnly = true,
            )
            Box(Modifier.matchParentSize().clickable { surfaceExpanded = true })
            DropdownMenu(expanded = surfaceExpanded, onDismissRequest = { surfaceExpanded = false }) {
                ChatSurfaceStyle.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.name.lowercase().replaceFirstChar { it.uppercaseChar() }) },
                        onClick = {
                            surfaceExpanded = false
                            onChange(appearance.copy(surfaceStyle = option))
                        },
                    )
                }
            }
        }

        var radius by remember(appearance.bubbleRadiusDp) { mutableStateOf(appearance.bubbleRadiusDp) }
        Text("Bubble radius · ${radius.roundToInt()} dp", style = MaterialTheme.typography.labelLarge)
        KaiSlider(
            value = radius,
            onValueChange = { radius = it },
            onValueChangeFinished = { onChange(appearance.copy(bubbleRadiusDp = radius)) },
            valueRange = 6f..32f,
            steps = 12,
        )

        var opacity by remember(appearance.bubbleOpacity) { mutableStateOf(appearance.bubbleOpacity) }
        Text("Bubble opacity · ${(opacity * 100).roundToInt()}%", style = MaterialTheme.typography.labelLarge)
        KaiSlider(
            value = opacity,
            onValueChange = { opacity = it },
            onValueChangeFinished = { onChange(appearance.copy(bubbleOpacity = opacity)) },
            valueRange = 0.35f..1f,
            steps = 12,
        )

        var composerRadius by remember(appearance.composerRadiusDp) { mutableStateOf(appearance.composerRadiusDp) }
        Text("Composer radius · ${composerRadius.roundToInt()} dp", style = MaterialTheme.typography.labelLarge)
        KaiSlider(
            value = composerRadius,
            onValueChange = { composerRadius = it },
            onValueChangeFinished = { onChange(appearance.copy(composerRadiusDp = composerRadius)) },
            valueRange = 12f..32f,
            steps = 9,
        )

        ToggleableHeadline(
            title = "Compact spacing",
            description = "Reduce vertical space between messages and chat controls.",
            checked = appearance.compactSpacing,
            onCheckedChange = { onChange(appearance.copy(compactSpacing = it)) },
        )
    }
}

@file:OptIn(ExperimentalFoundationApi::class)

package com.inspiredandroid.kai.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateFloatAsState
import androidx.compose.animation.spring
import androidx.compose.animation.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.dragAndDropTarget
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Waveform
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalMotionEnabled
import androidx.compose.ui.platform.LocalScreenDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.inspiredandroid.kai.ui.markdown.KaiUiBlock
import com.inspiredandroid.kai.ui.markdown.parseMarkdown
import kai.composeapp.generated.resources.Res
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

// ══════════════════════════════════════════════════════════════════════════════
// COLOR TOKENS
// ══════════════════════════════════════════════════════════════════════════════

data class KaironColors(
    val backgroundCanvas: Color,
    val surfaceElevated: Color,
    val surfaceElevatedBorder: Color,
    val surfaceSubtle: Color,
    val surfaceTint: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val accentPrimary: Color,
    val drawerScrim: Color,
    val placeholder: Color,
    val mutedIcons: Color,
)

val LightKaironColors = KaironColors(
    backgroundCanvas = Color(0xFFFFFFFF),
    surfaceElevated = Color(0xFFFFFFFF),
    surfaceElevatedBorder = Color(0xFFE5E7EB),
    surfaceSubtle = Color(0xFFF4F4F6),
    surfaceTint = Color(0xFFECEEFF),
    textPrimary = Color(0xFF0D0D0D),
    textSecondary = Color(0xFF6B7280),
    accentPrimary = Color(0xFF1D63FF),
    drawerScrim = Color(0xFF000000).copy(alpha = 0.32f),
    placeholder = Color(0xFF6B7280),
    mutedIcons = Color(0xFF6B7280),
)

val DarkKaironColors = KaironColors(
    backgroundCanvas = Color(0xFF000000),
    surfaceElevated = Color(0xFF212121),
    surfaceElevatedBorder = Color(0xFF303030),
    surfaceSubtle = Color(0xFF2F2F2F),
    surfaceTint = Color(0xFF1E293B),
    textPrimary = Color(0xFFECECF1),
    textSecondary = Color(0xFF9B9BA6),
    accentPrimary = Color(0xFF2563EB),
    drawerScrim = Color(0xFF000000).copy(alpha = 0.55f),
    placeholder = Color(0xFF9B9BA6),
    mutedIcons = Color(0xFF9B9BA6),
)

val LocalKaironColors = androidx.compose.runtime.staticCompositionLocalOf {
    LightKaironColors
}

// ══════════════════════════════════════════════════════════════════════════════
// SCALE HELPER (412dp reference)
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun rememberScreenScale(): Float {
    val config = LocalConfiguration.current
    return remember(config.screenWidthDp) {
        (config.screenWidthDp / 412f).coerceIn(0.85f, 1.15f)
    }
}

fun androidx.compose.ui.unit.Dp.proportional(scale: Float): Dp = this * scale

// ══════════════════════════════════════════════════════════════════════════════
// MAIN SHELL
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun KaironChatScreen(
    onSend: () -> Unit = {},
    onVoiceToggle: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
) {
    val scale = rememberScreenScale()
    val colors = LocalKaironColors.current
    var drawerOpen by remember { mutableStateOf(false) }
    var drawerOffset by remember {
        animateFloatAsState(
            targetValue = if (drawerOpen) 1f else 0f,
            animationSpec = spring(stiffness = 380f, dampingRatio = 0.88f),
        )
    }
    var scrimAlpha by remember {
        animateFloatAsState(
            targetValue = if (drawerOpen) 1f else 0f,
            animationSpec = spring(stiffness = 380f, dampingRatio = 0.88f),
        )
    }
    var inputText by remember { mutableStateOf("") }
    var isStreaming by remember { mutableStateOf(false) }

    val drawerWidthDp =
        (LocalConfiguration.current.screenWidthDp * 0.82f).proportional(scale)

    androidx.compose.foundation.layout.Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars),
        containerColor = colors.backgroundCanvas,
    ) { paddingValues ->

        Box(Modifier.fillMaxSize()) {
            // Dimmed scrim behind drawer
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x000000).copy(alpha = scrimAlpha * colors.drawerScrim.alpha))
                    .clickable { drawerOpen = false }
            )

            // Drawer
            SidebarDrawer(
                isOpen = drawerOpen,
                offset = drawerOffset,
                width = drawerWidthDp,
                colors = colors,
                scale = scale,
                onClose = { drawerOpen = false },
                onNavigateToSettings = onNavigateToSettings,
            )

            // Main content
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { },
                            onDrag = { change, dragAmount ->
                                val dx = dragAmount.changePosition.x.abs()
                                val dy = dragAmount.changePosition.y.abs()
                                if (dx > 1.5f * dy && dx > 20.dp.toPx()) {
                                    drawerOpen = true
                                }
                            },
                        )
                    },
            ) {
                TopBar(
                    colors = colors,
                    scale = scale,
                    onMenuClick = { drawerOpen = true },
                )

                Spacer(Modifier.height(16.dp))

                ChatArea(
                    colors = colors,
                    scale = scale,
                    isStreaming = isStreaming,
                )

                BottomComposer(
                    colors = colors,
                    scale = scale,
                    inputText = inputText,
                    onTextChange = { inputText = it },
                    isStreaming = isStreaming,
                    onSend = { onSend(); inputText = "" },
                    onVoiceToggle = { onVoiceToggle(); isStreaming = !isStreaming },
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// TOP APP BAR
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun TopBar(
    colors: KaironColors,
    scale: Float,
    onMenuClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp.proportional(scale))
            .padding(horizontal = 16.dp.proportional(scale)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
            8.dp.proportional(scale),
        ),
    ) {
        // Menu / drawer
        Box(
            Modifier
                .size(48.dp.proportional(scale))
                .clickable { onMenuClick() },
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Open sidebar",
                tint = colors.textSecondary,
                modifier = androidx.compose.ui.Modifier.size(24.dp.proportional(scale)),
            )
        }

        // Optional pill badge
        Box(
            Modifier
                .height(34.dp.proportional(scale))
                .padding(horizontal = 14.dp.proportional(scale))
                .background(colors.surfaceTint, RoundedCornerShape(999.dp))
                .clickable { },
        ) {
            Text(
                text = "GPT-4o",
                style = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium,
                ),
                color = colors.textPrimary,
                modifier = androidx.compose.ui.Modifier.align(Alignment.CenterVertically),
            )
        }

        androidx.compose.foundation.layout.Spacer(
            Modifier = androidx.compose.ui.Modifier.weight(1f),
        )

        // Right: dashed-circle icon (new chat toggle)
        Box(
            Modifier
                .size(48.dp.proportional(scale))
                .clickable { },
        ) {
            Icon(
                imageVector = Icons.Default.AddCircleOutline,
                contentDescription = "New chat",
                tint = colors.textSecondary,
                modifier = androidx.compose.ui.Modifier.size(24.dp.proportional(scale)),
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// SIDEBAR DRAWER
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun SidebarDrawer(
    isOpen: Boolean,
    offset: Float,
    width: Dp,
    colors: KaironColors,
    scale: Float,
    onClose: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    val drawerWidthPx = with(LocalDensity.current) { width.toPx() }

    Box(
        Modifier
            .fillMaxHeight()
            .width(width)
            .graphicsLayer {
                translationX = -drawerWidthPx * (1f - offset)
                alpha = offset.coerceIn(0f, 1f)
            }
            .zIndex(1f),
    ) {
        Box(Modifier.fillMaxSize().background(colors.surfaceElevated)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp.proportional(scale)),
            ) {
                // Header
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp.proportional(scale))
                        .padding(top = 8.dp.proportional(scale)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Kairon",
                        style = TextStyle(
                            fontSize = 20.sp,
                            lineHeight = 26.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = -0.01.em,
                        ),
                        color = colors.textPrimary,
                    )
                    Spacer(Modifier = androidx.compose.ui.Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = colors.textSecondary,
                        modifier = androidx.compose.ui.Modifier.size(24.dp.proportional(scale)),
                    )
                }

                androidx.compose.foundation.layout.Divider(
                    Modifier.fillMaxWidth(),
                    color = colors.surfaceElevatedBorder,
                    thickness = 1.dp,
                )

                // Primary Nav
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    listOf("Home", "Agent", "Explore", "Library", "Settings")
                        .forEach { item ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(48.dp.proportional(scale))
                                    .padding(horizontal = 16.dp.proportional(scale))
                                    .clickable { },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(
                                    14.dp.proportional(scale),
                                ),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    tint = colors.mutedIcons,
                                    modifier = androidx.compose.ui.Modifier.size(
                                        22.dp.proportional(scale),
                                    ),
                                )
                                Text(
                                    text = item,
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        lineHeight = 20.sp,
                                        fontWeight = FontWeight.Medium,
                                    ),
                                    color = colors.textPrimary,
                                )
                            }
                        }
                }

                Spacer(Modifier = androidx.compose.ui.Modifier.weight(1f))

                // Chat History (stub)
                Text(
                    text = "Recent chats",
                    style = TextStyle(
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                    color = colors.textSecondary,
                )

                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp.proportional(scale)),
                ) {
                    items(5) { index ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(44.dp.proportional(scale))
                                .padding(horizontal = 16.dp.proportional(scale))
                                .clickable { },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Thread $index",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight.Normal,
                                ),
                                color = colors.textPrimary,
                                modifier = androidx.compose.ui.Modifier.weight(1f),
                            )
                            Box(Modifier.size(8.dp.proportional(scale)))
                                .background(colors.accentPrimary, CircleShape)
                        }
                        androidx.compose.foundation.layout.Divider(
                            Modifier.fillMaxWidth(),
                            color = colors.surfaceElevatedBorder,
                            thickness = 0.5.dp,
                        )
                    }
                }

                // Pinned footer
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(64.dp.proportional(scale))
                        .padding(top = 8.dp.proportional(scale))
                        .border(
                            BorderStroke(1.dp, colors.surfaceElevatedBorder),
                            shape = RoundedCornerShape(0.dp),
                        ),
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp.proportional(scale)),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        // Chat pill
                        Box(
                            Modifier
                                .height(40.dp.proportional(scale))
                                .widthIn(minWidth = 96.dp.proportional(scale))
                                .background(colors.accentPrimary, RoundedCornerShape(999.dp))
                                .clickable { },
                        ) {
                            Row(
                                Modifier.align(Alignment.Center),
                                horizontalArrangement = Arrangement.spacedBy(
                                    6.dp.proportional(scale),
                                ),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = androidx.compose.ui.Modifier.size(
                                        14.dp.proportional(scale),
                                    ),
                                )
                                Text(
                                    text = "Chat",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                    color = Color.White,
                                )
                            }
                        }

                        // Avatar
                        Box(
                            Modifier
                                .size(36.dp.proportional(scale))
                                .background(colors.surfaceSubtle, CircleShape)
                                .clickable { onNavigateToSettings() },
                        ) {
                            Text(
                                text = "U",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    lineHeight = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                ),
                                color = colors.textPrimary,
                                modifier = androidx.compose.ui.Modifier.align(Alignment.Center),
                            )
                        }

                        // Voice quick-launch
                        Box(
                            Modifier
                                .size(36.dp.proportional(scale))
                                .background(colors.accentPrimary, CircleShape)
                                .clickable { },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice",
                                tint = Color.White,
                                modifier = androidx.compose.ui.Modifier.size(20.dp.proportional(scale)),
                            )
                        }
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// CHAT AREA
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun ChatArea(
    colors: KaironColors,
    scale: Float,
    isStreaming: Boolean,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(Unit, isStreaming) {
        if (isStreaming) {
            listState.scrollToItem(0)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp.proportional(scale)),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(20.dp.proportional(scale)),
    ) {
        item {
            // User bubble
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                Box(
                    Modifier
                        .widthIn(
                            max = (LocalConfiguration.current.screenWidthDp * 0.78f)
                                .dp
                                .proportional(scale),
                        )
                        .padding(
                            horizontal = 14.dp.proportional(scale),
                            vertical = 12.dp.proportional(scale),
                        )
                        .background(
                            colors.surfaceSubtle,
                            RoundedCornerShape(18.dp.proportional(scale)),
                        ),
                ) {
                    Text(
                        text = "Can you help me build a Compose UI for my Android assistant?",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                        color = colors.textPrimary,
                    )
                }
            }
        }

        item {
            // Assistant response
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp.proportional(scale)),
            ) {
                Text(
                    text = "Sure thing. Here's a ChatGPT-style shell with Material 3 tokens, edge-to-edge insets, and a morphing composer button.",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                    color = colors.textPrimary,
                )

                // Response action bar
                ResponseActionBar(
                    colors = colors,
                    scale = scale,
                    onCopy = { },
                    onReadAloud = { },
                    onLike = { },
                    onOverflow = { },
                )
            }
        }

        if (isStreaming) {
            item {
                // Thinking shimmer pill
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(32.dp.proportional(scale))
                        .background(colors.surfaceTint, RoundedCornerShape(999.dp)),
                ) {
                    Row(
                        Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp.proportional(scale),
                        ),
                    ) {
                        Box(
                            Modifier
                                .size(20.dp.proportional(scale))
                                .background(colors.accentPrimary, CircleShape),
                        )
                        Text(
                            text = "Thinking",
                            style = TextStyle(
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                fontWeight = FontWeight.Normal,
                            ),
                            color = colors.textSecondary,
                        )
                    }
                }
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// RESPONSE ACTION BAR
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun ResponseActionBar(
    colors: KaironColors,
    scale: Float,
    onCopy: () -> Unit,
    onReadAloud: () -> Unit,
    onLike: () -> Unit,
    onOverflow: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(40.dp.proportional(scale))
            .padding(top = 8.dp.proportional(scale)),
        horizontalArrangement = Arrangement.spacedBy(8.dp.proportional(scale)),
    ) {
        listOf(
            Pair(Icons.Default.ContentCopy, "Copy"),
            Pair(Icons.Default.VolumeUp, "Read Aloud"),
            Pair(Icons.Default.ThumbUp, "Like"),
            Pair(Icons.Default.MoreVert, "More"),
        ).forEachIndexed { index, (icon, label) ->
            Box(
                Modifier
                    .size(36.dp.proportional(scale))
                    .border(
                        BorderStroke(
                            1.dp,
                            colors.mutedIcons.copy(alpha = 0.4f),
                        ),
                        CircleShape,
                    )
                    .clickable {
                        when (label) {
                            "Copy" -> onCopy()
                            "Read Aloud" -> onReadAloud()
                            "Like" -> onLike()
                            "More" -> onOverflow()
                        }
                    },
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = colors.mutedIcons,
                    modifier = androidx.compose.ui.Modifier.size(18.dp.proportional(scale)),
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
// BOTTOM COMPOSER
// ══════════════════════════════════════════════════════════════════════════════

@Composable
fun BottomComposer(
    colors: KaironColors,
    scale: Float,
    inputText: String,
    onTextChange: (String) -> Unit,
    isStreaming: Boolean,
    onSend: () -> Unit,
    onVoiceToggle: () -> Unit,
) {
    val motionEnabled = LocalMotionEnabled.current
    val duration = if (motionEnabled) 160 else 0

    Column(
        Modifier
            .fillMaxWidth()
            .padding(
                start = 12.dp.proportional(scale),
                end = 12.dp.proportional(scale),
                bottom = 10.dp.proportional(scale),
            ),
    ) {
        // Composer container
        Box(
            Modifier
                .fillMaxWidth()
                .minHeight(92.dp.proportional(scale))
                .padding(
                    horizontal = 16.dp.proportional(scale),
                    vertical = 12.dp.proportional(scale),
                )
                .background(
                    color = colors.surfaceElevated,
                    shape = RoundedCornerShape(24.dp.proportional(scale)),
                )
                .border(
                    BorderStroke(1.dp, colors.surfaceElevatedBorder),
                    RoundedCornerShape(24.dp.proportional(scale)),
                )
                .shadow(
                    2.dp,
                    colors = if (colors == LightKaironColors) {
                        Shadow(colors.mutedIcons.copy(alpha = 0.2f))
                    } else {
                        Shadow(Color.Transparent)
                    },
                )
                .animateContentSize(
                    animationSpec = spring(stiffness = 420f, dampingRatio = 0.85f),
                ),
        ) {
            Column(Modifier.fillMaxSize()) {
                // Row 1: Input
                androidx.compose.material3.TextField(
                    value = inputText,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        color = colors.textPrimary,
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.ask_anything),
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = colors.placeholder,
                            ),
                        )
                    },
                    leadingIcon = { },
                    trailingIcon = { },
                    singleLine = false,
                    maxLines = 5,
                    visualTransformation = VisualTransformation.None,
                    shape = RoundedCornerShape(0.dp),
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                        backgroundColor = Color.Transparent,
                    ),
                )

                Spacer(Modifier.height(8.dp.proportional(scale)))

                // Row 2: Controls
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Left: attachment + pill
                    Row(
                        Modifier.verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp.proportional(scale),
                        ),
                    ) {
                        Box(
                            Modifier
                                .size(36.dp.proportional(scale))
                                .background(colors.surfaceSubtle, CircleShape)
                                .clickable { },
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = "Attach",
                                tint = colors.textSecondary,
                                modifier = androidx.compose.ui.Modifier.size(
                                    20.dp.proportional(scale),
                                ),
                            )
                        }

                        if (inputText.isNotEmpty()) {
                            Box(
                                Modifier
                                    .height(32.dp.proportional(scale))
                                    .padding(horizontal = 8.dp.proportional(scale))
                                    .background(colors.surfaceTint, RoundedCornerShape(999.dp)),
                            ) {
                                Text(
                                    text = "GPT-4o",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                    ),
                                    color = colors.textPrimary,
                                    modifier = androidx.compose.ui.Modifier
                                        .align(Alignment.CenterVertically),
                                )
                            }
                        }
                    }

                    // Right: mic + primary action
                    Row(
                        Modifier.verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp.proportional(scale),
                        ),
                    ) {
                        Box(
                            Modifier
                                .size(36.dp.proportional(scale))
                                .background(colors.surfaceSubtle, CircleShape)
                                .clickable { onVoiceToggle() },
                        ) {
                            Icon(
                                imageVector = if (isStreaming) Icons.Default.Stop
                                else Icons.Default.Mic,
                                contentDescription = if (isStreaming) "Stop" else "Voice",
                                tint = Color.White,
                                modifier = androidx.compose.ui.Modifier.size(
                                    20.dp.proportional(scale),
                                ),
                            )
                        }

                        // Morphing primary action
                        AnimatedContent(
                            targetState = if (inputText.isNotEmpty() && !isStreaming) {
                                "send"
                            } else if (isStreaming) {
                                "stop"
                            } else {
                                "voice"
                            },
                            animationSpec = tween(durationMillis = duration),
                            contentType = null,
                            label = "action button",
                        ) { state ->
                            when (state) {
                                "voice" -> {
                                    Box(
                                        Modifier
                                            .size(36.dp.proportional(scale))
                                            .background(colors.accentPrimary, CircleShape)
                                            .clickable { onVoiceToggle() },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Waveform,
                                            contentDescription = "Voice",
                                            tint = Color.White,
                                            modifier = androidx.compose.ui.Modifier.size(
                                                20.dp.proportional(scale),
                                            ),
                                        )
                                    }
                                }
                                "send" -> {
                                    Box(
                                        Modifier
                                            .size(36.dp.proportional(scale))
                                            .background(colors.textPrimary, CircleShape)
                                            .clickable { onSend() },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Send,
                                            contentDescription = "Send",
                                            tint = Color.White,
                                            modifier = androidx.compose.ui.Modifier.size(
                                                20.dp.proportional(scale),
                                            ),
                                        )
                                    }
                                }
                                "stop" -> {
                                    Box(
                                        Modifier
                                            .size(36.dp.proportional(scale))
                                            .background(colors.accentPrimary, CircleShape)
                                            .clickable { onVoiceToggle() },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stop,
                                            contentDescription = "Stop",
                                            tint = Color.White,
                                            modifier = androidx.compose.ui.Modifier.size(
                                                12.dp.proportional(scale),
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
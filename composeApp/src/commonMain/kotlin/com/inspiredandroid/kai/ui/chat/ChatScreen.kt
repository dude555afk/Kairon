@file:OptIn(
    ExperimentalFoundationApi::class,
)

package com.inspiredandroid.kai.ui.chat


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.inspiredandroid.kai.BackIcon
import com.inspiredandroid.kai.TerminalLine
import com.inspiredandroid.kai.data.Service
import com.inspiredandroid.kai.data.supportsAgenticFlows
import com.inspiredandroid.kai.getBackgroundDispatcher
import com.inspiredandroid.kai.onDragAndDropEventDropped
import com.inspiredandroid.kai.ui.build.KaiBuildScreen
import com.inspiredandroid.kai.ui.chat.composables.BotMessage
import com.inspiredandroid.kai.ui.chat.composables.ChatHistorySheet
import com.inspiredandroid.kai.ui.chat.composables.CircleIconButton
import com.inspiredandroid.kai.ui.chat.composables.EmptyState
import com.inspiredandroid.kai.ui.chat.composables.ErrorMessage
import com.inspiredandroid.kai.ui.chat.composables.FreeProviderSuggestionsPanel
import com.inspiredandroid.kai.ui.chat.composables.HeartbeatBanner
import com.inspiredandroid.kai.ui.chat.composables.PendingSmsBanners
import com.inspiredandroid.kai.ui.chat.composables.QuestionInput
import com.inspiredandroid.kai.ui.chat.composables.ServiceSelector
import com.inspiredandroid.kai.ui.chat.composables.TopBar
import com.inspiredandroid.kai.ui.chat.composables.TrailingIcon
import com.inspiredandroid.kai.ui.chat.composables.UserMessage
import com.inspiredandroid.kai.ui.chat.composables.WaitingResponseRow
import com.inspiredandroid.kai.ui.chat.composables.uiErrorText
import com.inspiredandroid.kai.ui.components.LogoAnimation
import com.inspiredandroid.kai.ui.components.VerticalScrollbarForList
import com.inspiredandroid.kai.ui.components.animatedGradientBorder
import com.inspiredandroid.kai.ui.dynamicui.FrozenSubmission
import com.inspiredandroid.kai.ui.dynamicui.KaiUiRenderer
import com.inspiredandroid.kai.ui.dynamicui.toSpeakableText
import com.inspiredandroid.kai.ui.handCursor
import com.inspiredandroid.kai.ui.markdown.KaiUiBlock
import com.inspiredandroid.kai.ui.markdown.parseMarkdown
import com.inspiredandroid.kai.ui.sandbox.SandboxTabsContent
import com.inspiredandroid.kai.ui.settings.SandboxUiState
import com.inspiredandroid.kai.ui.settings.SandboxViewModel
import kai.composeapp.generated.resources.Res
import kai.composeapp.generated.resources.fallback_answered_by
import kai.composeapp.generated.resources.fallback_service_failed
import kai.composeapp.generated.resources.fallback_trying_next
import kai.composeapp.generated.resources.ic_stop
import kai.composeapp.generated.resources.interactive_back_content_description
import kai.composeapp.generated.resources.interactive_exit_content_description
import kai.composeapp.generated.resources.interactive_title
import kai.composeapp.generated.resources.interactive_ui_parsing_failed
import kai.composeapp.generated.resources.interactive_welcome_subtitle
import kai.composeapp.generated.resources.interactive_welcome_title
import kai.composeapp.generated.resources.scroll_to_bottom_content_description
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import nl.marc_apps.tts.TextToSpeechInstance
import nl.marc_apps.tts.errors.TextToSpeechSynthesisInterruptedError
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ChatScreen(
    viewModel: ChatViewModel = koinViewModel(),
    textToSpeech: TextToSpeechInstance?,
    onNavigateToSettings: () -> Unit,
    isSandboxAvailable: Boolean = false,
    isKaiBuildAvailable: Boolean = false,
    navigationTabBar: (@Composable () -> Unit)? = null,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    ChatScreenContent(
        uiState = uiState,
        textToSpeech = textToSpeech,
        onNavigateToSettings = onNavigateToSettings,
        isSandboxAvailable = isSandboxAvailable,
        isKaiBuildAvailable = isKaiBuildAvailable,
        navigationTabBar = navigationTabBar,
    )
}


@Composable
fun ChatScreenContent(
    uiState: ChatUiState,
    textToSpeech: TextToSpeechInstance? = null,
    onNavigateToSettings: () -> Unit = {},
    isSandboxAvailable: Boolean = false,
    isKaiBuildAvailable: Boolean = false,
    navigationTabBar: (@Composable () -> Unit)? = null,
    initialSandboxOpen: Boolean = false,
    previewSandboxState: SandboxUiState? = null,
    previewSandboxLines: ImmutableList<TerminalLine> = persistentListOf(),
) {
    // Kai Build is a transient surface, not a conversation — unlike interactive
    // mode it needs no persistence beyond surviving configuration changes.
    var isKaiBuildOpen by rememberSaveable { mutableStateOf(false) }
    var drawerOpen by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerState.Closed)

    // Shared text opens a new chat; leave Kai Build so the composer is visible.
    LaunchedEffect(uiState.composerPrefill) {
        if (uiState.composerPrefill != null && isKaiBuildOpen) {
            isKaiBuildOpen = false
        }
    }

    when {
        uiState.isInteractiveMode && !uiState.isRestoring -> InteractiveModeScreen(
            uiState = uiState,
        )

        isKaiBuildOpen -> KaiBuildScreen(onExit = { isKaiBuildOpen = false })

        else -> KaiDrawerLayout(
            drawerOpen = drawerOpen,
            toggleDrawer = { drawerOpen = !drawerOpen },
            drawerContent = {
                KaiDrawerContent(
                    onNavigateToSettings = onNavigateToSettings,
                    isSandboxAvailable = isSandboxAvailable,
                    isKaiBuildAvailable = isKaiBuildAvailable,
                    onOpenKaiBuild = { isKaiBuildOpen = true }.takeIf { isKaiBuildAvailable },
                )
            },
            drawerState = drawerState,
        ) {
            ChatModeScreen(
                uiState = uiState,
                textToSpeech = textToSpeech,
                onNavigateToSettings = onNavigateToSettings,
                isSandboxAvailable = isSandboxAvailable,
                onOpenKaiBuild = { isKaiBuildOpen = true }.takeIf { isKaiBuildAvailable },
                navigationTabBar = navigationTabBar,
                initialSandboxOpen = initialSandboxOpen,
                previewSandboxState = previewSandboxState,
                previewSandboxLines = previewSandboxLines,
            )
        }
    }

    if (showHistorySheet) {
        ChatHistorySheet(
            conversations = filteredConversations,
            currentConversationId = uiState.currentConversationId,
            pendingConversationDeletion = uiState.pendingConversationDeletion,
            actions = uiState.actions,
            onDismiss = { showHistorySheet = false },
            onConversationSelected = { isSandboxOpen = false },
        )
    }
}


/** ModalNavigationDrawer wrapper. The drawer covers the full screen height
 *  (82% width) with a pinned footer containing Chat / Settings / Voice. */
@Composable
private fun KaiDrawerLayout(
    drawerOpen: Boolean,
    toggleDrawer: () -> Unit,
    drawerContent: @Composable () -> Unit,
    drawerState: DrawerState,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = Modifier.fillMaxSize(),
        drawerContent = drawerContent,
        content = {
            Box(Modifier.fillMaxSize()) {
                content()
                // Pinned footer bar (64.dp) — Chat pill, Settings avatar, Voice
                KaiDrawerFooter(
                    onChatTap = { scope.launch { drawerState.close() } },
                    onSettingsTap = { scope.launch { drawerState.close() } },
                    onVoiceTap = { scope.launch { drawerState.close() } },
                )
            }
        },
    )
}


@Composable
private fun KaiDrawerContent(
    onNavigateToSettings: () -> Unit,
    isSandboxAvailable: Boolean,
    isKaiBuildAvailable: Boolean,
    onOpenKaiBuild: (() -> Unit)?,
) {
    Column(Modifier.fillMaxSize()) {
        // Drawer header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(start = 16.dp, top = 24.dp, end = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Column {
                Text(
                    text = "Kai 9000",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Personal AI Assistant",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Divider()

        Column(modifier = Modifier.weight(1f)) {
            // Nav rows
            DrawerRow(
                icon = Icons.Default.Chat,
                label = "New Chat",
                onClick = { /* handled by footer pill */ },
            )
            DrawerRow(
                icon = Icons.Default.History,
                label = "History",
                onClick = { /* open history sheet */ },
            )
            DrawerRow(
                icon = Icons.Default.Settings,
                label = "Settings",
                onClick = onNavigateToSettings,
            )
            if (isSandboxAvailable) {
                DrawerRow(
                    icon = Icons.Default.Terminal,
                    label = "Sandbox",
                    onClick = { /* open sandbox */ },
                )
            }
            if (isKaiBuildAvailable && onOpenKaiBuild != null) {
                DrawerRow(
                    icon = Icons.Default.Build,
                    label = "Kai Build",
                    onClick = onOpenKaiBuild,
                )
            }
        }

        // Pinned section at bottom of drawer
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}


@Composable
private fun KaiDrawerFooter(
    onChatTap: () -> Unit,
    onSettingsTap: () -> Unit,
    onVoiceTap: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Chat pill — primary action
        Box(
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .background(voiceAccent(MaterialTheme.colorScheme), RoundedCornerShape(20.dp))
                .clickable { onChatTap() }
                .handCursor(),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "New chat",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "Chat",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color.White,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    ),
                )
            }
        }

        // Settings avatar (small circle)
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(if (MaterialTheme.colorScheme.isDark) Color(0xFF212121) else Color(0xFFE5E7EB), CircleShape)
                .clickable { onSettingsTap() }
                .handCursor(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(20.dp),
            )
        }

        // Voice button (morphing mic/send)
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(voiceAccent(MaterialTheme.colorScheme), CircleShape)
                .clickable { onVoiceTap() }
                .handCursor(),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice input",
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}


private fun voiceAccent(scheme: androidx.compose.material3.ColorScheme): androidx.compose.ui.graphics.Color =
    if (scheme.isDark) Color(0xFF2563EB) else Color(0xFF1D63FF)


@Composable
private fun Divider() {
    androidx.compose.foundation.Divider(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}


@Composable
private fun DrawerRow(
    icon: androidx.compose.material.icons.Icons.Default,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .handCursor()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}


/** ChatModeScreen — the main conversation surface, now wrapped in KaiDrawerLayout. */
@Composable
private fun ChatModeScreen(
    uiState: ChatUiState,
    textToSpeech: TextToSpeechInstance?,
    onNavigateToSettings: () -> Unit,
    isSandboxAvailable: Boolean,
    onOpenKaiBuild: (() -> Unit)?,
    navigationTabBar: (@Composable () -> Unit)? = null,
    initialSandboxOpen: Boolean = false,
    previewSandboxState: SandboxUiState? = null,
    previewSandboxLines: ImmutableList<TerminalLine> = persistentListOf(),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val componentScope = rememberCoroutineScope()
    var questionInputText by remember { mutableStateOf("") }
    var isSandboxOpen by rememberSaveable { mutableStateOf(initialSandboxOpen) }
    var showHistorySheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val showScrollToBottom by remember {
        derivedStateOf {
            val visibleItems = listState.visibleItemsInfo
            visibleItems.isEmpty() ||
                visibleItems.lastOrNull()?.index != listState.layoutInfo.totalItemsCount - 1
        }
    }

    // History + UI-state derived values (unchanged from original)
    val allConversations = remember(uiState.conversations) {
        uiState.conversations.toImmutableList()
    }
    val filteredConversations = remember(allConversations, uiState.currentConversationId) {
        allConversations.filter { it.id != uiState.currentConversationId }
    }

    val reasoningSegmentsByAssistantId = remember(uiState.history) {
        uiState.history
            .filter { it.role == History.Role.THINKING }
            .groupBy { it.id }
            .mapValues { (_, segments) ->
                segments.map { it.content }.toImmutableList()
            }
    }
    val suppressedThinkingIds = remember(uiState.history) {
        uiState.history
            .filter { it.role == History.Role.ASSISTANT && it.content.isNotEmpty() }
            .map { it.id }
            .toSet()
    }
    val fallbackStatusText = remember(uiState.fallbackServiceName) {
        if (uiState.fallbackServiceName != null) {
            stringResource(Res.string.fallback_answered_by, uiState.fallbackServiceName)
        } else null
    }
    val executingToolsState = rememberExecutingTools(uiState.history)
    val frozenByAssistantId = remember(uiState.frozenSubmissions) {
        uiState.frozenSubmissions.associateBy { it.assistantId }
    }

    LaunchedEffect(uiState.isRestoring) {
        if (!uiState.isRestoring) {
            componentScope.launch {
                listState.scrollToItem(0)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
            .statusBarsPadding(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart,
        ) {
            HeartbeatBanner(
                visible = uiState.showHeartbeatBanner,
                onDismiss = { /* dismiss handled in VM */ },
            )
        }

        TopBar(
            actions = uiState.actions,
            availableServices = uiState.availableServices,
            onSelectService = uiState.actions.selectService,
            onNewChat = { /* new chat */ },
            onOpenDrawer = { /* toggle drawer — handled by KaiDrawerLayout */ },
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            item(key = "empty") {
                if (uiState.history.isEmpty() && !uiState.isLoading) {
                    EmptyState(
                        onNewChat = { /* new chat */ },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    )
                }
            }

            items(
                items = uiState.history,
                key = { it.id },
            ) { history ->
                when (history.role) {
                    History.Role.USER -> {
                        UserMessage(
                            message = history.content,
                            attachments = history.attachments,
                        )
                    }
                    History.Role.ASSISTANT -> {
                        val frozen = frozenByAssistantId[history.id]
                        val isLastAssistant = history.id == uiState.history.lastOrNull()?.id
                        BotMessage(
                            message = history.content,
                            textToSpeech = textToSpeech,
                            isSpeaking = uiState.isSpeaking,
                            setIsSpeaking = { uiState.actions.setSpeaking(it) },
                            onRegenerate = if (isLastAssistant && !uiState.isLoading && frozen == null) {
                                uiState.actions.regenerate
                            } else null,
                            isInteractive = isLastAssistant && !uiState.isLoading && frozen == null,
                            onUiCallback = { event, data ->
                                uiState.actions.submitUiCallback(event, data)
                            },
                            frozen = frozen,
                            onResubmit = if (pairedUserId != null && !uiState.isLoading) {
                                { event, data -> uiState.actions.resubmit(pairedUserId, event, data) }
                            } else {
                                null
                            },
                            reasoningSegments = reasoningSegmentsByAssistantId[history.id] ?: persistentListOf(),
                        )
                        if (history.fallbackServiceName != null) {
                            androidx.compose.material3.Text(
                                text = stringResource(Res.string.fallback_answered_by, history.fallbackServiceName),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                            )
                        }
                    } else if (history.isThinking &&
                        history.content.isNotEmpty() &&
                        history.id !in suppressedThinkingIds
                    ) {
                        // Thinking-only turn still in flight — render as a standalone
                        // reasoning bubble. The precomputation above has already gathered
                        // every earlier thinking segment in this cycle under this id.
                        BotMessage(
                            message = "",
                            textToSpeech = null,
                            isSpeaking = false,
                            setIsSpeaking = {},
                            reasoningSegments = reasoningSegmentsByAssistantId[history.id]
                                ?: persistentListOf(history.content),
                        )
                    }
                    History.Role.TOOL_EXECUTING -> {
                        // Rendered in WaitingResponseRow below
                    }
                    History.Role.TOOL -> {
                        // Don't show completed tool results in UI
                    }
                }
            }

            // Skip the generic "thinking" row during a pending kai-ui submission — the
            // pressed button's pulse already signals work in flight. Keep it for tool
            // activity so tool feedback isn't lost.
            val showWaitingRow = uiState.isLoading &&
                (frozenByAssistantId.values.none { it.isPending } || executingToolsState.tools.isNotEmpty())
            if (showWaitingRow) {
                item(key = "loading") {
                    WaitingResponseRow(
                        executingTools = executingToolsState.tools,
                        isStatusOnly = executingToolsState.isStatusOnly,
                        statusText = fallbackStatusText,
                    )
                }
            }
            uiState.error?.let { error ->
                item(key = "error") {
                    if (uiState.showFreeProviderSuggestions) {
                        FreeProviderSuggestionsPanel(
                            error = error,
                            retry = uiState.actions.retry,
                        )
                    } else {
                        ErrorMessage(error = error, retry = uiState.actions.retry)
                    }
                }
            }
        }

        VerticalScrollbarForList(
            listState = listState,
            modifier = Modifier.align(CenterEnd).fillMaxHeight(),
        )

        androidx.compose.animation.AnimatedVisibility(
            visible = showScrollToBottom,
            modifier = Modifier.align(BottomCenter).padding(bottom = 8.dp),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            SmallFloatingActionButton(
                modifier = Modifier
                    .handCursor(),
                onClick = {
                    componentScope.launch {
                        val totalItems = listState.layoutInfo.totalItemsCount
                        if (totalItems > 0) {
                            listState.animateScrollToItem(totalItems - 1)
                        }
                    }
                },
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = stringResource(Res.string.scroll_to_bottom_content_description))
            }
        }

        if (!isSandboxOpen) {
            QuestionInput(
                files = uiState.files,
                addFile = uiState.actions.addFile,
                removeFile = uiState.actions.removeFile,
                ask = uiState.actions.ask,
                supportedFileExtensions = uiState.supportedFileExtensions,
                textState = questionInputText,
                onTextStateChange = { questionInputText = it },
                isLoading = uiState.isLoading,
                cancel = uiState.actions.cancel,
                availableServices = uiState.availableServices,
                onSelectService = uiState.actions.selectService,
                installedSkills = uiState.installedSkills,
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(BottomCenter).padding(bottom = 80.dp),
        ) { data ->
            Snackbar(snackbarData = data)
        }
    }
}


private data class ExecutingToolsState(
    val tools: ImmutableList<Pair<String, String>>,
    val isStatusOnly: Boolean,
)


@Composable
private fun rememberExecutingTools(history: ImmutableList<History>): ExecutingToolsState {
    // Wrap the history parameter in State so derivedStateOf can observe it, then
    // only recompute (and only emit) when the executing-tools subset actually changes.
    // Streaming tokens mutate `history` on every frame but rarely change this derived slice.
    val historyState = rememberUpdatedState(history)
    val state by remember {
        derivedStateOf {
            val executing = historyState.value.filter { it.role == History.Role.TOOL_EXECUTING }
            ExecutingToolsState(
                tools = executing.map { it.id to (it.toolName ?: "tool") }.toImmutableList(),
                isStatusOnly = executing.any { it.isStatusMessage },
            )
        }
    }
    return state
}

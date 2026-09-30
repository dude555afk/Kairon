package com.inspiredandroid.kai.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kai.composeapp.generated.resources.Res
import kai.composeapp.generated.resources.settings_tab_agent
import kai.composeapp.generated.resources.settings_tab_general
import kai.composeapp.generated.resources.settings_tab_integrations
import kai.composeapp.generated.resources.settings_tab_sandbox
import kai.composeapp.generated.resources.settings_tab_services
import kai.composeapp.generated.resources.settings_tab_tools
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.StringResource

internal fun settingsTabTitle(tab: SettingsTab): StringResource = when (tab) {
    SettingsTab.General -> Res.string.settings_tab_general
    SettingsTab.Agent -> Res.string.settings_tab_agent
    SettingsTab.Services -> Res.string.settings_tab_services
    SettingsTab.Tools -> Res.string.settings_tab_tools
    SettingsTab.Integrations -> Res.string.settings_tab_integrations
    SettingsTab.Sandbox -> Res.string.settings_tab_sandbox
}

private data class KelivoSettingsDestination(
    val tab: SettingsTab,
    val title: String,
    val description: String,
    val icon: ImageVector,
)

private val kelivoSettingsDestinations = listOf(
    KelivoSettingsDestination(
        tab = SettingsTab.General,
        title = "Appearance & behavior",
        description = "Theme, chat style, layout, display and app behavior",
        icon = Icons.Default.Palette,
    ),
    KelivoSettingsDestination(
        tab = SettingsTab.Agent,
        title = "Assistants & memory",
        description = "Assistant identity, instructions, memory and proactive behavior",
        icon = Icons.Default.SmartToy,
    ),
    KelivoSettingsDestination(
        tab = SettingsTab.Services,
        title = "Providers & models",
        description = "AI providers, models, reasoning, API endpoints and routing",
        icon = Icons.Default.Memory,
    ),
    KelivoSettingsDestination(
        tab = SettingsTab.Tools,
        title = "Tools & skills",
        description = "Local tools, MCP servers, skills and agent capabilities",
        icon = Icons.Default.Extension,
    ),
    KelivoSettingsDestination(
        tab = SettingsTab.Integrations,
        title = "Integrations",
        description = "GitHub and connected services",
        icon = Icons.Default.Link,
    ),
    KelivoSettingsDestination(
        tab = SettingsTab.Sandbox,
        title = "Workspace & sandbox",
        description = "Linux workspace, packages, terminal and Kai Build",
        icon = Icons.Default.Terminal,
    ),
)

@Composable
internal fun SettingsOverview(
    tabs: ImmutableList<SettingsTab>,
    onOpen: (SettingsTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    val visible = remember(tabs, query) {
        val q = query.trim().lowercase()
        kelivoSettingsDestinations.filter { destination ->
            destination.tab in tabs &&
                (
                    q.isEmpty() ||
                        destination.title.lowercase().contains(q) ||
                        destination.description.lowercase().contains(q)
                    )
        }
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Search settings") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                focusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f),
            ),
        )

        SettingsSection(
            title = "Personalization",
            destinations = visible.filter { it.tab in listOf(SettingsTab.General, SettingsTab.Agent) },
            onOpen = onOpen,
        )
        SettingsSection(
            title = "AI & capabilities",
            destinations = visible.filter {
                it.tab in listOf(SettingsTab.Services, SettingsTab.Tools, SettingsTab.Integrations)
            },
            onOpen = onOpen,
        )
        SettingsSection(
            title = "Workspace",
            destinations = visible.filter { it.tab == SettingsTab.Sandbox },
            onOpen = onOpen,
        )

        if (visible.isEmpty()) {
            Text(
                text = "No settings found",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 18.dp),
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    destinations: List<KelivoSettingsDestination>,
    onOpen: (SettingsTab) -> Unit,
) {
    if (destinations.isEmpty()) return

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp),
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
            border = BorderStroke(
                0.6.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
            ),
        ) {
            destinations.forEachIndexed { index, destination ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 58.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.38f),
                    )
                }
                SettingsDestinationRow(destination = destination, onOpen = onOpen)
            }
        }
    }
}

@Composable
private fun SettingsDestinationRow(
    destination: KelivoSettingsDestination,
    onOpen: (SettingsTab) -> Unit,
) {
    Surface(
        onClick = { onOpen(destination.tab) },
        color = androidx.compose.ui.graphics.Color.Transparent,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = RoundedCornerShape(11.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.size(34.dp),
            ) {
                androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = destination.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = destination.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

package com.inspiredandroid.kai.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kai.composeapp.generated.resources.Res
import kai.composeapp.generated.resources.kairon_settings_agent_desc
import kai.composeapp.generated.resources.kairon_settings_capabilities
import kai.composeapp.generated.resources.kairon_settings_general_desc
import kai.composeapp.generated.resources.kairon_settings_integrations_desc
import kai.composeapp.generated.resources.kairon_settings_intro
import kai.composeapp.generated.resources.kairon_settings_sandbox_desc
import kai.composeapp.generated.resources.kairon_settings_services_desc
import kai.composeapp.generated.resources.kairon_settings_tools_desc
import kai.composeapp.generated.resources.kairon_settings_workspace
import kai.composeapp.generated.resources.settings_tab_agent
import kai.composeapp.generated.resources.settings_tab_general
import kai.composeapp.generated.resources.settings_tab_integrations
import kai.composeapp.generated.resources.settings_tab_sandbox
import kai.composeapp.generated.resources.settings_tab_services
import kai.composeapp.generated.resources.settings_tab_tools
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal fun settingsTabTitle(tab: SettingsTab): StringResource = when (tab) {
    SettingsTab.General -> Res.string.settings_tab_general
    SettingsTab.Agent -> Res.string.settings_tab_agent
    SettingsTab.Services -> Res.string.settings_tab_services
    SettingsTab.Tools -> Res.string.settings_tab_tools
    SettingsTab.Integrations -> Res.string.settings_tab_integrations
    SettingsTab.Sandbox -> Res.string.settings_tab_sandbox
}

private fun settingsTabDescription(tab: SettingsTab): StringResource = when (tab) {
    SettingsTab.General -> Res.string.kairon_settings_general_desc
    SettingsTab.Agent -> Res.string.kairon_settings_agent_desc
    SettingsTab.Services -> Res.string.kairon_settings_services_desc
    SettingsTab.Tools -> Res.string.kairon_settings_tools_desc
    SettingsTab.Integrations -> Res.string.kairon_settings_integrations_desc
    SettingsTab.Sandbox -> Res.string.kairon_settings_sandbox_desc
}

/** Groups the existing screens into destinations without duplicating their controls or state. */
@Composable
internal fun SettingsOverview(
    tabs: ImmutableList<SettingsTab>,
    onOpen: (SettingsTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Text(
            text = stringResource(Res.string.kairon_settings_intro),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
        )
        SettingsGroup(
            title = stringResource(Res.string.kairon_settings_workspace),
            tabs = listOf(SettingsTab.General, SettingsTab.Agent).filter { it in tabs },
            onOpen = onOpen,
        )
        SettingsGroup(
            title = stringResource(Res.string.kairon_settings_capabilities),
            tabs = listOf(SettingsTab.Services, SettingsTab.Tools, SettingsTab.Integrations, SettingsTab.Sandbox)
                .filter { it in tabs },
            onOpen = onOpen,
        )
    }
}

@Composable
private fun SettingsGroup(title: String, tabs: List<SettingsTab>, onOpen: (SettingsTab) -> Unit) {
    if (tabs.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
            shape = MaterialTheme.shapes.medium,
            border = androidx.compose.foundation.BorderStroke(
                0.6.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f),
            ),
        ) {
            tabs.forEachIndexed { index, tab ->
                if (index > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                    )
                }
                SettingsDestination(tab, onOpen)
            }
        }
    }
}

@Composable
private fun SettingsDestination(tab: SettingsTab, onOpen: (SettingsTab) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        androidx.compose.material3.Surface(
            onClick = { onOpen(tab) },
            modifier = Modifier.fillMaxWidth(),
            color = androidx.compose.ui.graphics.Color.Transparent,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = stringResource(settingsTabTitle(tab)),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = stringResource(settingsTabDescription(tab)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

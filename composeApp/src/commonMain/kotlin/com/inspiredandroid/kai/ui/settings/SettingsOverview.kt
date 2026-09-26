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

/**
 * First-level settings index. The existing category contents and their actions remain
 * untouched; users enter a category rather than confronting every configuration at once.
 */
@Composable
internal fun SettingsOverview(
    tabs: ImmutableList<SettingsTab>,
    onOpen: (SettingsTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(Res.string.kairon_settings_intro),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        SettingsGroupTitle(stringResource(Res.string.kairon_settings_workspace))
        listOf(SettingsTab.General, SettingsTab.Agent).filter { it in tabs }.forEach { tab ->
            SettingsDestination(tab, onOpen)
        }
        SettingsGroupTitle(stringResource(Res.string.kairon_settings_capabilities))
        listOf(SettingsTab.Services, SettingsTab.Tools, SettingsTab.Integrations, SettingsTab.Sandbox)
            .filter { it in tabs }
            .forEach { tab -> SettingsDestination(tab, onOpen) }
    }
}

@Composable
private fun SettingsGroupTitle(value: String) {
    Text(
        text = value,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp, top = 12.dp, bottom = 2.dp),
    )
}

@Composable
private fun SettingsDestination(tab: SettingsTab, onOpen: (SettingsTab) -> Unit) {
    Card(
        onClick = { onOpen(tab) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(settingsTabTitle(tab)),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = stringResource(settingsTabDescription(tab)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(8.dp))
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

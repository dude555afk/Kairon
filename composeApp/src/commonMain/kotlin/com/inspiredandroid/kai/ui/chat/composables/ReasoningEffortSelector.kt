package com.inspiredandroid.kai.ui.chat.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.kai.data.ReasoningEffort
import kai.composeapp.generated.resources.Res
import kai.composeapp.generated.resources.kairon_effort_auto
import kai.composeapp.generated.resources.kairon_effort_high
import kai.composeapp.generated.resources.kairon_effort_low
import kai.composeapp.generated.resources.kairon_effort_max
import kai.composeapp.generated.resources.kairon_effort_medium
import kai.composeapp.generated.resources.kairon_effort_xhigh
import kai.composeapp.generated.resources.kairon_reasoning_effort
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * Always visible beneath the composer, rather than hidden behind a provider-gated dropdown.
 * Unsupported models show a disabled control so the UI does not imply the API honors effort.
 */
@Composable
internal fun ReasoningEffortSelector(
    levels: List<ReasoningEffort>,
    selected: ReasoningEffort,
    onSelect: (ReasoningEffort) -> Unit,
) {
    val supported = levels.size >= 2
    val visibleLevels = if (supported) levels else ReasoningEffort.entries
    val safeSelected = selected.takeIf { supported && it in levels } ?: ReasoningEffort.AUTO
    Column(
        modifier = Modifier.fillMaxWidth().padding(start = 18.dp, end = 18.dp, bottom = 7.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(Res.string.kairon_reasoning_effort),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = " · " + if (supported) effortLabel(safeSelected) else "Unavailable for this model",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Slider(
            value = visibleLevels.indexOf(safeSelected).coerceAtLeast(0).toFloat(),
            onValueChange = { position ->
                if (supported) onSelect(visibleLevels[position.roundToInt().coerceIn(0, visibleLevels.lastIndex)])
            },
            enabled = supported,
            valueRange = 0f..visibleLevels.lastIndex.toFloat(),
            steps = (visibleLevels.size - 2).coerceAtLeast(0),
            modifier = Modifier.fillMaxWidth(),
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Auto", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
            Text(if (supported) effortLabel(visibleLevels.last()) else "Not supported by provider",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun effortLabel(value: ReasoningEffort): String = stringResource(
    when (value) {
        ReasoningEffort.AUTO -> Res.string.kairon_effort_auto
        ReasoningEffort.LOW -> Res.string.kairon_effort_low
        ReasoningEffort.MEDIUM -> Res.string.kairon_effort_medium
        ReasoningEffort.HIGH -> Res.string.kairon_effort_high
        ReasoningEffort.XHIGH -> Res.string.kairon_effort_xhigh
        ReasoningEffort.MAX -> Res.string.kairon_effort_max
    },
)

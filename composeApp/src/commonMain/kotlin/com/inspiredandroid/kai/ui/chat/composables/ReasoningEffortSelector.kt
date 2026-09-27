package com.inspiredandroid.kai.ui.chat.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.kai.data.ReasoningEffort
import kai.composeapp.generated.resources.Res
import kai.composeapp.generated.resources.kairon_reasoning_effort
import kai.composeapp.generated.resources.kairon_effort_auto
import kai.composeapp.generated.resources.kairon_effort_low
import kai.composeapp.generated.resources.kairon_effort_medium
import kai.composeapp.generated.resources.kairon_effort_high
import kai.composeapp.generated.resources.kairon_effort_xhigh
import kai.composeapp.generated.resources.kairon_effort_max
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/** Only rendered for a service/model pair on the explicit capability allowlist. */
@Composable
internal fun ReasoningEffortSelector(
    levels: List<ReasoningEffort>,
    selected: ReasoningEffort,
    onSelect: (ReasoningEffort) -> Unit,
) {
    if (levels.isEmpty()) return
    var expanded by remember { mutableStateOf(false) }
    val safeSelected = selected.takeIf { it in levels } ?: ReasoningEffort.AUTO
    val currentLabel = effortLabel(safeSelected)
    androidx.compose.foundation.layout.Box {
        Surface(
            onClick = { expanded = true },
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
        ) {
            Text(
                text = currentLabel,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Column(Modifier.width(240.dp).padding(horizontal = 14.dp, vertical = 10.dp)) {
                Text(
                    text = stringResource(Res.string.kairon_reasoning_effort),
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(currentLabel, style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = levels.indexOf(safeSelected).coerceAtLeast(0).toFloat(),
                    onValueChange = { position ->
                        onSelect(levels[position.roundToInt().coerceIn(0, levels.lastIndex)])
                    },
                    valueRange = 0f..levels.lastIndex.toFloat(),
                    steps = (levels.size - 2).coerceAtLeast(0),
                )
            }
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

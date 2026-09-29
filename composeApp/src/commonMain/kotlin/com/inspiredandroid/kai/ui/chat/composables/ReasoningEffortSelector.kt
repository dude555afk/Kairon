package com.inspiredandroid.kai.ui.chat.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

/** Compact composer control: expand the slider only when the user requests it. */
@Composable
internal fun ReasoningEffortSelector(
    levels: List<ReasoningEffort>,
    selected: ReasoningEffort,
    onSelect: (ReasoningEffort) -> Unit,
) {
    val supported = levels.size >= 2
    val safeSelected = selected.takeIf { it in levels } ?: ReasoningEffort.AUTO
    var expanded by remember { mutableStateOf(false) }
    androidx.compose.foundation.layout.Box {
        Surface(
            onClick = { expanded = true },
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHighest,
        ) {
            Text(
                text = if (supported) "Effort · ${effortLabel(safeSelected)}" else "Effort · Unavailable",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Column(Modifier.width(260.dp).padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text(stringResource(Res.string.kairon_reasoning_effort), style = MaterialTheme.typography.titleSmall)
                if (supported) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Auto", style = MaterialTheme.typography.labelSmall)
                        Spacer(Modifier.weight(1f))
                        Text(effortLabel(safeSelected), style = MaterialTheme.typography.labelMedium)
                    }
                    Slider(
                        value = levels.indexOf(safeSelected).coerceAtLeast(0).toFloat(),
                        onValueChange = { position ->
                            onSelect(levels[position.roundToInt().coerceIn(0, levels.lastIndex)])
                        },
                        valueRange = 0f..levels.lastIndex.toFloat(),
                        steps = (levels.size - 2).coerceAtLeast(0),
                    )
                    Text(levels.joinToString(" · ") { it.name.lowercase().replaceFirstChar { c -> c.uppercaseChar() } },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Text(
                        "This model/provider has no verified effort control. Its normal reasoning behavior remains available, but Kairon will not send an unsupported effort setting.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
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

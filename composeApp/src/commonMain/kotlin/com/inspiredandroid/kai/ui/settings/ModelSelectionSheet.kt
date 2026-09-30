package com.inspiredandroid.kai.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.inspiredandroid.kai.data.ModelSpecResolver
import com.inspiredandroid.kai.formatContextWindow
import com.inspiredandroid.kai.formatReleaseDate
import com.inspiredandroid.kai.ui.KaiOutlinedTextField
import com.inspiredandroid.kai.ui.components.KaiSearchField
import com.inspiredandroid.kai.ui.components.VerticalScrollbarForList
import com.inspiredandroid.kai.ui.handCursor
import kai.composeapp.generated.resources.Res
import kai.composeapp.generated.resources.ic_arrow_drop_down
import kai.composeapp.generated.resources.model_filter_free
import kai.composeapp.generated.resources.model_free_badge
import kai.composeapp.generated.resources.model_free_empty
import kai.composeapp.generated.resources.model_sort_context
import kai.composeapp.generated.resources.model_sort_date
import kai.composeapp.generated.resources.model_sort_score
import kai.composeapp.generated.resources.settings_model_label
import kai.composeapp.generated.resources.settings_model_search
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ModelSelection(
    currentSelectedModel: SettingsModel?,
    models: ImmutableList<SettingsModel>,
    onClick: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val pickerModels = remember(models) { models.filter { !it.isManualEntry } }
    if (pickerModels.isEmpty()) return

    Box(modifier = Modifier.fillMaxWidth()) {
        KaiOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = currentSelectedModel?.let { it.displayName ?: it.id } ?: "",
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    stringResource(Res.string.settings_model_label),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier.handCursor(),
                    imageVector = vectorResource(Res.drawable.ic_arrow_drop_down),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
        )
        Box(
            modifier = Modifier.matchParentSize().handCursor().clickable { expanded = true },
        )
    }

    if (!expanded) return

    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = { expanded = false },
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
    ) {
        var searchQuery by remember { mutableStateOf("") }
        val hasFreeModels = remember(pickerModels) { pickerModels.any { it.isFreeTier } }
        var freeFilterOnly by remember { mutableStateOf(false) }
        var sortOption by remember { mutableStateOf(ModelSortOption.Score) }

        LaunchedEffect(hasFreeModels) {
            if (!hasFreeModels) freeFilterOnly = false
        }

        val filteredModels = remember(pickerModels, searchQuery, freeFilterOnly, sortOption) {
            pickerModels
                .filter { model ->
                    val matchesFree = !freeFilterOnly || model.isFreeTier
                    val matchesSearch = searchQuery.isBlank() ||
                        model.id.contains(searchQuery, ignoreCase = true) ||
                        model.subtitle.contains(searchQuery, ignoreCase = true) ||
                        model.displayName?.contains(searchQuery, ignoreCase = true) == true
                    matchesFree && matchesSearch
                }
                .sortedWith(sortOption.comparator)
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Select model",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            )
            KaiSearchField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = stringResource(Res.string.settings_model_search),
            )
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ModelSortOption.entries.forEach { option ->
                    FilterChip(
                        selected = sortOption == option,
                        onClick = { sortOption = option },
                        label = { Text(stringResource(option.labelRes)) },
                        modifier = Modifier.handCursor(),
                    )
                }
                if (hasFreeModels) {
                    FilterChip(
                        selected = freeFilterOnly,
                        onClick = { freeFilterOnly = !freeFilterOnly },
                        label = { Text(stringResource(Res.string.model_filter_free)) },
                        modifier = Modifier.handCursor(),
                    )
                }
            }

            if (filteredModels.isEmpty()) {
                Text(
                    text = stringResource(Res.string.model_free_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(24.dp),
                )
            } else {
                val listState = rememberLazyListState()
                Box(modifier = Modifier.fillMaxHeight(0.74f)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    ) {
                        items(filteredModels, key = { it.id }) { model ->
                            ModelRow(
                                model = model,
                                isSelected = currentSelectedModel?.id == model.id,
                                onClick = {
                                    onClick(model.id)
                                    expanded = false
                                },
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(start = 12.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.32f),
                            )
                        }
                    }
                    VerticalScrollbarForList(
                        listState = listState,
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    )
                }
            }
        }
    }
}

private enum class ModelSortOption(
    val labelRes: StringResource,
    val comparator: Comparator<SettingsModel>,
) {
    Date(Res.string.model_sort_date, compareByDescending<SettingsModel> { it.releaseDate }.thenBy { it.id }),
    Score(Res.string.model_sort_score, compareByDescending<SettingsModel> { it.arenaScore }.thenBy { it.id }),
    Ctx(Res.string.model_sort_context, compareByDescending<SettingsModel> { it.contextWindow }.thenBy { it.id }),
}

@Composable
private fun ModelRow(
    model: SettingsModel,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val displayName = model.displayName?.takeIf { it.isNotBlank() && it != model.id }
    val title = displayName ?: model.id
    val contextText = model.contextWindow?.let { formatContextWindow(it) }
    val releaseText = model.releaseDate?.let { formatReleaseDate(it) }
    val detailText = listOfNotNull(releaseText, model.parameterCount, contextText)
        .joinToString(" · ")
        .ifEmpty { null }
    val modelSpec = remember(model.id) { ModelSpecResolver.resolve("", model.id) }

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().handCursor(),
        color = if (isSelected) {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f)
        } else {
            Color.Transparent
        },
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f),
                    )
                    if (model.isFreeTier) {
                        Spacer(Modifier.width(6.dp))
                        Badge(text = stringResource(Res.string.model_free_badge))
                    }
                    if (modelSpec.supportsReasoning) {
                        Spacer(Modifier.width(6.dp))
                        Badge(text = "Reasoning")
                    }
                }
                if (displayName != null && model.id != displayName) {
                    Text(
                        text = model.id,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else if (model.subtitle.isNotBlank()) {
                    Text(
                        text = model.subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                detailText?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.78f),
                    )
                }
            }
            if (isSelected) {
                Spacer(Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun Badge(text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
        )
    }
}

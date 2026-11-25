@file:Suppress("DEPRECATION")


package com.inspiredandroid.kai.ui


import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.background

import androidx.compose.foundation.border

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Clear

import androidx.compose.material3.CardColors

import androidx.compose.material3.CardDefaults

import androidx.compose.material3.ColorScheme

import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.OutlinedTextField

import androidx.compose.material3.OutlinedTextFieldDefaults

import androidx.compose.material3.darkColorScheme

import androidx.compose.material3.lightColorScheme

import androidx.compose.runtime.Composable

import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.remember

import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.alpha

import androidx.compose.ui.draw.clip

import androidx.compose.ui.focus.onFocusChanged

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.graphics.Shape

import androidx.compose.ui.input.pointer.PointerIcon

import androidx.compose.ui.input.pointer.pointerHoverIcon

import androidx.compose.ui.text.input.VisualTransformation

import androidx.compose.ui.unit.dp

import org.jetbrains.compose.ui.tooling.preview.Preview


val voiceAccentLight = Color(0xFF1D63FF)
val voiceAccentDark = Color(0xFF2563EB)

val drawerScrim = Color(0xFF000000).copy(alpha = 0.32f)

fun Modifier.handCursor() = pointerHoverIcon(PointerIcon.Hand, overrideDescendants = true)


val DarkColorScheme = darkColorScheme(
    primary = voiceAccentDark,
    onPrimary = Color(0xFFFFFFFF),
    surface = Color(0xFF171717),
    background = Color(0xFF000000),
    onBackground = Color(0xFFECECF1),
    onSurface = Color(0xFFECECF1),
    surfaceVariant = Color(0xFF2F2F2F),
    outlineVariant = Color(0xFF2F2F2F),
)


fun ColorScheme.withBlackBackground(): ColorScheme = copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceContainerLowest = Color.Black,
)


val ColorScheme.isOledFlavor: Boolean get() = background == Color.Black


@Composable

fun kaiAdaptiveCardColors(): CardColors = CardDefaults.cardColors(
    containerColor = if (MaterialTheme.colorScheme.isOledFlavor) {
        Color.Transparent
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    },
)


@Composable

fun kaiAdaptiveCardBorder(): BorderStroke? = if
(MaterialTheme.colorScheme.isOledFlavor) {
    BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
} else {
    null
}


@Composable

fun Modifier.kaiAdaptiveCardSurface(shape: Shape = CardDefaults.shape):
Modifier = this
    .clip(shape)
    .background(
        if (MaterialTheme.colorScheme.isOledFlavor) {
            Color.Transparent
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        },
    )
    .then(
        if (MaterialTheme.colorScheme.isOledFlavor) {
            Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
        } else {
            Modifier
        },
    )


val LightColorScheme = lightColorScheme(
    primary = voiceAccentLight,
    onPrimary = Color(0xFFFFFFFF),
    surface = Color(0xFFF4F4F6),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF0D0D0D),
    onSurface = Color(0xFF0D0D0D),
    surfaceVariant = Color(0xFFE5E7EB),
    outlineVariant = Color(0xFFE5E7EB),
)


@Composable

fun outlineTextFieldColors() = OutlinedTextFieldDefaults.colors()


@Composable

fun KaiOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        label = label,
        placeholder = placeholder,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        shape = RoundedCornerShape(12.dp),
        colors = outlineTextFieldColors(),
    )
}


@Composable

fun KaiClearableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
) {
    var focused by remember { mutableStateOf(false) }
    KaiOutlinedTextField(
        modifier = modifier.fillMaxWidth().onFocusChanged { focused = it.isFocused },
        value = value,
        onValueChange = onValueChange,
        label = label,
        singleLine = singleLine,
        trailingIcon = {
            IconButton(
                onClick = { onValueChange("") },
                modifier = Modifier.handCursor()
                    .alpha(if (focused && value.isNotEmpty()) 1f else 0f),
                enabled = value.isNotEmpty(),
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}


@Composable

@Preview

fun Theme(
    colorScheme: ColorScheme,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme,
    ) {
        content()
    }
}

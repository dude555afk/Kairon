@file:Suppress("DEPRECATION")

package com.inspiredandroid.kai.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Shapes
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.inspiredandroid.kai.data.AccentPreset
import org.jetbrains.compose.ui.tooling.preview.Preview

val darkPurple = Color(0xFF6200EE)
val lightPurple = Color(0xff8063C5)
val gradientBrush = androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(darkPurple, lightPurple))

// Animated border gradient colors
val gradientPurple = Color(0xFF9C27B0)
val gradientViolet = Color(0xFF7C4DFF)
val gradientMagenta = Color(0xFFE040FB)

fun Modifier.handCursor() = pointerHoverIcon(PointerIcon.Hand, overrideDescendants = true)

// Calm, layered surfaces inspired by Kelivo's visual philosophy, built in Compose.
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFB7C6FF),
    onPrimary = Color(0xFF17234A),
    primaryContainer = Color(0xFF303D61),
    onPrimaryContainer = Color(0xFFE1E7FF),
    secondary = Color(0xFFA8C9C5),
    onSecondary = Color(0xFF15312E),
    background = Color(0xFF101216),
    onBackground = Color(0xFFE9EAEE),
    surface = Color(0xFF13161B),
    onSurface = Color(0xFFE9EAEE),
    onSurfaceVariant = Color(0xFFB5B8C2),
    surfaceContainerLowest = Color(0xFF0D0F13),
    surfaceContainerLow = Color(0xFF191C22),
    surfaceContainer = Color(0xFF20242B),
    surfaceContainerHigh = Color(0xFF292D35),
    surfaceContainerHighest = Color(0xFF343841),
    outlineVariant = Color(0xFF414650),
)

fun ColorScheme.withAccent(preset: AccentPreset): ColorScheme {
    val accent = when (preset) {
        AccentPreset.Default -> return this
        AccentPreset.Graphite -> Color(0xFF9BA0AA)
        AccentPreset.Lavender -> Color(0xFFB4A7F5)
        AccentPreset.Rose -> Color(0xFFF38BA8)
        AccentPreset.Teal -> Color(0xFF69D5C4)
        AccentPreset.CatppuccinMocha -> Color(0xFFCBA6F7)
    }
    // Apply the accent consistently to the app canvas, raised surfaces, controls,
    // and outlines while preserving pure black on OLED.
    val oled = background == Color.Black
    return copy(
        primary = accent,
        onPrimary = Color(0xFF171717),
        primaryContainer = lerp(surfaceContainerHigh, accent, 0.26f),
        onPrimaryContainer = onSurface,
        secondary = accent,
        onSecondary = Color(0xFF171717),
        secondaryContainer = lerp(surfaceContainerHigh, accent, 0.16f),
        onSecondaryContainer = onSurface,
        tertiary = accent,
        onTertiary = Color(0xFF171717),
        tertiaryContainer = lerp(surfaceContainerHigh, accent, 0.16f),
        onTertiaryContainer = onSurface,
        surfaceTint = accent,
        inversePrimary = accent,
        background = if (oled) Color.Black else lerp(background, accent, 0.025f),
        surface = if (oled) Color.Black else lerp(surface, accent, 0.035f),
        surfaceContainerLowest = if (oled) Color.Black else lerp(surfaceContainerLowest, accent, 0.025f),
        surfaceContainerLow = lerp(surfaceContainerLow, accent, 0.055f),
        surfaceContainer = lerp(surfaceContainer, accent, 0.08f),
        surfaceContainerHigh = lerp(surfaceContainerHigh, accent, 0.12f),
        surfaceContainerHighest = lerp(surfaceContainerHighest, accent, 0.17f),
        surfaceVariant = lerp(surfaceVariant, accent, 0.12f),
        outline = lerp(outline, accent, 0.22f),
        outlineVariant = lerp(outlineVariant, accent, 0.24f),
    )
}

fun ColorScheme.withBlackBackground(): ColorScheme = copy(
    background = Color.Black,
    surface = Color.Black,
    surfaceContainerLowest = Color.Black,
)

val ColorScheme.isOledFlavor: Boolean get() = background == Color.Black

// One filled, theme-derived surface language for all settings and workspace cards.
// Even OLED keeps readable raised surfaces instead of a grid of outlined boxes.
@Composable
fun kaiAdaptiveCardColors(): CardColors = CardDefaults.cardColors(
    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
)

@Composable
fun kaiAdaptiveCardBorder(): BorderStroke? = BorderStroke(
    0.6.dp,
    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.60f),
)

@Composable
fun Modifier.kaiAdaptiveCardSurface(shape: Shape = CardDefaults.shape): Modifier = this
    .clip(shape)
    .background(MaterialTheme.colorScheme.surfaceContainerLow)

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4B609E),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCE5FF),
    onPrimaryContainer = Color(0xFF20355F),
    secondary = Color(0xFF426D69),
    onSecondary = Color.White,
    background = Color(0xFFF6F7F9),
    onBackground = Color(0xFF202329),
    surface = Color(0xFFF6F7F9),
    onSurface = Color(0xFF202329),
    onSurfaceVariant = Color(0xFF626873),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFFFFFF),
    surfaceContainer = Color(0xFFF0F2F5),
    surfaceContainerHigh = Color(0xFFE9ECF1),
    surfaceContainerHighest = Color(0xFFDFE4EA),
    outlineVariant = Color(0xFFD0D5DC),
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
        shape = RoundedCornerShape(13.dp),
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

private val KaironShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
@Preview
fun Theme(
    colorScheme: ColorScheme,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme,
        shapes = KaironShapes,
    ) {
        content()
    }
}

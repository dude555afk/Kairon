package com.inspiredandroid.kai.data

/**
 * Persistent chat presentation preferences. Rendering code consumes this model instead
 * of owning hard-coded bubble geometry, which lets the chat shell evolve without
 * touching Dynamic UI, message history, tools, or conversation semantics.
 */
enum class ChatMessageLayout {
    BUBBLES,
    FLAT,
}

enum class ChatSurfaceStyle {
    SOLID,
    TRANSLUCENT,
}

data class ChatAppearance(
    val messageLayout: ChatMessageLayout = ChatMessageLayout.BUBBLES,
    val surfaceStyle: ChatSurfaceStyle = ChatSurfaceStyle.TRANSLUCENT,
    val bubbleRadiusDp: Float = 18f,
    val bubbleOpacity: Float = 0.88f,
    val borderWidthDp: Float = 0.6f,
    val compactSpacing: Boolean = false,
    val composerRadiusDp: Float = 24f,
) {
    fun normalized(): ChatAppearance = copy(
        bubbleRadiusDp = bubbleRadiusDp.coerceIn(6f, 32f),
        bubbleOpacity = bubbleOpacity.coerceIn(0.35f, 1f),
        borderWidthDp = borderWidthDp.coerceIn(0f, 2f),
        composerRadiusDp = composerRadiusDp.coerceIn(12f, 32f),
    )
}

package com.inspiredandroid.kai.data

import kotlin.test.Test
import kotlin.test.assertEquals

class ChatAppearanceTest {
    @Test
    fun `appearance values are clamped before persistence or rendering`() {
        val normalized = ChatAppearance(
            bubbleRadiusDp = 100f,
            bubbleOpacity = 0.1f,
            borderWidthDp = -2f,
            composerRadiusDp = 2f,
        ).normalized()

        assertEquals(32f, normalized.bubbleRadiusDp)
        assertEquals(0.35f, normalized.bubbleOpacity)
        assertEquals(0f, normalized.borderWidthDp)
        assertEquals(12f, normalized.composerRadiusDp)
    }
}

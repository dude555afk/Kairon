package com.inspiredandroid.kai.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReasoningEffortTest {
    @Test
    fun `free routes and unknown models never expose an effort control`() {
        assertTrue(supportedReasoningEfforts(Service.Free.id, "kilo-auto/free").isEmpty())
        assertTrue(supportedReasoningEfforts(Service.OpenRouter.id, "openrouter/free").isEmpty())
        assertTrue(supportedReasoningEfforts(Service.OpenAI.id, "gpt-4o").isEmpty())
        assertTrue(supportedReasoningEfforts(Service.OpenAICompatible.id, "gpt-5.6").isEmpty())
    }

    @Test
    fun `verified OpenAI Sol routes offer exactly the levels the wire accepts`() {
        val levels = supportedReasoningEfforts(Service.OpenAI.id, "gpt-5.6-sol")
        assertEquals(
            listOf(
                ReasoningEffort.AUTO, ReasoningEffort.LOW, ReasoningEffort.MEDIUM,
                ReasoningEffort.HIGH, ReasoningEffort.XHIGH, ReasoningEffort.MAX,
            ),
            levels,
        )
        assertEquals(null, ReasoningEffort.AUTO.wireValue)
        assertEquals("high", ReasoningEffort.HIGH.wireValue)
    }
}

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
                ReasoningEffort.AUTO,
                ReasoningEffort.LOW,
                ReasoningEffort.MEDIUM,
                ReasoningEffort.HIGH,
                ReasoningEffort.XHIGH,
                ReasoningEffort.MAX,
            ),
            levels,
        )
        assertEquals(null, ReasoningEffort.AUTO.wireValue)
        assertEquals("high", ReasoningEffort.HIGH.wireValue)
    }

    @Test
    fun `resolver recognizes reasoning even when a route has no verified effort transport`() {
        val gemini = ModelSpecResolver.resolve(Service.Gemini.id, "gemini-2.5-pro")
        assertTrue(gemini.supportsReasoning)
        assertTrue(gemini.reasoning.supportsCustomBudget)
        assertTrue(gemini.reasoning.adjustableEfforts.isEmpty())

        val qwen = ModelSpecResolver.resolve(Service.OpenRouter.id, "qwen/qwen3-235b-a22b")
        assertTrue(qwen.supportsReasoning)
        assertTrue(qwen.reasoning.canDisable)
        assertTrue(qwen.reasoning.adjustableEfforts.isEmpty())
    }

    @Test
    fun `plain chat models do not pretend to support reasoning`() {
        val spec = ModelSpecResolver.resolve(Service.OpenAI.id, "gpt-4o")
        assertTrue(!spec.supportsReasoning)
        assertTrue(spec.reasoning.adjustableEfforts.isEmpty())
    }

    @Test
    fun `other known OpenAI reasoning families expose their supported subset`() {
        val levels = supportedReasoningEfforts(Service.OpenAI.id, "gpt-5.5")
        assertEquals(
            listOf(ReasoningEffort.AUTO, ReasoningEffort.LOW, ReasoningEffort.MEDIUM, ReasoningEffort.HIGH, ReasoningEffort.XHIGH),
            levels,
        )
        assertTrue(ReasoningEffort.MAX !in levels)
        assertEquals(
            levels,
            supportedReasoningEfforts(Service.OpenAI.id, "gpt-5.4"),
        )
    }
}

package com.inspiredandroid.kai.data

/**
 * Kairon's provider-neutral model capability description.
 *
 * This is intentionally separate from request DTOs. The UI asks this layer what a
 * model can do; provider adapters decide how a capability is encoded on the wire.
 * That separation lets unknown/aggregated models be identified as reasoning-capable
 * without pretending Kairon can safely send an effort parameter for them.
 */
enum class ModelModality { TEXT, IMAGE, AUDIO, VIDEO, PDF }

enum class ModelAbility { TOOLS, REASONING, STRUCTURED_OUTPUT }

enum class ReasoningDialect {
    NONE,
    OPENAI_RESPONSES,
    OPENAI_EFFORT,
    OPENROUTER,
    ANTHROPIC_BUDGET,
    GEMINI_THINKING,
    THINKING_TOGGLE,
    KIMI_THINKING,
}

data class ReasoningSpec(
    val supported: Boolean = false,
    val dialect: ReasoningDialect = ReasoningDialect.NONE,
    val adjustableEfforts: List<ReasoningEffort> = emptyList(),
    val defaultEffort: ReasoningEffort = ReasoningEffort.AUTO,
    val canDisable: Boolean = false,
    val supportsCustomBudget: Boolean = false,
)

data class ModelSpec(
    val modelId: String,
    val inputModalities: Set<ModelModality> = setOf(ModelModality.TEXT),
    val outputModalities: Set<ModelModality> = setOf(ModelModality.TEXT),
    val abilities: Set<ModelAbility> = emptySet(),
    val reasoning: ReasoningSpec = ReasoningSpec(),
    val contextWindow: Long? = null,
) {
    val supportsTools: Boolean get() = ModelAbility.TOOLS in abilities
    val supportsReasoning: Boolean get() = ModelAbility.REASONING in abilities
    val supportsImages: Boolean get() = ModelModality.IMAGE in inputModalities
}

/**
 * Central capability resolver used by composer controls, attachments, agent filters,
 * and later the model editor. Unknown models stay conservative: capabilities are only
 * added when Kairon has a concrete family rule or local metadata.
 */
object ModelSpecResolver {
    fun resolve(serviceId: String, modelId: String): ModelSpec {
        val id = normalize(modelId)
        val abilities = buildSet {
            if (supportsTools(modelId)) add(ModelAbility.TOOLS)
        }.toMutableSet()
        val input = buildSet {
            add(ModelModality.TEXT)
            if (modelSupportsImages(modelId)) add(ModelModality.IMAGE)
        }

        val reasoning = reasoningSpec(serviceId, id)
        if (reasoning.supported) abilities += ModelAbility.REASONING

        return ModelSpec(
            modelId = modelId,
            inputModalities = input,
            abilities = abilities,
            reasoning = reasoning,
        )
    }

    private fun reasoningSpec(serviceId: String, id: String): ReasoningSpec {
        // GPT-5 families are recognized even through aggregators, but only native
        // OpenAI currently gets an adjustable slider because that is the route Kairon
        // has verified and actually forwards effort on.
        if (id == "gpt-5.6" || id.startsWith("gpt-5.6-")) {
            return ReasoningSpec(
                supported = true,
                dialect = if (serviceId == Service.OpenAI.id) ReasoningDialect.OPENAI_RESPONSES else ReasoningDialect.OPENAI_EFFORT,
                adjustableEfforts = if (serviceId == Service.OpenAI.id) {
                    listOf(
                        ReasoningEffort.AUTO,
                        ReasoningEffort.LOW,
                        ReasoningEffort.MEDIUM,
                        ReasoningEffort.HIGH,
                        ReasoningEffort.XHIGH,
                        ReasoningEffort.MAX,
                    )
                } else {
                    emptyList()
                },
            )
        }
        if (id == "gpt-5.5" || id.startsWith("gpt-5.5-") ||
            id == "gpt-5.4" || id.startsWith("gpt-5.4-")
        ) {
            return ReasoningSpec(
                supported = true,
                dialect = if (serviceId == Service.OpenAI.id) ReasoningDialect.OPENAI_RESPONSES else ReasoningDialect.OPENAI_EFFORT,
                adjustableEfforts = if (serviceId == Service.OpenAI.id) {
                    listOf(
                        ReasoningEffort.AUTO,
                        ReasoningEffort.LOW,
                        ReasoningEffort.MEDIUM,
                        ReasoningEffort.HIGH,
                        ReasoningEffort.XHIGH,
                    )
                } else {
                    emptyList()
                },
            )
        }

        if (id.matches(Regex("""o[134](?:$|[-.].*)"""))) {
            return ReasoningSpec(supported = true, dialect = ReasoningDialect.OPENAI_EFFORT)
        }
        if (id.contains("gemini-2.5") || id.contains("gemini-3")) {
            return ReasoningSpec(
                supported = true,
                dialect = ReasoningDialect.GEMINI_THINKING,
                supportsCustomBudget = id.contains("2.5"),
            )
        }
        if (id.contains("claude-3.7") || id.contains("claude-4") || id.contains("claude-5")) {
            return ReasoningSpec(
                supported = true,
                dialect = ReasoningDialect.ANTHROPIC_BUDGET,
                supportsCustomBudget = true,
            )
        }
        if (id.contains("deepseek-r1") || id.contains("deepseek-reasoner")) {
            return ReasoningSpec(
                supported = true,
                dialect = ReasoningDialect.THINKING_TOGGLE,
                canDisable = id.contains("reasoner").not(),
            )
        }
        if (id.contains("qwen3") || id.contains("qwq")) {
            return ReasoningSpec(
                supported = true,
                dialect = ReasoningDialect.THINKING_TOGGLE,
                canDisable = true,
            )
        }
        if (id.contains("glm-z1") || id.contains("glm-5.2") || id.contains("glm-5.3")) {
            return ReasoningSpec(
                supported = true,
                dialect = ReasoningDialect.THINKING_TOGGLE,
                canDisable = true,
            )
        }
        if (id.contains("kimi-k2") || id.contains("kimi-k3")) {
            return ReasoningSpec(
                supported = true,
                dialect = ReasoningDialect.KIMI_THINKING,
                canDisable = !id.contains("thinking"),
            )
        }
        if (id.contains("sonar-reasoning") || id.contains("sonar-deep-research")) {
            return ReasoningSpec(supported = true, dialect = ReasoningDialect.THINKING_TOGGLE)
        }

        return ReasoningSpec()
    }

    private fun normalize(modelId: String): String = modelId.trim().substringAfterLast('/').lowercase()
}

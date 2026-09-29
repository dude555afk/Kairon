package com.inspiredandroid.kai.data

/**
 * An explicit per-request preference, not a provider-wide toggle. AUTO leaves the
 * request untouched so the provider uses its native default.
 */
enum class ReasoningEffort(val wireValue: String?) {
    AUTO(null),
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    XHIGH("xhigh"),
    MAX("max"),
}

/**
 * Conservative capability registry: never guess from a model being described as
 * "reasoning" or from the mere presence of a gateway. Unsupported/unknown routes
 * (including Kai Free and OpenRouter free-routing aliases) get no control and no
 * reasoning parameter on the wire.
 *
 * Extend only after the corresponding request DTO/provider path supports effort.
 */
fun supportedReasoningEfforts(serviceId: String, modelId: String): List<ReasoningEffort> {
    if (serviceId != Service.OpenAI.id) return emptyList()
    // Model IDs may include provider revisions/snapshots; match the family rather than
    // requiring one of four exact display IDs. Only the native OpenAI Responses route
    // currently forwards the effort field, so other providers must not claim support.
    val id = modelId.trim().lowercase()
    val family = when {
        id == "gpt-5.6" || id.startsWith("gpt-5.6-") -> "5.6"
        id == "gpt-5.5" || id.startsWith("gpt-5.5-") -> "5.5"
        id == "gpt-5.4" || id.startsWith("gpt-5.4-") -> "5.4"
        else -> return emptyList()
    }
    val levels = mutableListOf(
        ReasoningEffort.AUTO,
        ReasoningEffort.LOW,
        ReasoningEffort.MEDIUM,
        ReasoningEffort.HIGH,
        ReasoningEffort.XHIGH,
    )
    if (family == "5.6") levels.add(ReasoningEffort.MAX)
    return levels
}

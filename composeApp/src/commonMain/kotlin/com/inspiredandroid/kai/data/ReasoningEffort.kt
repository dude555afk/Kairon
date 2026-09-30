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
 * Compatibility helper for existing composer/request code.
 *
 * Capability discovery now lives in [ModelSpecResolver]. Only models whose current
 * provider route has a verified adjustable effort transport return levels here.
 */
fun supportedReasoningEfforts(serviceId: String, modelId: String): List<ReasoningEffort> = ModelSpecResolver.resolve(serviceId, modelId).reasoning.adjustableEfforts

package com.inspiredandroid.kai.data

/**
 * Compatibility helper for existing composer/request code.
 *
 * Capability discovery now lives in [ModelSpecResolver]. Only models whose current
 * provider route has a verified adjustable effort transport return levels here.
 */
fun supportedReasoningEfforts(serviceId: String, modelId: String): List<ReasoningEffort> =
    ModelSpecResolver.resolve(serviceId, modelId).reasoning.adjustableEfforts

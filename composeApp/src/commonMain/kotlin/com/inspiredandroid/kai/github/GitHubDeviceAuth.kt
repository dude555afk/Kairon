package com.inspiredandroid.kai.github

import com.inspiredandroid.kai.httpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull

/**
 * GitHub OAuth device authorization. No client secret, embedded browser credentials, or
 * personal access token entry. The OAuth App must have Device Flow enabled in GitHub.
 *
 * Tokens are returned to the caller; this class NEVER persists or logs them.
 */
class GitHubDeviceAuth {
    private val client = httpClient()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun start(clientId: String): GitHubDeviceChallenge {
        require(clientId.matches(Regex("[A-Za-z0-9_]{8,128}"))) { "Enter a valid OAuth App client ID" }
        val response = client.submitForm(
            url = "https://github.com/login/device/code",
            formParameters = Parameters.build {
                append("client_id", clientId)
                append("scope", "repo read:user")
            },
        ) { header("Accept", "application/json") }
        require(response.status.isSuccess()) { "GitHub authorization could not start (HTTP ${response.status.value})" }
        val data = json.parseToJsonElement(response.bodyAsText()).jsonObject
        return GitHubDeviceChallenge(
            deviceCode = data.required("device_code"),
            userCode = data.required("user_code"),
            verificationUri = data.required("verification_uri"),
            expiresInSeconds = data.required("expires_in").toInt(),
            intervalSeconds = data.required("interval").toInt().coerceAtLeast(5),
        )
    }

    suspend fun awaitAuthorization(clientId: String, challenge: GitHubDeviceChallenge): GitHubSession {
        var interval = challenge.intervalSeconds.coerceAtLeast(5)
        val deadline = kotlin.time.TimeSource.Monotonic.markNow() + kotlin.time.Duration.parse("${challenge.expiresInSeconds}s")
        while (deadline.hasNotPassedNow()) {
            delay(interval * 1000L)
            val response = client.submitForm(
                url = "https://github.com/login/oauth/access_token",
                formParameters = Parameters.build {
                    append("client_id", clientId)
                    append("device_code", challenge.deviceCode)
                    append("grant_type", "urn:ietf:params:oauth:grant-type:device_code")
                },
            ) { header("Accept", "application/json") }
            require(response.status.isSuccess()) { "GitHub authorization failed (HTTP ${response.status.value})" }
            val result = json.parseToJsonElement(response.bodyAsText()).jsonObject
            when (result["error"]?.jsonPrimitive?.contentOrNull) {
                null -> {
                    val token = result.required("access_token")
                    require(result["token_type"]?.jsonPrimitive?.contentOrNull.equals("bearer", ignoreCase = true)) {
                        "Unexpected authorization token type"
                    }
                    return GitHubSession(token, result["scope"]?.jsonPrimitive?.contentOrNull.orEmpty())
                }
                "authorization_pending" -> Unit
                "slow_down" -> interval += 5
                "expired_token", "access_denied" -> error("GitHub authorization expired or was denied")
                else -> error("GitHub authorization failed")
            }
        }
        error("GitHub authorization timed out")
    }
}

private fun kotlinx.serialization.json.JsonObject.required(name: String): String =
    this[name]?.jsonPrimitive?.contentOrNull?.takeIf { it.isNotBlank() }
        ?: error("GitHub authorization response is missing $name")

data class GitHubDeviceChallenge(
    val deviceCode: String,
    val userCode: String,
    val verificationUri: String,
    val expiresInSeconds: Int,
    val intervalSeconds: Int,
)

/** Keep only in volatile session memory. Never place token in AppSettings or exported settings. */
class GitHubSession internal constructor(internal val token: String, val scopes: String)

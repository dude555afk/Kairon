package com.inspiredandroid.kai.github

import com.inspiredandroid.kai.httpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Remote-only GitHub data source. Does not clone, execute commands, write local files, or retain
 * credentials. A trusted auth component supplies a short-lived token only for the API call.
 *
 * Its workflow dispatcher triggers only an existing YAML workflow and requires explicit user action.
 */
class RemoteGitHub {
    private val client = httpClient {
        install(HttpTimeout) { requestTimeoutMillis = 20_000 }
    }
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun repositories(token: String, page: Int = 1): List<GitHubRepository> {
        val result = getJson(token, "/user/repos?sort=updated&per_page=30&page=${page.coerceIn(1, 100)}")
        return result.jsonArray.map { item ->
            val obj = item.jsonObject
            GitHubRepository(
                fullName = obj.string("full_name"),
                defaultBranch = obj.string("default_branch"),
                isPrivate = obj["private"]?.jsonPrimitive?.booleanOrNull == true,
                description = obj["description"]?.jsonPrimitive?.contentOrNull,
            )
        }
    }

    suspend fun directory(token: String, repository: String, path: String = "", ref: String? = null): List<GitHubEntry> {
        val route = contentsRoute(repository, path, ref)
        return when (val result = getJson(token, route)) {
            is JsonArray -> result.map { it.jsonObject.toEntry() }
            is JsonObject -> listOf(result.toEntry())
            else -> emptyList()
        }
    }

    suspend fun file(token: String, repository: String, path: String, ref: String? = null): GitHubFile {
        require(path.isNotBlank()) { "A file path is required" }
        val result = getJson(token, contentsRoute(repository, path, ref)).jsonObject
        require(result.string("type") == "file") { "The selected path is not a file" }
        val size = result["size"]?.jsonPrimitive?.contentOrNull?.toLongOrNull() ?: 0L
        require(size <= MAX_FILE_BYTES) { "File exceeds remote preview limit" }
        // GitHub's content response is base64; leave decoding to the caller. We do not cache bytes.
        return GitHubFile(
            path = result.string("path"),
            sha = result.string("sha"),
            size = size,
            encodedContent = result["content"]?.jsonPrimitive?.contentOrNull.orEmpty(),
            encoding = result.string("encoding"),
        )
    }

    suspend fun workflowRuns(token: String, repository: String, branch: String? = null): List<GitHubWorkflowRun> {
        val (owner, repo) = splitRepository(repository)
        val query = branch?.let { "?branch=${encodePathSegment(it)}&per_page=20" } ?: "?per_page=20"
        val response = getJson(token, "/repos/$owner/$repo/actions/runs$query").jsonObject
        return response["workflow_runs"]?.jsonArray.orEmpty().map { item ->
            val run = item.jsonObject
            GitHubWorkflowRun(
                id = run["id"]?.jsonPrimitive?.contentOrNull?.toLongOrNull() ?: 0L,
                name = run.string("name"),
                status = run.string("status"),
                conclusion = run["conclusion"]?.jsonPrimitive?.contentOrNull,
                url = run.string("html_url"),
            )
        }
    }

    /** Dispatches an existing workflow; does not change source code or execute arbitrary commands. */
    suspend fun dispatchWorkflow(token: String, repository: String, workflow: String, ref: String, inputs: Map<String, String> = emptyMap()) {
        val (owner, repo) = splitRepository(repository)
        require(workflow.matches(Regex("[A-Za-z0-9_.-]+\\.ya?ml"))) { "Choose an existing workflow YAML filename" }
        require(ref.isNotBlank() && ref.length <= 255 && !ref.any { it.isWhitespace() }) { "Invalid Git ref" }
        require(inputs.size <= 5 && inputs.all { (key, value) -> key.matches(Regex("[a-z_]+")) && value.length <= 4000 }) { "Invalid workflow inputs" }
        require(token.isNotBlank()) { "GitHub authorization is required" }
        val response = client.post("https://api.github.com/repos/$owner/$repo/actions/workflows/${encodePathSegment(workflow)}/dispatches") {
            bearerAuth(token)
            header("Accept", "application/vnd.github+json")
            header("X-GitHub-Api-Version", "2022-11-28")
            header("User-Agent", "Kairon-Remote-Agent")
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject {
                put("ref", ref)
                put("inputs", buildJsonObject { inputs.forEach { (key, value) -> put(key, value) } })
            }.toString())
        }
        if (!response.status.isSuccess()) throw GitHubRequestException(response.status.value)
    }

    private suspend fun getJson(token: String, route: String): kotlinx.serialization.json.JsonElement {
        require(token.isNotBlank()) { "GitHub authorization is required" }
        val response = client.get("https://api.github.com$route") {
            bearerAuth(token)
            header("Accept", "application/vnd.github+json")
            header("X-GitHub-Api-Version", "2022-11-28")
            header("User-Agent", "Kairon-Remote-Agent")
        }
        val body = response.bodyAsText()
        if (!response.status.isSuccess()) {
            // Do not include response headers, credential material, or arbitrary response bodies.
            throw GitHubRequestException(response.status.value)
        }
        return json.parseToJsonElement(body)
    }

    private fun contentsRoute(repository: String, path: String, ref: String?): String {
        val (owner, repo) = splitRepository(repository)
        val encodedPath = path.split('/').filter { it.isNotEmpty() }.joinToString("/") { encodePathSegment(it) }
        val query = ref?.takeIf { it.isNotBlank() }?.let { "?ref=${encodePathSegment(it)}" }.orEmpty()
        return "/repos/$owner/$repo/contents/$encodedPath$query"
    }
}

private const val MAX_FILE_BYTES = 512L * 1024L

/** Prevents accidentally building an arbitrary URL from model-produced repository names. */
fun splitRepository(repository: String): Pair<String, String> {
    val pieces = repository.split('/')
    require(pieces.size == 2 && pieces.all { it.matches(Regex("[A-Za-z0-9_.-]+")) && it != "." && it != ".." }) {
        "Expected a repository in owner/name format"
    }
    return pieces[0] to pieces[1]
}

internal fun encodePathSegment(value: String): String = buildString {
    for (byte in value.encodeToByteArray()) {
        val i = byte.toInt() and 0xff
        if (i in 65..90 || i in 97..122 || i in 48..57 || i in listOf(45, 46, 95, 126)) {
            append(i.toChar())
        } else {
            append('%')
            append(i.toString(16).uppercase().padStart(2, '0'))
        }
    }
}

private fun JsonObject.string(key: String): String = this[key]?.jsonPrimitive?.contentOrNull.orEmpty()

private fun JsonObject.toEntry() = GitHubEntry(
    name = string("name"),
    path = string("path"),
    type = string("type"),
    sha = string("sha"),
    size = this["size"]?.jsonPrimitive?.contentOrNull?.toLongOrNull(),
)

@Serializable
data class GitHubRepository(val fullName: String, val defaultBranch: String, val isPrivate: Boolean, val description: String?)

@Serializable
data class GitHubEntry(val name: String, val path: String, val type: String, val sha: String, val size: Long?)

@Serializable
data class GitHubFile(val path: String, val sha: String, val size: Long, val encodedContent: String, val encoding: String)

@Serializable
data class GitHubWorkflowRun(val id: Long, val name: String, val status: String, val conclusion: String?, val url: String)

class GitHubRequestException(val statusCode: Int) : IllegalStateException("GitHub request failed (HTTP $statusCode)")

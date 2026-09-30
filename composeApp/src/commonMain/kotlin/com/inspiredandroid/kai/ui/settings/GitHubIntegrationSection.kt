package com.inspiredandroid.kai.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.inspiredandroid.kai.github.GitHubDeviceAuth
import com.inspiredandroid.kai.github.GitHubDeviceChallenge
import com.inspiredandroid.kai.github.GitHubEntry
import com.inspiredandroid.kai.github.GitHubRepository
import com.inspiredandroid.kai.github.GitHubSession
import com.inspiredandroid.kai.github.GitHubWorkflowRun
import com.inspiredandroid.kai.github.RemoteGitHub
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Explicit device authorization; token remains in this composition's memory and is discarded
 * when the section leaves composition or Disconnect is pressed. No silent persistence/export.
 */
@OptIn(ExperimentalEncodingApi::class)
@Composable
internal fun GitHubIntegrationSection() {
    val auth = remember { GitHubDeviceAuth() }
    val github = remember { RemoteGitHub() }
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    var clientId by remember { mutableStateOf("") }
    var challenge by remember { mutableStateOf<GitHubDeviceChallenge?>(null) }
    var session by remember { mutableStateOf<GitHubSession?>(null) }
    var repositories by remember { mutableStateOf<List<GitHubRepository>>(emptyList()) }
    var selectedRepository by remember { mutableStateOf<GitHubRepository?>(null) }
    var path by remember { mutableStateOf("") }
    var entries by remember { mutableStateOf<List<GitHubEntry>>(emptyList()) }
    var preview by remember { mutableStateOf("") }
    var runs by remember { mutableStateOf<List<GitHubWorkflowRun>>(emptyList()) }
    var agentTask by remember { mutableStateOf("") }
    var agentModel by remember { mutableStateOf("minimax/minimax-m2.1:free") }
    var busy by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }
    SettingsCard {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Text("GitHub · Remote access", style = MaterialTheme.typography.titleMedium)
            Text(
                "Connect through GitHub's official device flow. Register a GitHub OAuth App with Device Flow enabled and enter its public client ID. This preview keeps access only until you leave this screen.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (session == null) {
                OutlinedTextField(
                    value = clientId,
                    onValueChange = { clientId = it.trim() },
                    label = { Text("OAuth App client ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(enabled = !busy && clientId.isNotBlank(), onClick = {
                    busy = true
                    status = "Requesting authorization..."
                    scope.launch {
                        try {
                            challenge = auth.start(clientId)
                            status = "Enter the displayed code on GitHub, then return here."
                        } catch (e: Exception) {
                            status = e.message ?: "Could not start GitHub authorization"
                        } finally {
                            busy = false
                        }
                    }
                }) { Text("Connect GitHub") }
                challenge?.let { current ->
                    Text("Code: ${current.userCode}", style = MaterialTheme.typography.titleLarge)
                    OutlinedButton(onClick = { uriHandler.openUri(current.verificationUri) }) {
                        Text("Open GitHub verification")
                    }
                    Button(enabled = !busy, onClick = {
                        busy = true
                        status = "Waiting for authorization..."
                        scope.launch {
                            try {
                                val connected = auth.awaitAuthorization(clientId, current)
                                val repos = github.repositories(connected.token)
                                session = connected
                                repositories = repos
                                challenge = null
                                status = "Connected. Showing up to 30 recently updated repositories."
                            } catch (e: Exception) {
                                status = e.message ?: "Authorization failed"
                            } finally {
                                busy = false
                            }
                        }
                    }) { Text("I've entered the code") }
                }
            } else {
                Text("Connected for this session", color = MaterialTheme.colorScheme.primary)
                if (selectedRepository == null) {
                    repositories.forEach { repo ->
                        OutlinedButton(enabled = !busy, onClick = {
                            selectedRepository = repo
                            path = ""
                            preview = ""
                            busy = true
                            scope.launch {
                                try {
                                    entries = github.directory(session!!.token, repo.fullName, ref = repo.defaultBranch)
                                    runs = github.workflowRuns(session!!.token, repo.fullName, repo.defaultBranch)
                                    status = "Opened ${repo.fullName}"
                                } catch (e: Exception) {
                                    status = e.message ?: "Unable to open repository"
                                } finally {
                                    busy = false
                                }
                            }
                        }, modifier = Modifier.fillMaxWidth()) { Text(repo.fullName) }
                    }
                } else {
                    val repo = selectedRepository!!
                    Text(repo.fullName, style = MaterialTheme.typography.titleMedium)
                    Text("Branch: ${repo.defaultBranch} · Path: /$path", style = MaterialTheme.typography.bodySmall)
                    OutlinedButton(enabled = !busy, onClick = {
                        selectedRepository = null
                        entries = emptyList()
                        preview = ""
                        runs = emptyList()
                    }) { Text("Back to repositories") }
                    if (path.isNotEmpty()) {
                        OutlinedButton(enabled = !busy, onClick = {
                            val parent = path.substringBeforeLast('/', "")
                            busy = true
                            scope.launch {
                                try {
                                    entries = github.directory(session!!.token, repo.fullName, parent, repo.defaultBranch)
                                    path = parent
                                    preview = ""
                                } catch (e: Exception) {
                                    status = e.message ?: "Cannot open parent"
                                } finally {
                                    busy = false
                                }
                            }
                        }) { Text("Up one folder") }
                    }
                    entries.forEach { entry ->
                        OutlinedButton(enabled = !busy, onClick = {
                            busy = true
                            scope.launch {
                                try {
                                    if (entry.type == "dir") {
                                        entries = github.directory(session!!.token, repo.fullName, entry.path, repo.defaultBranch)
                                        path = entry.path
                                        preview = ""
                                    } else if (entry.type == "file") {
                                        val file = github.file(session!!.token, repo.fullName, entry.path, repo.defaultBranch)
                                        preview = if (file.encoding == "base64") {
                                            Base64.decode(file.encodedContent.filterNot(Char::isWhitespace)).decodeToString()
                                                .take(12000)
                                        } else {
                                            "This file cannot be previewed."
                                        }
                                        status = "Preview: ${entry.path} (read only)"
                                    }
                                } catch (e: Exception) {
                                    status = e.message ?: "Cannot open file"
                                } finally {
                                    busy = false
                                }
                            }
                        }, modifier = Modifier.fillMaxWidth()) {
                            Text("${if (entry.type == "dir") "📁" else "📄"} ${entry.name}")
                        }
                    }
                    if (preview.isNotBlank()) {
                        Text(preview, style = MaterialTheme.typography.bodySmall)
                    }
                    HorizontalDivider()
                    Text("GitHub Actions", style = MaterialTheme.typography.titleSmall)
                    OutlinedButton(enabled = !busy, onClick = {
                        busy = true
                        scope.launch {
                            try {
                                runs = github.workflowRuns(session!!.token, repo.fullName, repo.defaultBranch)
                                status = "Build runs refreshed"
                            } catch (e: Exception) {
                                status = e.message ?: "Cannot load build runs"
                            } finally {
                                busy = false
                            }
                        }
                    }) { Text("Refresh runs") }
                    runs.take(5).forEach { run ->
                        OutlinedButton(onClick = { uriHandler.openUri(run.url) }, modifier = Modifier.fillMaxWidth()) {
                            Text("${run.name}: ${run.conclusion ?: run.status}")
                        }
                    }
                    if (repo.fullName == "dude555afk/Kairon") {
                        HorizontalDivider()
                        Text("Remote coding agent", style = MaterialTheme.typography.titleSmall)
                        Text(
                            "Runs on GitHub, proposes changes in a separate branch, builds the APK and opens a pull request. Requires the workflow on the repository's default branch and Actions write/PR permissions. No commands run on your phone.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                        OutlinedTextField(
                            value = agentTask,
                            onValueChange = { agentTask = it.take(4000) },
                            label = { Text("Describe what the agent should change") },
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        OutlinedTextField(
                            value = agentModel,
                            onValueChange = { agentModel = it.take(120) },
                            label = { Text("Kilo model (:free for anonymous use)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Button(enabled = !busy && agentTask.isNotBlank(), onClick = {
                            busy = true
                            scope.launch {
                                try {
                                    github.dispatchWorkflow(
                                        session!!.token,
                                        repo.fullName,
                                        "remote-coding-agent.yml",
                                        "feature/remote-github-agent",
                                        mapOf("task" to agentTask, "model" to agentModel),
                                    )
                                    status = "Coding task dispatched. Refresh Actions and review the proposed pull request."
                                } catch (e: Exception) {
                                    status = e.message ?: "Agent dispatch failed"
                                } finally {
                                    busy = false
                                }
                            }
                        }) { Text("Run autonomous coding task") }
                    }
                    if (repo.fullName == "dude555afk/Kairon") {
                        Button(enabled = !busy, onClick = {
                            busy = true
                            scope.launch {
                                try {
                                    github.dispatchWorkflow(
                                        session!!.token,
                                        repo.fullName,
                                        "kairon-preview.yml",
                                        "feature/remote-github-agent",
                                    )
                                    status = "Preview build requested. Refresh runs to see it."
                                } catch (e: Exception) {
                                    status = e.message ?: "Build dispatch failed"
                                } finally {
                                    busy = false
                                }
                            }
                        }) { Text("Build preview APK remotely") }
                    }
                }
                OutlinedButton(onClick = {
                    session = null
                    selectedRepository = null
                    entries = emptyList()
                    preview = ""
                    runs = emptyList()
                    repositories = emptyList()
                    challenge = null
                    status = "Local session disconnected. Revoke authorization on GitHub to invalidate the granted token."
                }) { Text("Disconnect") }
                OutlinedButton(onClick = { uriHandler.openUri("https://github.com/settings/applications") }) {
                    Text("Manage GitHub authorization")
                }
            }
            if (status.isNotBlank()) Text(status, style = MaterialTheme.typography.bodySmall)
        }
    }
}

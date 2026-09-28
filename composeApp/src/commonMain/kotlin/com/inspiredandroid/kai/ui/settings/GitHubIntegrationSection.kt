package com.inspiredandroid.kai.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
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
import com.inspiredandroid.kai.github.GitHubSession
import com.inspiredandroid.kai.github.RemoteGitHub
import com.inspiredandroid.kai.github.GitHubRepository
import kotlinx.coroutines.launch

/**
 * Explicit device authorization; token remains in this composition's memory and is discarded
 * when the section leaves composition or Disconnect is pressed. No silent persistence/export.
 */
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
                        } finally { busy = false }
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
                            } finally { busy = false }
                        }
                    }) { Text("I've entered the code") }
                }
            } else {
                Text("Connected for this session", color = MaterialTheme.colorScheme.primary)
                repositories.forEach { repo ->
                    Text("${repo.fullName} · ${repo.defaultBranch}", style = MaterialTheme.typography.bodySmall)
                }
                OutlinedButton(onClick = {
                    session = null
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

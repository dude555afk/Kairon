package com.inspiredandroid.kai.github

import kotlinx.serialization.Serializable

/**
 * Wire contract for a future remote agent service. Android only exchanges bounded task
 * descriptions, progress, diff summaries, and artifact links. Repository checkout, model tool
 * execution, Gradle, and GitHub credential exchange stay on the server.
 *
 * Nothing here starts a job until an authenticated remote service implements the contract.
 */
@Serializable
data class RemoteCodingTask(
    val id: String,
    val repository: String,
    val baseBranch: String,
    val workingBranch: String,
    val instruction: String,
    val status: RemoteTaskStatus,
    val completedSteps: List<RemoteTaskStep> = emptyList(),
    val pendingApproval: RemoteApproval? = null,
    val artifacts: List<RemoteArtifact> = emptyList(),
)

@Serializable
enum class RemoteTaskStatus {
    QUEUED,
    ANALYZING,
    EDITING,
    VERIFYING,
    WAITING_FOR_APPROVAL,
    COMPLETED,
    FAILED,
    CANCELLED,
}

@Serializable
data class RemoteTaskStep(val title: String, val status: RemoteTaskStatus, val summary: String? = null)

@Serializable
data class RemoteArtifact(val name: String, val downloadUrl: String, val expiresAt: String? = null)

@Serializable
data class RemoteApproval(
    val id: String,
    val kind: RemoteApprovalKind,
    val diffSummary: String,
    val filesChanged: List<String>,
    val expectedBranchHeadSha: String,
)

@Serializable
enum class RemoteApprovalKind {
    PUBLISH_COMMIT,
    DELETE_FILE,
    MERGE_BRANCH,
    CREATE_RELEASE,
    MODIFY_WORKFLOW,
    CHANGE_PERMISSIONS,
}

/** Check both intent and a fixed commit SHA to reject approvals against a moving branch. */
fun RemoteApproval.isValidFor(kind: RemoteApprovalKind, currentHeadSha: String): Boolean =
    this.kind == kind && currentHeadSha.isNotBlank() && expectedBranchHeadSha == currentHeadSha &&
        id.isNotBlank() && filesChanged.isNotEmpty()

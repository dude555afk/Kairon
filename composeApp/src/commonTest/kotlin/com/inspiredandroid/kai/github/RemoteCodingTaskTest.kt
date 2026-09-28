package com.inspiredandroid.kai.github

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RemoteCodingTaskTest {
    @Test
    fun approvalsAreBoundToTheOperationAndExpectedHead() {
        val request = RemoteApproval(
            id = "approval-1",
            kind = RemoteApprovalKind.PUBLISH_COMMIT,
            diffSummary = "Fix UI",
            filesChanged = listOf("composeApp/src/commonMain/kotlin/Example.kt"),
            expectedBranchHeadSha = "abc123",
        )
        assertTrue(request.isValidFor(RemoteApprovalKind.PUBLISH_COMMIT, "abc123"))
        assertFalse(request.isValidFor(RemoteApprovalKind.MERGE_BRANCH, "abc123"))
        assertFalse(request.isValidFor(RemoteApprovalKind.PUBLISH_COMMIT, "changed"))
        assertFalse(request.isValidFor(RemoteApprovalKind.PUBLISH_COMMIT, ""))
    }
}

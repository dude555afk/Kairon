# Kairon remote GitHub coding agent

## Goal

An Android phone is a thin remote controller. The AI provider handles inference, GitHub stores
source control, and a separately deployed service coordinates remote agent tasks. GitHub Actions
handles ordinary CI/build workflows. Neither a repository clone nor Gradle nor an agent
workspace runs on the phone. Voice search is out of scope.

## Implemented foundation (this branch)

- `RemoteGitHub`: bounded, read-only GitHub REST access for repository listing, tree/file
  previews, and workflow status. The authenticated token is passed per call, never persisted
  by the client. File previews reject responses above 512 KiB.
- Strict `owner/repo` validation and URL segment encoding.
- Serializable remote task, approval, progress, and artifact data contracts.
- Tests for repository route validation and approval semantics.

**Not implemented yet:** user-facing GitHub account connection, an OAuth server/broker,
remote agent execution, write tools, workflow dispatch, artifact download, task persistence,
and full-app integration. The data contracts must not be mistaken for a live backend.

## Authentication design

Register a GitHub App with fine-grained repository permissions. Use GitHub App web-user
authorization with a hosted callback/broker; never embed its client secret in the APK.
Use state and PKCE wherever the selected authorization flow supports it. The broker
manages short-lived credentials, encrypts refresh/authorization data in server-side storage,
exchanges them for narrowly scoped repository credentials, and returns only an app session
to Android. Android uses platform-backed secure storage for the session. Provide repository
selection, account disconnect, revoke, and per-repo permissions. No tokens or full repository
contents belong in prompt, logs, app preferences, or analytics.

## Coding execution

1. The user chooses a repository and requests a change in chat.
2. The broker authorizes access to that repository only.
3. The orchestrator creates `kairon/task-<id>` off the requested base revision and
   starts an ephemeral cloud workspace. For trivial read-only queries, use GitHub REST
   without creating a workspace.
4. The coding model receives brokered tools: search/list/read, patch/create, diff,
   run checks, fetch CI logs, and read artifacts. Tools enforce paths, budgets, scopes,
   and redaction on the server.
5. Writes occur in the isolated branch; before publishing, return a diff and expected
   branch-head SHA for explicit user approval. Reject stale approvals.
6. Build/test workflows run remotely. Retry compilation fixes within a strict budget,
   then surface the remaining failure, logs, and rollback option.
7. The server reports progress and signed, expiring artifact links to the Android UI.
8. Cleanup: stop idle runners, expire artifacts, and discard temporary workspace data.

No autonomous release, merge, destructive edit, or permission escalation without a
fresh, operation-specific confirmation. Treat repository content and build logs as
untrusted input, not instructions. Preserve branch checkpoints and offer rollback.

## Mobile resource budget

Only fetch requested file slices; paginate repository trees and logs; cap cache and
clear on disconnect; suspend foreground streaming when app is backgrounded and resume
from task cursor. Persist task state on the backend, not a long-running Android service.
Prefer authenticated notifications for task completion. The client must not download
a repository or spawn a local toolchain.

## Next implementation gates

1. Run cross-platform compilation and fix any issues.
2. Implement credential broker and secure Android session storage.
3. Add GitHub connection UI and the read-only repository browser.
4. Build remote agent service and read-only tools; test permission boundaries.
5. Implement branch-only edits and diff approval; then CI/log inspection and artifact links.
6. Add live task progress, checkpoints, resumability, and on-device release/download UX.

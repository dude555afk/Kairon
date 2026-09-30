# Kairon on Kelivo

This branch intentionally uses Kelivo as the application foundation.
The former Kotlin/Compose Kairon implementation is preserved at:
`backup/pre-kelivo-rebase-20260930`.

Kairon-only capabilities to port onto this Flutter base:
- Dynamic UI rendering
- Remote GitHub coding agent
- Linux sandbox / workspace execution
- Heartbeat / proactive agent flows
- Kilo anonymous/free routing
- Kairon MCP and skills additions
- Android device integrations

Kelivo UI, settings, navigation, providers, model management, memory,
voice, branching/editing/versioning, assistants/profiles, MCP UX,
customization, and other Kelivo behavior are the new baseline.


## Port status

- Kelivo application foundation: active
- Kelivo UI/settings/navigation/providers/memory/voice/branching/customization: active
- Kilo anonymous/free provider: active (`kilo-auto/free`)
- Remote GitHub coding agent: ported to Flutter verification
- Remaining Kairon-native capabilities are being ported onto the Flutter base rather than restoring the old Compose UI.

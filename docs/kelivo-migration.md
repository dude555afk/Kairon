# Kelivo-style Kairon migration

Kairon is moving to a new product shell: the polished, highly configurable chat and
provider experience associated with Kelivo, implemented independently in Kotlin/Compose,
with Kairon's strongest agentic features retained.

## Licensing boundary

Kairon is Apache-2.0 and the Kelivo-derived Orvia source used as a behavior reference is
AGPL-3.0. Do not copy AGPL implementation code into Kairon. Recreate behavior, data
models, interaction patterns, and visual ideas independently in this repository.

## Replace by default

The new implementation should supersede Kairon's existing versions of:

- chat presentation and message surfaces
- composer and per-chat controls
- theme/chat customization
- model capability and reasoning-control discovery
- provider/model configuration UX
- branch/edit/regenerate/version navigation
- search configuration and citation presentation
- memory management UX
- TTS/STT provider management
- assistant/profile configuration
- history and conversation quality-of-life controls

Old implementations should be removed only after their replacements are stable and their
stored data has a migration path.

## Preserve and integrate

These Kairon systems are product differentiators and must remain first-class:

- Dynamic UI / kai-ui renderer
- Linux sandbox
- Kai Build
- Remote GitHub coding agent
- autonomous Heartbeat
- MCP servers and Skills
- Android notifications, SMS, sharing, assistant invocation, calendar/device tools
- anonymous/free Kilo routing where available

Dynamic UI remains a message content type inside the new chat renderer. It must not be
split into a visually disconnected second chat system.

## Architecture rules

1. **Capabilities, not model-name UI hacks.** UI controls read a central ModelSpec.
   Provider adapters decide how a selected option is encoded on the wire.
2. **Appearance is persistent state.** Message components consume ChatAppearance rather
   than hard-coded radii, opacity, spacing, or surface choices.
3. **One conversation model.** Text, markdown, reasoning, tools, attachments, and
   Dynamic UI belong to the same message timeline.
4. **Kelivo-style UX wins on overlap.** Keep a Kairon implementation only when it is
   materially stronger or uniquely enables an agentic/device feature.
5. **No feature regression for power modules.** A shell migration must never disable
   sandbox, Build, GitHub agent, Heartbeat, MCP, Skills, or device integrations.

## Migration order

1. ModelSpec/capability resolver and reasoning control abstraction.
2. Persistent chat appearance model and customizable renderer.
3. Conversation version/branch/edit data model.
4. New composer, message actions, history, and navigation.
5. Provider configuration and model editor.
6. Memory, search, voice, and assistant/profile UX.
7. Reintegrate and visually unify Kairon power modules.
8. Remove superseded legacy UI and run performance/accessibility cleanup.

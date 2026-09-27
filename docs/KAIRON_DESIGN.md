# Kairon: product-wide visual system

Status: design contract, not a claim that the entire redesign is implemented.

## Intent

Kairon is a distinct AI workspace built on Kai's engine. Use the supplied ChatGPT Android screenshots as references for clarity, spatial rhythm, readable chat and navigation, not for branding or a pixel-perfect clone. Keep Kairon identity, logos and product language independent. Preserve all existing provider, agent, dynamic UI, memory, email, task, MCP, skill, sandbox and settings capabilities.

The first chat makeover was a proof of the UI pipeline. It is not an approved final layout. The entire product must be redesigned as a connected system.

## Principles

1. **Content first:** responses and generated interactive screens are the focus. Controls retreat when not needed.
2. **Quiet hierarchy:** layered neutral surfaces and typography provide grouping; avoid outlines around every card, gratuitous gradients and multiple competing accent colors.
3. **One language everywhere:** consistent corner radii, touch targets, spacing, icons, menus, text scale, state treatment and motion across every screen.
4. **Progressive disclosure:** top-level locations show what matters; complex provider and tool options remain accessible inside clearly labeled detail surfaces. Never delete a feature to simplify the first screen.
5. **Responsive and accessible:** mobile first; desktop/tablet adapt. Respect system font scaling, screen readers, keyboard navigation, RTL and reduce-motion settings.
6. **User-chosen colour:** do not lock a fixed colourway yet. Derive surfaces from MaterialTheme and support System, Light, Dark and OLED settings. Accent can become configurable later.
7. **Functional parity:** the model, data/storage, tool execution, credentials, MCP, memory, background service, interactive UI runtime and Linux integration remain intact unless a separate issue explicitly approves a change.

## Component language

- Base canvas: near-flat background supplied by active theme.
- Primary surfaces: gently filled surface-container tiers, no default visible border. Outlines only for focus, active selection, errors or meaningful separation.
- Cards: 16–24 dp radius; no decorative multicolor borders except intentional AI-generated content/activity cues.
- Composer: wide rounded raised surface, distinct text field and compact actions; service, attachments, send/cancel and skills must remain.
- Navigation: a true side drawer on mobile, coherent secondary panes on larger screens, persistent active-page indication and explicit back behaviour.
- Typography: large but restrained screen title, sectional headings, readable body, lighter supporting metadata.
- Spacing: 4/8/12/16/24/32 dp cadence; 48 dp preferred interactive targets.
- Motion: small, purposeful enter/exit and container transitions; no ambient animation solely for decoration.

## Screens and feature map

| Area | Kairon redesign treatment | Preserve |
|---|---|---|
| Chat & welcome | Focused prompt, model indication, spacious messages, calm thinking/tool feedback, contextual actions | Streaming, reasoning, copy, TTS, regeneration, attachments, skills |
| Navigation & history | Left drawer with new chat, searchable history when supported, grouped recents and conversation actions | Stored conversations, active selection, delete/undo, heartbeat and Interactive UI entries |
| Settings home | Grouped index into Appearance & General, Agent, Models & Providers, Tools & Skills, Integrations, Sandbox | Every present option and its original state handling |
| Models & providers | Consistent connection cards, readable status badges, compact editable details | All providers, free models, fallback, custom endpoint, API key handling |
| Agent | One visual hierarchy for Soul, Memory, Tasks, Heartbeat, Email and SMS | Full editing/toggles/logs and state |
| Tools | Separate clear sections for device capabilities, MCP connections and installed skills | Add/remove/refresh/configure, enabled status and permissions |
| Linux sandbox | Terminal-first workspace with tabs for Files and Packages, stable action bar and clear session/status | Distro/setup, package manager, terminal/files, task execution |
| Interactive UI | Neutral host chrome and navigation frame around generated content | AI-generated cards, user callbacks, session and freeze/resubmit behaviour |
| Feedback | Shared loading, permission, error, success, empty and confirmation patterns | All meaningful system status and cancellation controls |

## Architecture and implementation sequence

Keep Kotlin Multiplatform/Compose and the existing ViewModels/actions. Create reusable visual tokens and primitives in the shared UI package; refactor the present composables rather than replacing business logic.

1. Foundation: semantic shapes, surface levels, typography hierarchy, focus/disabled/error treatment, shared settings cards.
2. App shell: responsive drawer, chat home/header/composer, clear back actions.
3. Chat: content measure, bubbles, message actions, tool/reasoning, generated UI framing.
4. Settings architecture: grouped home, subsection navigation, coherent row/card/editor components.
5. Services and agent: providers/models, Soul/Memory/Tasks/Heartbeat/Email/SMS.
6. Tools/skills/MCP, integrations and Linux workspace.
7. Final polish: motion, empty/loading states, RTL, accessibility, screenshot tests and visual review.

Work in reviewable commits; never present a partial visual pass as the completed whole-app redesign.

## Validation and release

- Each phase: Android debug build + relevant tests and screenshots.
- Smoke-test chat, switching models, attachment/file opening, history, save/delete/undo, permission-gated tools, interactive callbacks, settings export/import and sandbox.
- Preview artifacts must keep a distinct `com.dude555afk.kairon` application ID. Preview's lower-permission flavor is for UI testing only; FOSS features are not dropped from production.
- The UI is ready only when the core screens share the same design system and functionality is retained. The release APK and its signing/update strategy need separate validation.

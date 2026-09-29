# Kairon: calm layered design system

Design direction: the restrained mobile surfaces and grouped settings philosophy
seen in Kelivo. This is an original Kotlin/Compose implementation, not copied
Flutter UI code or artwork. Kairon keeps its own identity and existing features.

## Visual grammar
- Neutral page canvas, contrasting raised cards and a third level for menus.
- Accent is a small navigation signal, not a full-screen gradient. Preserve
  theme presets, dynamic color and OLED black.
- Rounded corners: 14dp cards, 17dp conversation bubbles, 18dp drawer,
  23dp floating composer, 24dp header action capsule.
- 0.6dp hairlines with subdued outlineVariant alpha, 1-3dp elevations on
  detached surfaces only.
- Settings are compact inset groups with readable headings and description rows.
- Assistant content remains editorial and unboxed; user messages have softly
  tinted surfaces. Reasoning and tool surfaces stay separate.
- Welcome screen is quiet and static to avoid needless continuous animation.
- Respect reduced visual density without reducing touch targets or large-text
  layouts. Animate changing content rather than painting constant gradients.

## Implemented in this phase
Theme and global shape tokens; header and composer; history drawer; chat message
spacing/colors; welcome screen; settings cards and overview; dynamic UI cards.
All functionality stays in the existing Compose components.

## Remaining review
Audit each nested service, integration, provider and Kai Build page through
device screenshots. Do not claim the entire application is now pixel-for-pixel
Kelivo. Platform-specific features, link handling, model controls, attachments
and dynamic UI must keep their existing behavior.

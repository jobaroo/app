# App

## Scope
- `DESIGN.md` is the UI design source of truth. Update it in the same change whenever the visual language, layout rules, interaction patterns, or page copy rules change.
- `ui/`: pure Scala <-> DaisyUI vocabulary. Typed CSS tokens, themes, presets, and design primitives. No Tyrian dependency.
- `tyrianui/`: Tyrian integration layer on top of `ui/`. Attr merging, HTML helpers, icons, and Daisy-oriented render adapters belong here.
- `components/`: app-specific building blocks composed from `tyrianui`. This layer may depend on router/session/app messages.
- `pages/`: route screens only. Keep route logic thin and push reusable view composition into `components/`.
- `core/`: router, session, and frontend state orchestration.
- `common/`: frontend HTTP/client helpers and shared constants.

## Rules
- Keep backend contracts unchanged unless explicitly requested.
- Read `DESIGN.md` before making UI changes.
- Prefer changing the visual system in `ui/preset/Jobaroo.scala`, `styles.css`, and shared adapters/components before touching pages.
- Do not leak raw ad-hoc styling into pages when a reusable preset or component belongs in `ui`, `tyrianui`, or `components`.
- Keep page copy customer-facing; do not mention internal implementation details in the UI.
- Keep `DESIGN.md` and the implemented UI in sync.

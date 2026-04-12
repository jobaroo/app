# App

## Scope
- `DESIGN.md` is the UI design source of truth. Keep it concise and update it every time the visual language, layout rules, or interaction patterns change.
- `ui/`: pure typed UI vocabulary, tokens, themes, and presets. No Tyrian dependency.
- `tyrianui/`: Tyrian adapter and rendering helpers built on top of `ui/`.
- `components/`: app-specific building blocks composed from `tyrianui`; may depend on router/session/app messages.
- `pages/`: route-level screens only; keep page logic thin and push reusable UI into `components/`.
- `core/`: router, session, and frontend state orchestration.
- `common/`: frontend HTTP/client helpers and shared constants.

## Rules
- Keep backend contracts unchanged unless explicitly requested.
- Prefer changing the visual system in `ui/preset/Jobaroo.scala`, `styles.css`, and shared components before touching pages.
- Do not leak raw ad-hoc styling into pages when a reusable preset or component belongs in `ui`, `tyrianui`, or `components`.
- Keep `DESIGN.md` and the implemented UI in sync.

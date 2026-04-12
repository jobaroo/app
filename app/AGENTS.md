# App

## Scope
- `DESIGN.md` is the UI design source of truth. Update it in the same change whenever the visual language, layout rules, interaction patterns, or page copy rules change.
- `ui/`: pure Scala <-> DaisyUI vocabulary. Typed CSS tokens, themes, presets, and design primitives. No Tyrian dependency.
- `tyrianui/`: Tyrian integration layer on top of `ui/`. Attr merging, collection-aware HTML helpers, generic icons, and Daisy-oriented render adapters belong here.
- `components/`: app-specific building blocks composed from `tyrianui`. This layer may depend on router/session/app messages.
- `pages/`: route screens only. Keep route logic thin and push reusable view composition into `components/`.
- `core/`: router, session, and frontend state orchestration.
- `common/`: frontend HTTP/client helpers and shared constants.

## Rules
- Run `source "$HOME/.sdkman/bin/sdkman-init.sh" && sdk env` before SBT or Scala.js commands.
- Keep backend contracts unchanged unless explicitly requested.
- Read `DESIGN.md` before making UI changes.
- Prefer changing the visual system in `ui/preset/Jobaroo.scala`, `styles.css`, and shared adapters/components before touching pages.
- Do not leak raw ad-hoc styling into pages when a reusable preset or component belongs in `ui`, `tyrianui`, or `components`.
- Keep Tyrian varargs/child-sequence glue inside `tyrianui`. Pages and app components should compose child collections through shared helpers, not `toSeq*`/manual varargs expansion.
- Keep page copy customer-facing; do not mention internal implementation details in the UI.
- Secondary screens should expose an explicit in-app back affordance through shared components instead of relying on browser chrome alone.
- Do not introduce UI controls that imply unsupported backend behavior. If the contract only supports max salary, the UI must say max salary until the contract changes.
- Keep `DESIGN.md` and the implemented UI in sync.
- Verify meaningful UI changes in a real browser with `playwright-cli`, not only by reading code.
- Save Playwright screenshots inside `.playwright-cli/` and use `snapshot`, `console`, `eval`, and route navigation to inspect the rendered result before finishing.

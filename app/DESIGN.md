# Jobaroo UI Design

## Purpose
- Keep one compact source of truth for the visual system.
- Optimize for fast reuse by humans and LLMs: short rules, no narrative filler.

## Product direction
- Jobaroo should feel like a focused hiring desk, not a playful dashboard.
- The product is warm, sharp, and editorial: black structure, warm yellow accents, ivory canvas, restrained neutrals.
- The UI must feel customer-facing. Do not leak implementation or migration language into page copy.

## Brand frame
- Light theme is primary.
- Core palette: near-black, warm yellow, soft ivory, muted sand.
- Dark theme should keep the same personality: black surfaces, warm contrast, gold actions.
- Primary color is reserved for CTA buttons, active states, badges, and focus accents.

## Typography
- Use `Manrope` for body copy and `Space Grotesk` for headings and brand marks.
- Headings should feel dense and confident.
- Labels and microcopy should stay small, uppercase only when they help scanning.

## Layout rules
- Global shell: compact dark header, light content canvas, dark footer. Do not add a marketing hero to every page by default.
- Jobs page: fixed left filter rail, compact board header, flat white card grid, yellow accents only where they help scanning.
- Jobs page filters must scale: searchable sections, scrollable rail on desktop, no dead controls or placeholder affordances.
- Jobs page controls must map 1:1 to current backend capability. Do not ship dual-range salary, extra sort modes, or fake filter affordances until the contract supports them.
- Detail page: dark title band, fast metadata scan, clear external apply action, white reading surface below.
- Secondary pages must expose a visible back action near the top-left of the main content area. It should prefer in-app history and have a deterministic fallback.
- Auth/profile pages: dark account panel paired with a bright form workspace.
- Auth, recovery, and profile pages must use task-specific copy. Do not reuse the same headline/subtitle block for every account action.
- Post-job page: dark recruiter hero, grouped form editor on the left, sticky candidate preview rail on the right with preview, listing signals, and checklist.

## Surface language
- Prefer flat-to-soft cards over glassy gradients.
- Use compact rounded corners, thin borders, and shallow shadows.
- White content cards should sit on a clean white canvas with light neutral separators.
- Dark header/footer and recruiter hero surfaces carry the strongest contrast on the page.

## Component rules
- Job cards are dense and comparison-friendly:
  - company
  - title
  - short summary
  - location
  - compensation
  - remote/on-site signal
  - tags
  - apply action
- Filter groups should stay compact and scannable.
- Detail pages should not repeat company, title, summary, compensation, or location across multiple sections.
- Buttons should feel compact, bold, and obvious.
- Form controls must have visible boundaries; do not let fields disappear into white cards.
- Frontend validation should use native browser validity plus DaisyUI validator styling for URLs, emails, numbers, and required fields.
- Icons should be simple inline SVG marks, never placeholder text tokens.

## Interaction
- Keep motion subtle: small lift, border emphasis, shadow change.
- Hover, focus, active, and disabled states must be visually distinct.
- Theme toggle should preserve the same design language, not switch to a different product personality.
- Preview rails should mirror the candidate-facing surface closely enough to be trusted, but should not duplicate entire pages inside the editor.

## Copy
- Use customer-facing language.
- Avoid internal engineering terms in the UI.
- Prefer direct, calm, product-oriented phrasing.

## Accessibility
- Keep contrast strong enough in both themes.
- Preserve visible focus states.
- Do not rely on color alone for status or readiness.

## Change management
- Update this file in the same change whenever the visual language, layout rules, copy rules, or component behavior changes.
- Keep this document shorter than the implementation it governs.
- If code and `DESIGN.md` disagree, bring them back into sync immediately.

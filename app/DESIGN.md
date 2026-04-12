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
- Use a clean body face and a compact display face.
- Headings should feel dense and confident.
- Labels and microcopy should stay small, uppercase only when they help scanning.

## Layout rules
- Global shell: compact dark header, large dark hero, light content canvas, dark footer.
- Jobs page: left filter rail, dominant results column, flat white card stack.
- Detail page: strong title block, fast metadata scan, clear external apply action.
- Auth/profile pages: dark marketing panel paired with a bright form workspace.
- Post-job page: form workspace on the left, sticky candidate preview rail on the right.

## Surface language
- Prefer flat-to-soft cards over glassy gradients.
- Use large rounded corners, thin borders, and shallow shadows.
- White content cards should sit on a warm ivory background.
- Dark hero/header/footer surfaces carry the strongest contrast on the page.

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
- Buttons should feel compact, bold, and obvious.
- Form controls must have visible boundaries; do not let fields disappear into white cards.
- Icons should be simple inline SVG marks, never placeholder text tokens.

## Interaction
- Keep motion subtle: small lift, border emphasis, shadow change.
- Hover, focus, active, and disabled states must be visually distinct.
- Theme toggle should preserve the same design language, not switch to a different product personality.

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

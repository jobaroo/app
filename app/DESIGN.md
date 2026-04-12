# Jobaroo UI design

## Purpose
- Provide one compact source of truth for the app's visual language.
- Optimize for reuse by humans and LLMs: short sections, explicit rules, minimal prose.

## Product direction
- Build a hiring product that feels calm, sharp, and fast.
- Keep the visual language aligned with the logo: black, warm yellow, white, and restrained neutrals.
- Prefer clarity over ornament; use contrast, spacing, and hierarchy before adding effects.

## Design principles
- Make search and scanning effortless.
- Keep surfaces flat-to-soft, not glossy or futuristic.
- Use strong structure: clear header, obvious filters, dense but readable job cards.
- Make forms feel trustworthy and low-friction.
- Preserve functional behavior while improving presentation.

## Theme
- Brand colors: black, white, `#f5b800`-adjacent yellow.
- Light theme is primary; dark theme should feel like the same product, not a different one.
- Accent color is for primary actions, selection, and focus, not for decoration everywhere.

## Typography
- Use one readable body family and one more expressive heading family at most.
- Headings should feel confident and compact.
- Body copy should stay plain, fast to scan, and never oversized.

## Layout
- Header should be stable, compact, and brand-led.
- Job list should prioritize scan speed: filters on the side, results dominant, cards easy to compare.
- Cards should expose title, company, location, salary, seniority, and tags without visual clutter.
- Forms should separate composition from preview and keep submit intent visible.

## Components
- Shared visual rules belong in `ui/` presets and typed primitives.
- App-shaped building blocks belong in `components/`.
- Pages should compose existing building blocks instead of inventing new styling locally.

## Interaction
- Hover, focus, and selection states must be obvious and consistent.
- Primary actions should always stand out from secondary actions.
- Filters, toggles, and form states should communicate change immediately.

## Accessibility
- Preserve contrast in both themes.
- Keep focus states visible.
- Avoid using color as the only status signal.

## Change management
- Update this file whenever the design system, page composition rules, or visual priorities change.
- Keep entries compact and directive; this file should remain easy to diff and cheap to load into an LLM context.

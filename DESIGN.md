---
name: SpaceMarket
description: A quiet personal desk for one person's whole client pipeline.
colors:
  paper: "#f7f6f2"
  panel: "#ffffff"
  panel-muted: "#f1f0eb"
  ink: "#1c2321"
  ink-soft: "#4a5552"
  hairline: "#cfd3cc"
  outline: "#7a8480"
  teal: "#1f6f66"
  teal-wash: "#cfe8e2"
  teal-deep: "#0b3b35"
  amber-wash: "#f6e3c3"
  error: "#ba1a1a"
typography:
  headline:
    fontFamily: "Hanken Grotesk, system-ui, sans-serif"
    fontSize: "1.75rem"
    fontWeight: 700
    lineHeight: 1.2
    letterSpacing: "-0.02em"
  title:
    fontFamily: "Hanken Grotesk, system-ui, sans-serif"
    fontSize: "1.125rem"
    fontWeight: 600
    lineHeight: 1.4
  body:
    fontFamily: "Hanken Grotesk, system-ui, sans-serif"
    fontSize: "1rem"
    fontWeight: 400
    lineHeight: 1.5
  label:
    fontFamily: "Hanken Grotesk, system-ui, sans-serif"
    fontSize: "0.875rem"
    fontWeight: 500
    lineHeight: 1.4
rounded:
  sm: "4px"
  md: "8px"
spacing:
  xs: "4px"
  sm: "8px"
  md: "16px"
  lg: "24px"
  xl: "32px"
components:
  button-primary:
    backgroundColor: "{colors.teal}"
    textColor: "{colors.panel}"
    rounded: "{rounded.md}"
  panel:
    backgroundColor: "{colors.panel}"
    rounded: "{rounded.md}"
    padding: "16px 24px 24px"
  nav-item-active:
    backgroundColor: "{colors.teal-wash}"
    textColor: "{colors.teal-deep}"
---

# Design System: SpaceMarket

## Overview

**Creative North Star: "The Quiet Desk"**

SpaceMarket is a desk for one person: warm paper, clear ink, one pen colour. A freelancer opens it between client calls and should see where their pipeline stands without setup, roles or team vocabulary. The interface is calm because it is sparing, not because it is empty.

Familiarity wins over expression. Material components and a standard top bar plus side navigation are kept; the identity lives in the palette, a single type family, hairline borders and restrained use of the accent.

**Key Characteristics:**
- Warm neutral surfaces, one deep teal accent.
- One family (Hanken Grotesk) for headings, labels and data.
- Flat by default: hairline borders instead of shadows.
- Plain-language empty states that tell you the next action.

## Colors

A warm paper neutral carries every surface. Teal is the only chromatic voice.

### Primary
- **Desk Teal** (#1f6f66): primary buttons, links, the active navigation item, focus ring. White text on it is 6:1.
- **Teal Wash** (#cfe8e2): selected and active backgrounds, step markers.
- **Deep Teal** (#0b3b35): text on Teal Wash, link hover.

### Secondary
- **Amber Wash** (#f6e3c3): reserved for the tertiary container role; use only for rare warnings or highlights.

### Neutral
- **Paper** (#f7f6f2): page background.
- **Panel White** (#ffffff): content panels.
- **Side Paper** (#f1f0eb): sidebar and secondary layer.
- **Ink** (#1c2321): body text, tinted toward the accent hue, never pure black.
- **Soft Ink** (#4a5552): secondary text, 7:1 on Paper.
- **Hairline** (#cfd3cc): dividers and panel borders.
- **Outline** (#7a8480): input borders, 3:1 minimum.

### Named Rules
**The One Pen Rule.** Teal marks actions, current location and state only. It is never decoration.

## Typography

**Display / Body / Label Font:** Hanken Grotesk (with system-ui fallback), loaded in `index.html`.

**Character:** a friendly, slightly humanist grotesque that stays out of the way of data.

### Hierarchy
- **Headline** (700, 1.75rem, 1.2, -0.02em): page title, one per screen.
- **Title** (600, 1.125rem, 1.4): panel headings, row emphasis.
- **Body** (400, 1rem, 1.5): default text; prose capped near 65–75ch.
- **Label** (500, 0.875rem, 1.4): secondary text, links in panel headers, buttons.

### Named Rules
**The Tabular Numbers Rule.** Any figure that can be compared (values, counts) uses `.tabular`.

## Layout

App shell: top toolbar, fixed 15rem side navigation on a secondary neutral layer, content area scrolling inside the remaining height. Page content has a 72rem max width with 24px gutters (16px under 960px). Spacing runs on a 4/8/12/16/24/32/48px scale (`--space-1` to `--space-7`). Two-column page layouts (3fr / 2fr) collapse to one column under 960px.

## Elevation & Depth

Flat by default. Panels sit on Paper with a 1px Hairline border; no shadows at rest. Material overlays (menus, dialogs, snackbars) keep their standard elevation.

## Shapes

Gently curved: 8px on panels and skeletons, Material's own radii on controls, 50% only for the numbered step markers.

## Components

### Buttons
- **Primary:** Material flat button in Desk Teal with white label, one per view.
- **Secondary:** Material stroked button for the second action (e.g. "Add a contact" in a small empty state).
- Links inside panel headers are plain text links, not buttons.

### Panels
White, 8px radius, hairline border, padding 16px 24px 24px. Never nested.

### Lists
Rows separated by a 1px Hairline top border, no card per row. Values right-aligned, tabular.

### Navigation
`mat-nav-list` of real links with an icon and label. The active route is `activated` with Teal Wash and carries `aria-current="page"`.

### Empty and loading states
Empty states say what is missing and offer the one action that fixes it. Loading uses skeleton blocks that stop animating under `prefers-reduced-motion`.

## Do's and Don'ts

### Do:
- **Do** override Material through `--mat-sys-*` tokens in `src/styles.css`, not per-component CSS.
- **Do** keep Bootstrap to utility classes, bridged by `--bs-*` tokens.
- **Do** write empty states that name the next action.

### Don't:
- **Don't** use teal as decoration or on inactive states.
- **Don't** add a colored side-stripe border to rows or alerts.
- **Don't** nest panels or turn every row into a card.
- **Don't** introduce a second font family.

---
target: dashboard
total_score: 13
max_score: 40
na_heuristics: 
p0_count: 1
p1_count: 2
target_identity: "file:/Users/abi/IdeaProjects/SpaeMarket/front/src/pages/dashboard/dashboard.component.html"
target_fingerprint: "sha256:f3f3beccfe3065bc40a8d2ba9d003cc08a2db9b63072377cf4116e3ce6d916e9"
target_path: /Users/abi/IdeaProjects/SpaeMarket/front/src/pages/dashboard/dashboard.component.html
timestamp: 2026-10-05T19-56-45Z
slug: front-src-pages-dashboard-dashboard-component-html
---
Method: dual-agent. Target: dashboard (front/src/pages/dashboard) + shell.
Score 13/40 (Poor). Dashboard is a placeholder stub ("dashboard works!").
Scores: H1 1, H2 2, H3 2, H4 1, H5 2, H6 2, H7 0, H8 2, H9 1, H10 0.
Detector: overused-font (Roboto) at front/src/styles.css:4.
Priority issues:
- [P0] Dashboard is a stub and is the guard redirect target; login goes to /contacts. Fix: define and build "today" view (shape).
- [P1] Sidebar /documents -> 404 (route /app-document), no active state, buttons not links (harden, clarify).
- [P1] No design system: 13-line styles.css, Material+Bootstrap overlap, hard-coded #43a047 (document, typeset, colorize).
- [P2] Header: 3 equal raised buttons; use account menu, search, quick-add (distill, layout).
- [P3] No help/empty/first-run states (onboard).
Personas: Alex (no shortcuts/search), Sam (nav as buttons, no aria-current, snackbar ~3.3:1), Jordan (placeholder, 404 Documents).

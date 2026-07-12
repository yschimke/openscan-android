# Design parity

[design-parity](https://github.com/yschimke/design-parity) proves a UI change is
at parity with its intended design: it renders the Compose code (the
*candidate*), diffs it against a committed design *reference*, and emits a
verdict + a self-contained HTML comparison page (reference | candidate | diff).

This repo adopts it for the **scanner list** (`ScannerListBody`) and **scanner
detail** (`ScannerDetailBody`) screens, each in both **light** and **dark**
themes. All render on the CMP desktop backend from `:openscan-components`
`commonMain`.

## What's committed

| Path | Role |
| --- | --- |
| `design/ScannerList.{light,dark}.html` | The scanner-list references (`ScannerListBodyPreview` / `ScannerListBodyDarkPreview`). |
| `design/ScannerDetail.{light,dark}.html` | The scanner-detail references (`ScannerDetailBodyPreview` / `ScannerDetailBodyDarkPreview`); the dark one shows the scanning state. |
| `design/openscan.tokens.json` | The OpenScan design-system tokens as a committed [W3C DTCG](https://tr.designtokens.org/) document. Mirrors `openscan-components/.../theme/OpenScanTokens.kt`. |
| `design-map.json` | Correspondence: each preview-function code handle ↔ its reference + `previewId` + `tokensFile`. |
| `.design-parity.json` | Parity direction. **`code-led`** (advisory) for now — flip to `design-led` once thresholds are calibrated. |

The reference PNGs are **generated from the HTML, not committed** (they would
drift from it) — gitignored and rendered on demand. The current renders, plus
the candidate bundle and `report.html` triptychs, live on the
**`design-parity/main`** branch, regenerated on every push to `main` by
`.github/workflows/design-parity.yml`.

## Render path

Parity renders on the **CMP desktop (Skiko) backend**. The presentational bodies
(and their theme, `Dimens`, and the parity `@Preview`s) live in
`:openscan-components` `commonMain` with a `jvm("desktop")` target, so the
candidate renders off-Android — no Robolectric, no emulator. The stateful
screens (polling, WebView, DataStore) stay in `:app`; only the pure
presentational subtree is shared.

## Scaling further

Each new screen is one preview + one HTML reference + one `design-map.json`
entry. Keep subjects **static and deterministic** (sample state — no live
transport / clock / network), and add one variant at a time so
candidate↔reference *pairing* is never debugged alongside content drift.

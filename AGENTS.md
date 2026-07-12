# Agent notes

- Project layout: `app/` (Android application) plus the KMP library modules
  `openscan-core`, `openscan-components`, and `openscan-mobile`. See
  `docs/ARCHITECTURE.md`.
- Build: `./gradlew :app:assembleDebug` for a debug APK;
  `./gradlew test lintDebug ktfmtCheck` mirrors what CI runs.
- Formatting: ktfmt (Google style). Fix with `./gradlew ktfmtFormat`. Enforced
  by CI and the `.githooks/pre-commit` hook.
- JDK: 21 (configured via `gradle/gradle-daemon-jvm.properties`; do not pin a
  specific vendor).
- Cloud / remote agent sandboxes need an Android SDK — see `.claude/CLAUDE.md`
  for the `SessionStart` hook that installs one on demand.
- Keep PR titles and commits in conventional-commits form (`feat:`, `fix:`,
  `chore:`, …); enforced by `.github/workflows/pr-title.yml` +
  `.githooks/commit-msg`, and consumed by release-please.
- Shared UI must stay **stateless** in `:openscan-components` (it renders on the
  desktop backend for design-parity). Keep transport, polling, WebView and
  DataStore in `:app`.
- Device features that aren't cleanly covered by the OpenScan3 REST API should
  route through the in-app web fallback (`WebFallbackScreen`) rather than a
  half-built native screen.

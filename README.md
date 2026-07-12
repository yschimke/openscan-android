# OpenScan mobile

A mobile client for [OpenScan](https://openscan.eu) 3D scanners. Register your
OpenScan devices, find them on the network, configure and drive scans, watch a
live camera preview, and fall back to the device's own web UI for anything the
native app doesn't cover yet.

A Kotlin Multiplatform project targeting **Android** (with a Desktop/JVM target
used only to render shared UI for design-parity). Modelled on
[meshcore-mobile](https://github.com/yschimke/meshcore-mobile).

## Features

- **Register** one or more OpenScan scanners by address (`openscan.local`, an IP, …).
- **Detect** devices on your Wi-Fi automatically via mDNS/DNS-SD.
- **Configure** a scan — points, rotations — and jog the turntable.
- **Preview** the live camera feed.
- **Scan** — start, watch progress, and stop.
- **Fall back to web** — open the device's full web interface in-app for
  anything not (yet) native.

## Modules

| Module | Role |
| --- | --- |
| `:openscan-core` | KMP domain: models, the Ktor OpenScan API client, discovery interface, repository, per-device manager. |
| `:openscan-components` | Compose Multiplatform: shared stateless UI + the OpenScan Material 3 theme. |
| `:openscan-mobile` | Android integration: mDNS discovery via `NsdManager`. |
| `:app` | The Android app: navigation, DI, DataStore, stateful screens, WebView fallback. |

See [`docs/ARCHITECTURE.md`](./docs/ARCHITECTURE.md) for the full picture.

## Build & run

```shell
./gradlew :app:assembleDebug        # debug APK
./gradlew test lintDebug ktfmtCheck # what CI runs
```

JDK 21 is required (configured via `gradle/gradle-daemon-jvm.properties`). Cloud
/ remote agent sandboxes install the Android SDK on demand — see
[`.claude/CLAUDE.md`](./.claude/CLAUDE.md).

## Device API

The client talks to the [OpenScan3](https://github.com/OpenScan-org/OpenScan3)
firmware REST API under `/api/latest`. The web fallback covers anything the
native surface doesn't implement, so the native client can stay focused.

## Tooling

- **CI** (`.github/workflows/ci.yml`) — assemble, unit tests, Android lint.
- **ktfmt** — Google-style Kotlin formatting, enforced in CI and via the
  `.githooks/pre-commit` hook.
- **Compose Preview** — renders `@Preview`s and posts them to PRs.
- **Design parity** — see [`docs/design-parity.md`](./docs/design-parity.md).
- **Design artifacts** — publishes the design catalog (`catalog.spec.json`).
- **Release Please + Play Publisher** — conventional-commit releases build and
  publish to the Play internal track.

## Design parity

UI screens are checked against their design references with
[design-parity](https://github.com/yschimke/design-parity). The design intent is
committed as HTML under [`design/`](./design). The rendered artifacts are
regenerated on every push to `main` and published to the **`design-parity/main`**
branch.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html).

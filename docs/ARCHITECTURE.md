# Architecture

OpenScan mobile is a Kotlin Multiplatform project targeting Android (with a
Desktop/JVM target used only for rendering shared UI in design-parity). It
follows the same layering as
[meshcore-mobile](https://github.com/yschimke/meshcore-mobile): a KMP core, a
Compose Multiplatform component library, an Android integration module, and the
Android app.

## Modules

| Module | Plugin | Targets | Responsibility |
| --- | --- | --- | --- |
| `:openscan-core` | KMP library | android, jvm | Domain models, the typed OpenScan device API (Ktor), mDNS discovery interface, the scanner repository + persistence port, and the per-device `OpenScanManager`. No UI, no Android APIs. |
| `:openscan-components` | KMP + Compose | android, desktop | The shared, **stateless** presentational composables (`ScannerListBody`, `ScannerDetailBody`, `AddScannerBody`, cards) + the OpenScan Material 3 theme. Renders on the CMP desktop backend for design-parity. |
| `:openscan-mobile` | KMP Android library | android | Android integration: `NsdScannerDiscovery` (mDNS via `NsdManager`) wired to the shared `ScannerDiscovery` interface. |
| `:app` | Android application | android | `MainActivity` (navigation3), the `AppGraph` (manual DI), DataStore persistence, the stateful screens that bind live data to the shared bodies, and the WebView fallback. |

## Data flow

```
NsdScannerDiscovery ─┐
                     ├─▶ ScannerRepository ◀─ DataStoreScannerStore
OpenScanHttpApi ─▶ OpenScanManager ─▶ ScannerState (StateFlow)
                     │
   Compose screens (:app)  ─▶  stateless bodies (:openscan-components)
```

- **Register / detect** — `ScannerRepository` owns the saved scanners;
  `NsdScannerDiscovery` surfaces devices advertising `_openscan._tcp` /
  `_http._tcp` on the LAN. The list screen merges both.
- **Configure / preview / scan** — `OpenScanManager` wraps one device's
  `OpenScanApi`, polls status + camera frames, and exposes a single
  `ScannerState`. The detail screen drives the polling loop and renders it.
- **Fallback to web** — anything the native client doesn't implement is one tap
  away in `WebFallbackScreen` (an in-app `WebView` onto the device's own UI), so
  the native surface can stay small while the firmware API matures.

## Device API

The client speaks the OpenScan3 firmware REST API under `/api/latest` (see
`openscan-core/.../api/OpenScanEndpoints.kt`). The JSON codec is deliberately
lenient (`OpenScanJson`) because the firmware is a moving target. Prefer routing
uncertain features through the web fallback over guessing an endpoint.

## Dependency injection

`AppGraph` is a plain, hand-written object graph (no annotation-processor DI):
dependencies are explicit and constructable in tests. It's provided to
composables once at the activity root via `LocalAppGraph`.

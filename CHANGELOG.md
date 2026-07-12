# Changelog

All notable changes to this project are documented here. The format is based on
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html). Releases are cut
by [release-please](https://github.com/googleapis/release-please) from
conventional-commit history.

## [0.1.1](https://github.com/yschimke/openscan-android/compare/v0.1.0...v0.1.1) (2026-07-12)


### Features

* scaffold OpenScan mobile client (KMP core, CMP UI, Android app) ([6b08ffb](https://github.com/yschimke/openscan-android/commit/6b08ffb82df852bca2ee0dcad093c511a2dfd7c0))


### Bug Fixes

* pin compile/target SDK to 36 to match the installed platform ([4db7658](https://github.com/yschimke/openscan-android/commit/4db76583814f697ae4f3957c842f8391f7f3747e))
* target compileSdk 36 with androidx-core 1.16.0 ([b6efe52](https://github.com/yschimke/openscan-android/commit/b6efe5243e8630770f94f1e0ae38ad55971c24c7))

## 0.1.0 (unreleased)

Initial scaffold of the OpenScan mobile client:

- Kotlin Multiplatform architecture — `:openscan-core`, `:openscan-components`,
  `:openscan-mobile`, `:app`.
- Register scanners by address and auto-detect them on the network (mDNS).
- Configure and run scans, live camera preview, turntable jog.
- In-app web fallback onto the device's own UI.
- Tooling: CI, ktfmt, Compose Preview, design-parity, design-artifacts,
  Release Please + Play Publisher.

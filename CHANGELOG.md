# Changelog

All notable changes to this project are documented here. The format is based on
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html). Releases are cut
by [release-please](https://github.com/googleapis/release-please) from
conventional-commit history.

## 0.1.0 (unreleased)

Initial scaffold of the OpenScan mobile client:

- Kotlin Multiplatform architecture — `:openscan-core`, `:openscan-components`,
  `:openscan-mobile`, `:app`.
- Register scanners by address and auto-detect them on the network (mDNS).
- Configure and run scans, live camera preview, turntable jog.
- In-app web fallback onto the device's own UI.
- Tooling: CI, ktfmt, Compose Preview, design-parity, design-artifacts,
  Release Please + Play Publisher.

package ee.schimke.openscan.core.discovery

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** A scanner found on the local network by mDNS/DNS-SD before the user has registered it. */
data class DiscoveredScanner(
  val name: String,
  val host: String,
  val port: Int,
) {
  val key: String
    get() = "$host:$port"
}

/**
 * Discovers OpenScan devices on the local network. The interface lives in commonMain so screens and
 * the repository depend only on it; the real mDNS implementation is platform-specific
 * (`ee.schimke.openscan.mobile.NsdScannerDiscovery` on Android via `NsdManager`). Desktop/tests use
 * the [Noop] below or a fake.
 *
 * Implementations emit the current set of live devices; entries appear as services resolve and
 * disappear when they go away.
 */
interface ScannerDiscovery {
  val discovered: Flow<List<DiscoveredScanner>>

  /** Begin browsing. Safe to call repeatedly; idempotent while already running. */
  fun start()

  /** Stop browsing and release platform resources. */
  fun stop()

  /** A discovery that never finds anything — used on platforms without mDNS and in previews. */
  object Noop : ScannerDiscovery {
    private val empty = MutableStateFlow<List<DiscoveredScanner>>(emptyList())
    override val discovered: Flow<List<DiscoveredScanner>> = empty.asStateFlow()

    override fun start() = Unit

    override fun stop() = Unit
  }
}

package ee.schimke.openscan.mobile

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import ee.schimke.openscan.core.discovery.DiscoveredScanner
import ee.schimke.openscan.core.discovery.ScannerDiscovery
import java.util.ArrayDeque
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Discovers OpenScan devices on the local network with Android's [NsdManager]. Browses the OpenScan
 * DNS-SD type and the generic `_http._tcp` type (the firmware advertises its web UI there), resolves
 * each hit to a host/port and publishes the live set via [discovered].
 *
 * NsdManager forbids concurrent `resolveService` calls, so resolves are serialised through a small
 * queue. Call [start]/[stop] to bracket browsing to the screen lifecycle.
 */
class NsdScannerDiscovery(context: Context) : ScannerDiscovery {

  private val nsd = context.applicationContext.getSystemService(Context.NSD_SERVICE) as NsdManager

  private val _discovered = MutableStateFlow<List<DiscoveredScanner>>(emptyList())
  override val discovered: Flow<List<DiscoveredScanner>> = _discovered.asStateFlow()

  private val listeners = mutableListOf<NsdManager.DiscoveryListener>()
  private var running = false

  // Serialise resolves: NsdManager rejects a resolve while another is in flight.
  private val resolveQueue = ArrayDeque<NsdServiceInfo>()
  private var resolving = false

  override fun start() {
    if (running) return
    running = true
    SERVICE_TYPES.forEach { type -> browse(type) }
  }

  override fun stop() {
    running = false
    listeners.forEach { runCatching { nsd.stopServiceDiscovery(it) } }
    listeners.clear()
    synchronized(resolveQueue) {
      resolveQueue.clear()
      resolving = false
    }
    _discovered.value = emptyList()
  }

  private fun browse(serviceType: String) {
    val listener =
      object : NsdManager.DiscoveryListener {
        override fun onStartDiscoveryFailed(type: String, errorCode: Int) {}

        override fun onStopDiscoveryFailed(type: String, errorCode: Int) {}

        override fun onDiscoveryStarted(type: String) {}

        override fun onDiscoveryStopped(type: String) {}

        override fun onServiceFound(info: NsdServiceInfo) {
          enqueueResolve(info)
        }

        override fun onServiceLost(info: NsdServiceInfo) {
          _discovered.update { list -> list.filterNot { it.name == info.serviceName } }
        }
      }
    listeners += listener
    runCatching { nsd.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, listener) }
  }

  private fun enqueueResolve(info: NsdServiceInfo) {
    synchronized(resolveQueue) {
      resolveQueue.add(info)
      if (!resolving) pumpResolve()
    }
  }

  private fun pumpResolve() {
    val next = synchronized(resolveQueue) { resolveQueue.poll()?.also { resolving = true } }
    if (next == null) {
      synchronized(resolveQueue) { resolving = false }
      return
    }
    val resolveListener =
      object : NsdManager.ResolveListener {
        override fun onResolveFailed(info: NsdServiceInfo, errorCode: Int) {
          pumpResolve()
        }

        @Suppress("DEPRECATION")
        override fun onServiceResolved(info: NsdServiceInfo) {
          val host = info.host?.hostAddress
          if (host != null) {
            val entry = DiscoveredScanner(name = info.serviceName ?: host, host = host, port = info.port)
            _discovered.update { list -> (list.filterNot { it.key == entry.key } + entry) }
          }
          pumpResolve()
        }
      }
    runCatching { nsd.resolveService(next, resolveListener) }.onFailure { pumpResolve() }
  }

  private companion object {
    val SERVICE_TYPES = listOf("_openscan._tcp.", "_http._tcp.")
  }
}

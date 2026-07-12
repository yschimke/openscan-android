package ee.schimke.openscan.core.manager

import ee.schimke.openscan.core.api.OpenScanApi
import ee.schimke.openscan.core.api.OpenScanException
import ee.schimke.openscan.core.model.CameraInfo
import ee.schimke.openscan.core.model.DeviceInfo
import ee.schimke.openscan.core.model.Scanner
import ee.schimke.openscan.core.model.ScannerReachability
import ee.schimke.openscan.core.model.ScanSettings
import ee.schimke.openscan.core.model.ScanStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Everything the UI needs to know about one connected scanner, refreshed from the device. A pure
 * data snapshot: the app layer drives polling and feeds results in via [OpenScanManager].
 */
data class ScannerState(
  val scanner: Scanner,
  val reachability: ScannerReachability = ScannerReachability.UNKNOWN,
  val device: DeviceInfo? = null,
  val cameras: List<CameraInfo> = emptyList(),
  val scan: ScanStatus = ScanStatus(),
  val error: String? = null,
)

/**
 * Holds the live [ScannerState] for the currently selected device and performs the API calls that
 * mutate it. Platform-agnostic and free of any coroutine scheduling — the caller (the Android
 * `AppGraph`) owns the polling loop and calls [refresh] on a cadence.
 */
class OpenScanManager(private val api: OpenScanApi, scanner: Scanner) {

  private val _state = MutableStateFlow(ScannerState(scanner))
  val state: StateFlow<ScannerState> = _state.asStateFlow()

  /** Pull device identity + cameras + scan status in one pass. Never throws — errors land in state. */
  suspend fun refresh() {
    try {
      val device = api.device()
      val cameras = runCatching { api.cameras() }.getOrDefault(emptyList())
      val scan = runCatching { api.scanStatus() }.getOrDefault(_state.value.scan)
      _state.value =
        _state.value.copy(
          reachability = ScannerReachability.ONLINE,
          device = device,
          cameras = cameras,
          scan = scan,
          error = null,
        )
    } catch (e: OpenScanException) {
      _state.value =
        _state.value.copy(reachability = ScannerReachability.OFFLINE, error = e.message)
    }
  }

  suspend fun startScan(settings: ScanSettings) {
    runOp { _state.value = _state.value.copy(scan = api.startScan(settings)) }
  }

  suspend fun stopScan() {
    runOp { _state.value = _state.value.copy(scan = api.stopScan()) }
  }

  suspend fun rotate(motor: String, degrees: Double) {
    runOp { api.rotate(motor, degrees) }
  }

  suspend fun previewFrame(camera: String): ByteArray? =
    runCatching { api.previewFrame(camera) }.getOrNull()

  private inline fun runOp(block: () -> Unit) {
    try {
      block()
    } catch (e: OpenScanException) {
      _state.value = _state.value.copy(error = e.message)
    }
  }
}

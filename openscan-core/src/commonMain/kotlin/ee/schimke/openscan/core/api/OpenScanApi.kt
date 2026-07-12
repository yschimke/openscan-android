package ee.schimke.openscan.core.api

import ee.schimke.openscan.core.model.CameraInfo
import ee.schimke.openscan.core.model.DeviceInfo
import ee.schimke.openscan.core.model.LightState
import ee.schimke.openscan.core.model.ScanProject
import ee.schimke.openscan.core.model.ScanSettings
import ee.schimke.openscan.core.model.ScanStatus

/**
 * The typed OpenScan device API used by the app. One instance is bound to a single device base URL
 * (see [OpenScanHttpApi]). All calls are suspending and may throw [OpenScanException] on transport
 * or protocol failure — callers decide whether to surface the error or fall back to the web UI.
 */
interface OpenScanApi {
  suspend fun device(): DeviceInfo

  suspend fun cameras(): List<CameraInfo>

  /** Raw JPEG bytes of the current preview frame for [camera]. */
  suspend fun previewFrame(camera: String): ByteArray

  suspend fun lights(): List<LightState>

  suspend fun setLight(name: String, on: Boolean, brightness: Int): LightState

  suspend fun rotate(motor: String, degrees: Double)

  suspend fun scanStatus(): ScanStatus

  suspend fun startScan(settings: ScanSettings): ScanStatus

  suspend fun stopScan(): ScanStatus

  suspend fun projects(): List<ScanProject>
}

/** Wraps any failure talking to a device so callers can catch a single type. */
class OpenScanException(message: String, cause: Throwable? = null) : Exception(message, cause)

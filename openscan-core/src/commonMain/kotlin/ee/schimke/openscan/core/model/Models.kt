package ee.schimke.openscan.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A stable, user-facing identity for a registered scanner. Derived from the host the user entered
 * (or the mDNS instance name) so re-adding the same device reuses its saved config instead of
 * duplicating it.
 */
@JvmInline @Serializable value class ScannerId(val value: String)

/**
 * A scanner the user has registered in the app. Persisted verbatim (see
 * [ee.schimke.openscan.core.repository.ScannerStore]); everything volatile (status, cameras, scan
 * progress) is fetched live from the device and never stored here.
 */
@Serializable
data class Scanner(
  val id: ScannerId,
  /** Display name; defaults to [host] until the device reports its own. */
  val name: String,
  /** Hostname or IP, e.g. `openscan.local` or `192.168.1.42`. No scheme, no port. */
  val host: String,
  /** OpenScan3 serves the API and web UI on 80 by default; the dev image uses 8000. */
  val port: Int = 80,
  /** `true` once the user has confirmed this device, `false` for a discovered-but-unsaved entry. */
  val favorite: Boolean = false,
) {
  /** Base URL of the device web server, e.g. `http://openscan.local`. */
  val baseUrl: String
    get() = if (port == 80) "http://$host" else "http://$host:$port"

  companion object {
    /** Normalise a user-entered host into a stable [ScannerId]. */
    fun idFor(host: String, port: Int): ScannerId =
      ScannerId(if (port == 80) host.lowercase() else "${host.lowercase()}:$port")
  }
}

/** Reachability of a registered scanner, resolved by polling [DeviceInfo]. */
enum class ScannerReachability {
  UNKNOWN,
  ONLINE,
  OFFLINE,
}

/**
 * Firmware/device identity reported by OpenScan3 `GET /api/latest/device`. Only the fields the
 * client renders are modelled; unknown JSON keys are ignored (see the lenient Json in
 * [ee.schimke.openscan.core.api.OpenScanJson]).
 */
@Serializable
data class DeviceInfo(
  val name: String = "OpenScan",
  val model: String = "",
  @SerialName("firmware_version") val firmwareVersion: String = "",
  @SerialName("api_version") val apiVersion: String = "",
)

/** A camera exposed by the device (`GET /api/latest/cameras`). */
@Serializable
data class CameraInfo(
  val name: String,
  val type: String = "",
  @SerialName("is_ready") val isReady: Boolean = true,
)

/** Motor / turntable position (`GET /api/latest/motors/{motor}`). */
@Serializable
data class MotorState(
  val name: String,
  @SerialName("angle") val angleDegrees: Double = 0.0,
)

/** A controllable light ring / LED (`GET /api/latest/lights`). */
@Serializable
data class LightState(val name: String, val on: Boolean = false, val brightness: Int = 0)

/**
 * The settings that define how a scan is captured. Sent to the device when a scan is started; also
 * used as the local form model on the configure screen.
 */
@Serializable
data class ScanSettings(
  /** Number of photos around the turntable (rotational steps). */
  @SerialName("points") val points: Int = 40,
  /** Number of vertical arm positions per rotation. */
  @SerialName("rotations") val rotations: Int = 1,
  @SerialName("camera") val camera: String = "",
  /** Optional project name; the device generates one when blank. */
  @SerialName("project") val project: String = "",
)

/** Lifecycle of a scan on the device. */
enum class ScanPhase {
  IDLE,
  INITIALIZING,
  CAPTURING,
  PROCESSING,
  DONE,
  ERROR,
}

/**
 * Live status of the current (or most recent) scan, from `GET /api/latest/scan/status`. [captured]
 * / [total] drive the progress bar.
 */
@Serializable
data class ScanStatus(
  @SerialName("phase") val phaseRaw: String = "idle",
  @SerialName("captured") val captured: Int = 0,
  @SerialName("total") val total: Int = 0,
  @SerialName("project") val project: String = "",
  @SerialName("message") val message: String = "",
) {
  val phase: ScanPhase
    get() =
      when (phaseRaw.lowercase()) {
        "idle" -> ScanPhase.IDLE
        "initializing",
        "init" -> ScanPhase.INITIALIZING
        "capturing",
        "scanning",
        "running" -> ScanPhase.CAPTURING
        "processing" -> ScanPhase.PROCESSING
        "done",
        "finished",
        "complete" -> ScanPhase.DONE
        "error",
        "failed" -> ScanPhase.ERROR
        else -> ScanPhase.IDLE
      }

  val fraction: Float
    get() = if (total <= 0) 0f else (captured.toFloat() / total).coerceIn(0f, 1f)

  val isActive: Boolean
    get() = phase == ScanPhase.INITIALIZING || phase == ScanPhase.CAPTURING || phase == ScanPhase.PROCESSING
}

/** A completed capture set on the device (`GET /api/latest/projects`). */
@Serializable
data class ScanProject(
  val name: String,
  @SerialName("photos") val photoCount: Int = 0,
  @SerialName("created") val created: String = "",
)

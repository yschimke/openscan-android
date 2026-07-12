package ee.schimke.openscan.core.api

/**
 * Path templates for the OpenScan3 firmware REST API.
 *
 * OpenScan3 is a FastAPI app that serves a versioned API under `/api/latest` (the web UI documents
 * it at `http://<host>/api/latest/docs`). These constants are the single source of truth for the
 * paths the native client hits; anything the native client doesn't cover is reached through the
 * in-app web fallback (the device's own UI), so the surface here can stay small and stable even as
 * the firmware evolves.
 */
object OpenScanEndpoints {
  const val API_BASE = "/api/latest"

  const val DEVICE = "$API_BASE/device"
  const val CAMERAS = "$API_BASE/cameras"
  const val LIGHTS = "$API_BASE/lights"
  const val PROJECTS = "$API_BASE/projects"

  const val SCAN_STATUS = "$API_BASE/scan/status"
  const val SCAN_START = "$API_BASE/scan/start"
  const val SCAN_STOP = "$API_BASE/scan/stop"

  /** Live camera preview frame (JPEG). */
  fun cameraPreview(camera: String): String = "$CAMERAS/${camera.ifEmpty { "0" }}/preview"

  fun motor(name: String): String = "$API_BASE/motors/$name"

  fun motorMove(name: String): String = "${motor(name)}/move"
}

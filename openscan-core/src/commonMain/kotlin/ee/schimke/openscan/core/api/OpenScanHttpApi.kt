package ee.schimke.openscan.core.api

import ee.schimke.openscan.core.model.CameraInfo
import ee.schimke.openscan.core.model.DeviceInfo
import ee.schimke.openscan.core.model.LightState
import ee.schimke.openscan.core.model.ScanProject
import ee.schimke.openscan.core.model.ScanSettings
import ee.schimke.openscan.core.model.ScanStatus
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * Ktor-backed [OpenScanApi] for one device. [baseUrl] is the device root (e.g.
 * `http://openscan.local`); paths come from [OpenScanEndpoints].
 *
 * Decoding is done by hand through [OpenScanJson] rather than Ktor content-negotiation so the same
 * lenient codec covers list endpoints that the firmware sometimes wraps in an envelope and
 * sometimes returns bare.
 */
class OpenScanHttpApi(
  private val client: HttpClient,
  private val baseUrl: String,
  private val json: Json = OpenScanJson,
) : OpenScanApi {

  private fun url(path: String) = "$baseUrl$path"

  private suspend fun HttpResponse.textOrThrow(what: String): String {
    if (!status.isSuccess()) {
      throw OpenScanException("$what failed: HTTP ${status.value}")
    }
    return bodyAsText()
  }

  override suspend fun device(): DeviceInfo = wrap("device") {
    val body = client.get(url(OpenScanEndpoints.DEVICE)).textOrThrow("device")
    json.decodeFromString(DeviceInfo.serializer(), body)
  }

  override suspend fun cameras(): List<CameraInfo> = wrap("cameras") {
    val body = client.get(url(OpenScanEndpoints.CAMERAS)).textOrThrow("cameras")
    json.decodeFromString(ListSerializer(CameraInfo.serializer()), body)
  }

  override suspend fun previewFrame(camera: String): ByteArray = wrap("preview") {
    val response = client.get(url(OpenScanEndpoints.cameraPreview(camera)))
    if (!response.status.isSuccess()) {
      throw OpenScanException("preview failed: HTTP ${response.status.value}")
    }
    response.bodyAsBytes()
  }

  override suspend fun lights(): List<LightState> = wrap("lights") {
    val body = client.get(url(OpenScanEndpoints.LIGHTS)).textOrThrow("lights")
    json.decodeFromString(ListSerializer(LightState.serializer()), body)
  }

  override suspend fun setLight(name: String, on: Boolean, brightness: Int): LightState =
    wrap("setLight") {
      val payload = json.encodeToString(LightState.serializer(), LightState(name, on, brightness))
      val body =
        client
          .post(url("${OpenScanEndpoints.LIGHTS}/$name")) {
            contentType(ContentType.Application.Json)
            setBody(payload)
          }
          .textOrThrow("setLight")
      json.decodeFromString(LightState.serializer(), body)
    }

  override suspend fun rotate(motor: String, degrees: Double) = wrap("rotate") {
    client
      .post(url(OpenScanEndpoints.motorMove(motor))) {
        contentType(ContentType.Application.Json)
        setBody("""{"angle":$degrees}""")
      }
      .textOrThrow("rotate")
    Unit
  }

  override suspend fun scanStatus(): ScanStatus = wrap("scanStatus") {
    val body = client.get(url(OpenScanEndpoints.SCAN_STATUS)).textOrThrow("scanStatus")
    json.decodeFromString(ScanStatus.serializer(), body)
  }

  override suspend fun startScan(settings: ScanSettings): ScanStatus = wrap("startScan") {
    val payload = json.encodeToString(ScanSettings.serializer(), settings)
    val body =
      client
        .post(url(OpenScanEndpoints.SCAN_START)) {
          contentType(ContentType.Application.Json)
          setBody(payload)
        }
        .textOrThrow("startScan")
    json.decodeFromString(ScanStatus.serializer(), body)
  }

  override suspend fun stopScan(): ScanStatus = wrap("stopScan") {
    val body = client.post(url(OpenScanEndpoints.SCAN_STOP)).textOrThrow("stopScan")
    json.decodeFromString(ScanStatus.serializer(), body)
  }

  override suspend fun projects(): List<ScanProject> = wrap("projects") {
    val body = client.get(url(OpenScanEndpoints.PROJECTS)).textOrThrow("projects")
    json.decodeFromString(ListSerializer(ScanProject.serializer()), body)
  }

  /** Translate any lower-level failure into a single [OpenScanException] type. */
  private inline fun <T> wrap(what: String, block: () -> T): T =
    try {
      block()
    } catch (e: OpenScanException) {
      throw e
    } catch (e: Throwable) {
      throw OpenScanException("$what failed: ${e.message}", e)
    }
}

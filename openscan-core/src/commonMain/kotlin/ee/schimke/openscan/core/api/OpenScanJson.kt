package ee.schimke.openscan.core.api

import kotlinx.serialization.json.Json

/**
 * Shared JSON codec for the OpenScan API. Lenient by design: the firmware is a moving target, so
 * unknown keys are ignored and missing keys fall back to model defaults rather than throwing.
 */
val OpenScanJson: Json = Json {
  ignoreUnknownKeys = true
  isLenient = true
  explicitNulls = false
  encodeDefaults = true
}

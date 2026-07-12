package ee.schimke.openscan.components.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import ee.schimke.openscan.components.ui.theme.Dimens
import ee.schimke.openscan.core.manager.ScannerState
import ee.schimke.openscan.core.model.ScanPhase
import ee.schimke.openscan.core.model.ScanSettings

/**
 * Stateless body of a single scanner's detail screen: identity header, live camera preview,
 * turntable jog, scan configuration and start/stop with progress, plus the escape hatch to the
 * device's own web UI for anything the native client doesn't cover.
 *
 * The camera preview is a caller-supplied [previewSlot] so this body stays multiplatform (the app
 * fills it with a decoded frame; previews/desktop pass a placeholder).
 */
@Composable
fun ScannerDetailBody(
  state: ScannerState,
  settings: ScanSettings,
  onSettingsChange: (ScanSettings) -> Unit,
  onStartScan: () -> Unit,
  onStopScan: () -> Unit,
  onRotate: (Double) -> Unit,
  onOpenWeb: () -> Unit,
  modifier: Modifier = Modifier,
  previewSlot: @Composable (Modifier) -> Unit = { CameraPreviewPlaceholder(it) },
) {
  Column(
    modifier =
      modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(Dimens.ScreenPadding),
    verticalArrangement = Arrangement.spacedBy(Dimens.CardGap),
  ) {
    DeviceHeader(state)
    state.error?.let { ErrorBanner(it) }
    PreviewCard(previewSlot, onRotate)
    ScanCard(state, settings, onSettingsChange, onStartScan, onStopScan)
    OutlinedButton(onClick = onOpenWeb, modifier = Modifier.fillMaxWidth()) {
      Text("Open full web interface")
    }
  }
}

@Composable
private fun DeviceHeader(state: ScannerState) {
  Column(verticalArrangement = Arrangement.spacedBy(Dimens.XXS)) {
    Text(
      state.device?.name?.ifBlank { state.scanner.name } ?: state.scanner.name,
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.SemiBold,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.M), verticalAlignment = Alignment.CenterVertically) {
      ReachabilityPill(state.reachability)
      val fw = state.device?.firmwareVersion?.takeIf { it.isNotBlank() }
      Text(
        buildString {
          append(state.scanner.host)
          if (fw != null) append(" · fw $fw")
        },
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}

@Composable
private fun ErrorBanner(message: String) {
  Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
    Text(
      message,
      Modifier.padding(Dimens.L),
      color = MaterialTheme.colorScheme.onErrorContainer,
      style = MaterialTheme.typography.bodyMedium,
    )
  }
}

@Composable
private fun PreviewCard(previewSlot: @Composable (Modifier) -> Unit, onRotate: (Double) -> Unit) {
  Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
    Column(Modifier.padding(Dimens.L), verticalArrangement = Arrangement.spacedBy(Dimens.M)) {
      SectionHeader("Camera")
      previewSlot(Modifier.fillMaxWidth().aspectRatio(4f / 3f))
      Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.S),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text("Turntable", style = MaterialTheme.typography.labelLarge)
        FilledTonalButton(onClick = { onRotate(-15.0) }) { Text("−15°") }
        FilledTonalButton(onClick = { onRotate(15.0) }) { Text("+15°") }
      }
    }
  }
}

@Composable
private fun ScanCard(
  state: ScannerState,
  settings: ScanSettings,
  onSettingsChange: (ScanSettings) -> Unit,
  onStartScan: () -> Unit,
  onStopScan: () -> Unit,
) {
  Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
    Column(Modifier.padding(Dimens.L), verticalArrangement = Arrangement.spacedBy(Dimens.M)) {
      SectionHeader("Scan")
      if (state.scan.isActive) {
        val label =
          when (state.scan.phase) {
            ScanPhase.INITIALIZING -> "Preparing…"
            ScanPhase.PROCESSING -> "Processing…"
            else -> "Capturing ${state.scan.captured} / ${state.scan.total}"
          }
        Text(label, style = MaterialTheme.typography.bodyMedium)
        LinearProgressIndicator(
          progress = { state.scan.fraction },
          modifier = Modifier.fillMaxWidth(),
        )
        Button(
          onClick = onStopScan,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text("Stop scan")
        }
      } else {
        Stepper(
          label = "Points",
          value = settings.points,
          onChange = { onSettingsChange(settings.copy(points = it)) },
          range = 8..200,
          step = 4,
        )
        Stepper(
          label = "Rotations",
          value = settings.rotations,
          onChange = { onSettingsChange(settings.copy(rotations = it)) },
          range = 1..12,
          step = 1,
        )
        Button(onClick = onStartScan, modifier = Modifier.fillMaxWidth()) { Text("Start scan") }
      }
    }
  }
}

@Composable
private fun Stepper(label: String, value: Int, onChange: (Int) -> Unit, range: IntRange, step: Int) {
  Row(
    Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(label, style = MaterialTheme.typography.bodyLarge)
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Dimens.S)) {
      IconButton(onClick = { onChange((value - step).coerceIn(range.first, range.last)) }) { Text("−") }
      Text("$value", style = MaterialTheme.typography.titleMedium)
      IconButton(onClick = { onChange((value + step).coerceIn(range.first, range.last)) }) { Text("+") }
    }
  }
}

/** Neutral placeholder for the camera area (used in previews / while no frame is available). */
@Composable
fun CameraPreviewPlaceholder(modifier: Modifier = Modifier) {
  Box(
    modifier.background(MaterialTheme.colorScheme.surfaceContainerHighest, MaterialTheme.shapes.medium),
    contentAlignment = Alignment.Center,
  ) {
    Text("No preview", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
  }
}

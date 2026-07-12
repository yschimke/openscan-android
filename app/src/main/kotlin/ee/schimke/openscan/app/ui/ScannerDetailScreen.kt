package ee.schimke.openscan.app.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ee.schimke.openscan.app.di.LocalAppGraph
import ee.schimke.openscan.components.ui.CameraPreviewPlaceholder
import ee.schimke.openscan.components.ui.ScannerDetailBody
import ee.schimke.openscan.core.model.Scanner
import ee.schimke.openscan.core.model.ScannerId
import ee.schimke.openscan.core.model.ScanSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A single scanner: identity, live camera preview, turntable jog, scan configuration and control.
 * Owns the polling loops (status + preview frames) scoped to the screen. Anything the native client
 * doesn't cover is handed off to the device's own web UI via [onOpenWeb].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerDetailScreen(
  scannerId: String,
  onBack: () -> Unit,
  onOpenWeb: (url: String, title: String) -> Unit,
) {
  val graph = LocalAppGraph.current
  val scope = rememberCoroutineScope()

  val scanner by
    graph.repository
      .scanner(ScannerId(scannerId))
      .collectAsStateWithLifecycle(initialValue = null)

  val current = scanner
  if (current == null) {
    LoadingScaffold(onBack)
    return
  }

  ScannerDetailContent(current, onBack, onOpenWeb, graph, scope)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScannerDetailContent(
  scanner: Scanner,
  onBack: () -> Unit,
  onOpenWeb: (String, String) -> Unit,
  graph: ee.schimke.openscan.app.di.AppGraph,
  scope: kotlinx.coroutines.CoroutineScope,
) {
  val manager = remember(scanner.id) { graph.managerFor(scanner) }
  val state by manager.state.collectAsStateWithLifecycle()
  var settings by remember(scanner.id) { mutableStateOf(ScanSettings()) }
  var frame by remember(scanner.id) { mutableStateOf<ImageBitmap?>(null) }

  // Status poll — faster while a scan is active so progress feels live.
  LaunchedEffect(manager) {
    while (true) {
      manager.refresh()
      delay(if (manager.state.value.scan.isActive) 1_000 else 3_000)
    }
  }

  // Camera preview poll — only while the device is reachable.
  val camera = state.cameras.firstOrNull()?.name ?: "0"
  LaunchedEffect(manager, camera) {
    while (true) {
      manager.previewFrame(camera)?.let { bytes ->
        runCatching { BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap() }
          .getOrNull()
          ?.let { frame = it }
      }
      delay(1_000)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(state.device?.name?.ifBlank { scanner.name } ?: scanner.name) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    }
  ) { padding ->
    ScannerDetailBody(
      state = state,
      settings = settings,
      onSettingsChange = { settings = it },
      onStartScan = { scope.launch { manager.startScan(settings.copy(camera = camera)) } },
      onStopScan = { scope.launch { manager.stopScan() } },
      onRotate = { deg -> scope.launch { manager.rotate("turntable", deg) } },
      onOpenWeb = { onOpenWeb(scanner.baseUrl, scanner.name) },
      modifier = Modifier.padding(padding),
      previewSlot = { m ->
        val f = frame
        if (f != null) {
          Image(bitmap = f, contentDescription = "Camera preview", modifier = m, contentScale = ContentScale.Crop)
        } else {
          CameraPreviewPlaceholder(m)
        }
      },
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingScaffold(onBack: () -> Unit) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Scanner") },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    }
  ) { padding ->
    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
      CircularProgressIndicator()
    }
  }
}

package ee.schimke.openscan.app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ee.schimke.openscan.app.di.LocalAppGraph
import ee.schimke.openscan.components.ui.AddScannerBody
import ee.schimke.openscan.core.model.Scanner
import kotlinx.coroutines.launch

/** Manual add-scanner screen: enter a host/IP (+ optional port/name), validate, register. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScannerScreen(onAdded: (Scanner) -> Unit, onBack: () -> Unit) {
  val graph = LocalAppGraph.current
  val scope = rememberCoroutineScope()

  var host by remember { mutableStateOf("") }
  var port by remember { mutableStateOf("80") }
  var name by remember { mutableStateOf("") }
  var error by remember { mutableStateOf<String?>(null) }

  val parsedPort = port.toIntOrNull()
  val canSubmit = host.isNotBlank() && parsedPort != null && parsedPort in 1..65535

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Add scanner") },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    }
  ) { padding ->
    AddScannerBody(
      host = host,
      onHostChange = {
        host = it
        error = null
      },
      port = port,
      onPortChange = {
        port = it.filter(Char::isDigit)
        error = null
      },
      name = name,
      onNameChange = { name = it },
      error = error,
      canSubmit = canSubmit,
      onSubmit = {
        val p = parsedPort
        if (host.isBlank() || p == null) {
          error = "Enter a valid host and port."
          return@AddScannerBody
        }
        scope.launch {
          val scanner = graph.repository.register(host = host, port = p, name = name)
          onAdded(scanner)
        }
      },
      modifier = Modifier.padding(padding),
    )
  }
}

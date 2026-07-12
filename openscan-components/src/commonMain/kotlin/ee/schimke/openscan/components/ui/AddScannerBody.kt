package ee.schimke.openscan.components.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ee.schimke.openscan.components.ui.theme.Dimens

/**
 * Stateless manual add-scanner form. The screen owns the field state and validation; this body just
 * renders it. `host`/`port` come in as strings so the caller controls parsing and error display.
 */
@Composable
fun AddScannerBody(
  host: String,
  onHostChange: (String) -> Unit,
  port: String,
  onPortChange: (String) -> Unit,
  name: String,
  onNameChange: (String) -> Unit,
  error: String?,
  canSubmit: Boolean,
  onSubmit: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier.fillMaxSize().padding(Dimens.ScreenPadding),
    verticalArrangement = Arrangement.spacedBy(Dimens.M),
  ) {
    Text(
      "Enter the address shown in the OpenScan app or on the device — for example openscan.local, or an IP like 192.168.1.42.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    OutlinedTextField(
      value = host,
      onValueChange = onHostChange,
      label = { Text("Host or IP address") },
      singleLine = true,
      isError = error != null,
      modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
      value = port,
      onValueChange = onPortChange,
      label = { Text("Port") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
      value = name,
      onValueChange = onNameChange,
      label = { Text("Name (optional)") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
    )
    error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
    Button(onClick = onSubmit, enabled = canSubmit, modifier = Modifier.fillMaxWidth()) {
      Text("Add scanner")
    }
  }
}

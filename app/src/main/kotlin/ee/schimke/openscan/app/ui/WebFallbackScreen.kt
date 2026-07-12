package ee.schimke.openscan.app.ui

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * The escape hatch: the device's own web UI in a [WebView]. Everything the native client doesn't
 * implement (advanced settings, cloud upload, firmware config) is reachable here, so the native
 * surface can stay focused without ever blocking the user.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebFallbackScreen(url: String, title: String, onBack: () -> Unit) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(title) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
      )
    }
  ) { padding ->
    AndroidView(
      modifier = Modifier.fillMaxSize().padding(padding),
      factory = { context ->
        WebView(context).apply {
          webViewClient = WebViewClient()
          settings.javaScriptEnabled = true
          settings.domStorageEnabled = true
          loadUrl(url)
        }
      },
    )
  }
}

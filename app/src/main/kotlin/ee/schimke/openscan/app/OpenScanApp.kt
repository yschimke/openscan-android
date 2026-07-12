package ee.schimke.openscan.app

import android.app.Application
import ee.schimke.openscan.app.di.AppGraph
import ee.schimke.openscan.app.di.AppGraphHolder

/** Application entry point; owns the single [AppGraph] for the process. */
class OpenScanApp : Application(), AppGraphHolder {
  override val appGraph: AppGraph by lazy { AppGraph(this) }

  override fun onTerminate() {
    super.onTerminate()
    appGraph.close()
  }
}

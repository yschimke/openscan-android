package ee.schimke.openscan.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ee.schimke.openscan.core.model.Scanner
import ee.schimke.openscan.core.repository.ScannerStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val Context.scannerDataStore: DataStore<Preferences> by
  preferencesDataStore(name = "openscan_scanners")

/**
 * DataStore-backed [ScannerStore]. The saved scanners are persisted as a single JSON array under one
 * preferences key — small, atomic, and easy to migrate. Uses the [Scanner] serializer generated in
 * `openscan-core`.
 */
class DataStoreScannerStore(context: Context) : ScannerStore {

  private val store = context.applicationContext.scannerDataStore
  private val json = Json { ignoreUnknownKeys = true }
  private val serializer = ListSerializer(Scanner.serializer())

  override val scanners: Flow<List<Scanner>> =
    store.data.map { prefs -> prefs[KEY]?.let { decode(it) } ?: emptyList() }

  override suspend fun upsert(scanner: Scanner) {
    store.edit { prefs ->
      val current = prefs[KEY]?.let { decode(it) } ?: emptyList()
      val next = current.filterNot { it.id == scanner.id } + scanner
      prefs[KEY] = json.encodeToString(serializer, next)
    }
  }

  override suspend fun remove(id: String) {
    store.edit { prefs ->
      val current = prefs[KEY]?.let { decode(it) } ?: emptyList()
      prefs[KEY] = json.encodeToString(serializer, current.filterNot { it.id.value == id })
    }
  }

  private fun decode(raw: String): List<Scanner> =
    runCatching { json.decodeFromString(serializer, raw) }.getOrDefault(emptyList())

  private companion object {
    val KEY = stringPreferencesKey("scanners_json")
  }
}

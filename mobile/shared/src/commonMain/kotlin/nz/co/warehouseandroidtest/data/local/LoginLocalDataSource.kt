package nz.co.warehouseandroidtest.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import nz.co.warehouseandroidtest.data.remote.defaultJson
import nz.co.warehouseandroidtest.domain.model.LoginSession

class LoginLocalDataSource(
    private val dataStore: DataStore<Preferences>,
    private val json: Json = defaultJson,
) {
    suspend fun save(session: LoginSession) {
        dataStore.edit { preferences ->
            preferences[SESSION_KEY] = json.encodeToString(LoginSession.serializer(), session)
        }
    }

    suspend fun get(): LoginSession? {
        val raw = dataStore.data.first()[SESSION_KEY] ?: return null
        return runCatching { json.decodeFromString(LoginSession.serializer(), raw) }.getOrNull()
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(SESSION_KEY)
        }
    }

    private companion object {
        val SESSION_KEY = stringPreferencesKey("login_session")
    }
}

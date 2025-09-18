package com.example.subcriptionmanagementapp.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SETTINGS_PREFS_NAME = "app_settings"

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = SETTINGS_PREFS_NAME
)

@Singleton
class SettingsPreferencesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode_enabled")
        val CURRENCY = stringPreferencesKey("currency")
    }

    val darkModeFlow: Flow<Boolean> =
        context.settingsDataStore.data.map { preferences ->
            preferences[Keys.DARK_MODE] ?: false
        }

    val currencyFlow: Flow<String> =
        context.settingsDataStore.data.map { preferences ->
            preferences[Keys.CURRENCY] ?: "USD"
        }

    suspend fun setDarkMode(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.DARK_MODE] = enabled
        }
    }

    suspend fun setCurrency(currency: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[Keys.CURRENCY] = currency
        }
    }
}

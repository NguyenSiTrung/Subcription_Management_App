package com.example.subcriptionmanagementapp.data.repository

import com.example.subcriptionmanagementapp.data.local.preferences.SettingsPreferencesDataSource
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val preferencesDataSource: SettingsPreferencesDataSource
) : SettingsRepository {

    override fun observeDarkMode(): Flow<Boolean> = preferencesDataSource.darkModeFlow

    override suspend fun setDarkMode(enabled: Boolean) {
        preferencesDataSource.setDarkMode(enabled)
    }
}

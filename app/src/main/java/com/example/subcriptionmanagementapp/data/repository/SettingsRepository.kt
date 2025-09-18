package com.example.subcriptionmanagementapp.data.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeDarkMode(): Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
    fun observeCurrency(): Flow<String>
    suspend fun setCurrency(currency: String)
}

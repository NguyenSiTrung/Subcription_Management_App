package com.example.subcriptionmanagementapp.domain.usecase.settings

import com.example.subcriptionmanagementapp.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateDarkModeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setDarkMode(enabled)
    }
}

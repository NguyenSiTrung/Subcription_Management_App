package com.example.subcriptionmanagementapp.domain.usecase.settings

import com.example.subcriptionmanagementapp.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateCurrencyUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(currency: String) {
        repository.setCurrency(currency)
    }
}

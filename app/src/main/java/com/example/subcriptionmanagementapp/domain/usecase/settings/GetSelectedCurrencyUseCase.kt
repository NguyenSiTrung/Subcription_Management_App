package com.example.subcriptionmanagementapp.domain.usecase.settings

import com.example.subcriptionmanagementapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSelectedCurrencyUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<String> = settingsRepository.observeCurrency()
}

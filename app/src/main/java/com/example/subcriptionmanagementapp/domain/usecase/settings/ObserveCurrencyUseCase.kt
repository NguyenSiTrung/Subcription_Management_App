package com.example.subcriptionmanagementapp.domain.usecase.settings

import com.example.subcriptionmanagementapp.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveCurrencyUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<String> = repository.observeCurrency()
}

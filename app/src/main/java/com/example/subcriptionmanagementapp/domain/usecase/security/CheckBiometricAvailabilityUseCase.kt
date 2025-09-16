package com.example.subcriptionmanagementapp.domain.usecase.security

import com.example.subcriptionmanagementapp.data.security.BiometricAuthManager
import javax.inject.Inject

class CheckBiometricAvailabilityUseCase @Inject constructor(
    private val biometricAuthManager: BiometricAuthManager
) {
    operator fun invoke(): Boolean {
        return biometricAuthManager.isBiometricAvailable()
    }
}
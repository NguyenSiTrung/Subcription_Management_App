package com.example.subcriptionmanagementapp.domain.usecase.security

import androidx.fragment.app.FragmentActivity
import com.example.subcriptionmanagementapp.data.security.BiometricAuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthenticateWithBiometricUseCase @Inject constructor(
    private val biometricAuthManager: BiometricAuthManager
) {
    operator fun invoke(
        activity: FragmentActivity,
        title: String = "Biometric Authentication",
        subtitle: String = "Use biometric to authenticate",
        description: String = "Confirm your identity to continue",
        negativeButtonText: String = "Cancel"
    ): Flow<Boolean> {
        biometricAuthManager.authenticate(activity, title, subtitle, description, negativeButtonText)
        return biometricAuthManager.authResultFlow
    }
}
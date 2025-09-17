package com.example.subcriptionmanagementapp.data.security

import android.content.Context
import android.content.Intent
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

@Singleton
class BiometricAuthManager @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private const val BIOMETRIC_AUTH_REQUEST_CODE = 1001
    }

    private val authResultChannel = Channel<Boolean>()
    val authResultFlow = authResultChannel.receiveAsFlow()

    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun authenticate(
            activity: FragmentActivity,
            title: String = "Biometric Authentication",
            subtitle: String = "Use biometric to authenticate",
            description: String = "Confirm your identity to continue",
            negativeButtonText: String = "Cancel"
    ) {
        val executor = ContextCompat.getMainExecutor(context)
        val callback =
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(
                            result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        try {
                            authResultChannel.trySend(true)
                        } catch (e: Exception) {
                            // Handle error
                        }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        // Authentication failed, but don't close the channel
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        try {
                            authResultChannel.trySend(false)
                        } catch (e: Exception) {
                            // Handle error
                        }
                    }
                }

        val promptInfo =
                BiometricPrompt.PromptInfo.Builder()
                        .setTitle(title)
                        .setSubtitle(subtitle)
                        .setDescription(description)
                        .setNegativeButtonText(negativeButtonText)
                        .setAllowedAuthenticators(
                                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
                        )
                        .build()

        val biometricPrompt = BiometricPrompt(activity, executor, callback)
        biometricPrompt.authenticate(promptInfo)
    }

    fun getBiometricAuthIntent(): Intent {
        return Intent().apply { action = "androidx.biometric.BiometricPrompt" }
    }
}

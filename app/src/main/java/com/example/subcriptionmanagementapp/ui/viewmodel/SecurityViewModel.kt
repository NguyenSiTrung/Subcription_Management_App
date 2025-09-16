package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcriptionmanagementapp.data.security.SecurityManager
import com.example.subcriptionmanagementapp.domain.usecase.security.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val securityManager: SecurityManager,
    private val encryptDataUseCase: EncryptDataUseCase,
    private val decryptDataUseCase: DecryptDataUseCase,
    private val checkBiometricAvailabilityUseCase: CheckBiometricAvailabilityUseCase,
    private val authenticateWithBiometricUseCase: AuthenticateWithBiometricUseCase
) : ViewModel() {

    private val _isBiometricEnabled = MutableStateFlow(false)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    private val _isAppLockEnabled = MutableStateFlow(false)
    val isAppLockEnabled: StateFlow<Boolean> = _isAppLockEnabled.asStateFlow()

    private val _isEncryptionEnabled = MutableStateFlow(true)
    val isEncryptionEnabled: StateFlow<Boolean> = _isEncryptionEnabled.asStateFlow()

    private val _isBiometricAvailable = MutableStateFlow(false)
    val isBiometricAvailable: StateFlow<Boolean> = _isBiometricAvailable.asStateFlow()

    private val _authResult = MutableStateFlow<Boolean?>(null)
    val authResult: StateFlow<Boolean?> = _authResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadSecuritySettings()
        checkBiometricAvailability()
    }

    private fun loadSecuritySettings() {
        _isBiometricEnabled.value = securityManager.isBiometricEnabled()
        _isAppLockEnabled.value = securityManager.isAppLockEnabled()
        _isEncryptionEnabled.value = securityManager.isEncryptionEnabled()
    }

    fun checkBiometricAvailability() {
        _isBiometricAvailable.value = checkBiometricAvailabilityUseCase()
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                securityManager.setBiometricEnabled(enabled)
                _isBiometricEnabled.value = enabled
                _successMessage.value = if (enabled) {
                    "Biometric authentication enabled"
                } else {
                    "Biometric authentication disabled"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update biometric settings"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setAppLockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                securityManager.setAppLockEnabled(enabled)
                _isAppLockEnabled.value = enabled
                _successMessage.value = if (enabled) {
                    "App lock enabled"
                } else {
                    "App lock disabled"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update app lock settings"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setEncryptionEnabled(enabled: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                securityManager.setEncryptionEnabled(enabled)
                _isEncryptionEnabled.value = enabled
                _successMessage.value = if (enabled) {
                    "Data encryption enabled"
                } else {
                    "Data encryption disabled"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update encryption settings"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun authenticateWithBiometric(
        activity: FragmentActivity,
        title: String = "Biometric Authentication",
        subtitle: String = "Use biometric to authenticate",
        description: String = "Confirm your identity to continue",
        negativeButtonText: String = "Cancel"
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                authenticateWithBiometricUseCase(activity, title, subtitle, description, negativeButtonText)
                    .collect { result ->
                        _authResult.value = result
                        if (result) {
                            _successMessage.value = "Authentication successful"
                        } else {
                            _error.value = "Authentication failed"
                        }
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Authentication failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun encryptData(data: String): String {
        return encryptDataUseCase(data)
    }

    fun decryptData(encryptedData: String): String {
        return decryptDataUseCase(encryptedData)
    }

    fun saveEncryptedString(key: String, value: String) {
        securityManager.saveEncryptedString(key, value)
    }

    fun getEncryptedString(key: String, defaultValue: String = ""): String {
        return securityManager.getEncryptedString(key, defaultValue)
    }

    fun clearAuthResult() {
        _authResult.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
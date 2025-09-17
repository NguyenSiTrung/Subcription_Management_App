package com.example.subcriptionmanagementapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcriptionmanagementapp.domain.usecase.backup.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class BackupViewModel
@Inject
constructor(
        private val createBackupUseCase: CreateBackupUseCase,
        private val restoreBackupUseCase: RestoreBackupUseCase,
        private val getBackupFilePickerIntentUseCase: GetBackupFilePickerIntentUseCase,
        private val getShareBackupIntentUseCase: GetShareBackupIntentUseCase
) : ViewModel() {

    private val _backupUri = MutableStateFlow<Uri?>(null)
    val backupUri: StateFlow<Uri?> = _backupUri.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun createBackup() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val uri = createBackupUseCase()
                if (uri != null) {
                    _backupUri.value = uri
                    _successMessage.value = "Backup created successfully"
                } else {
                    _error.value = "Failed to create backup"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to create backup"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun restoreBackup(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = restoreBackupUseCase(uri)
                if (success) {
                    _successMessage.value = "Backup restored successfully"
                } else {
                    _error.value = "Failed to restore backup"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to restore backup"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getBackupFilePickerIntent(): android.content.Intent {
        return getBackupFilePickerIntentUseCase()
    }

    fun getShareBackupIntent(uri: Uri): android.content.Intent {
        return getShareBackupIntentUseCase(uri)
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}

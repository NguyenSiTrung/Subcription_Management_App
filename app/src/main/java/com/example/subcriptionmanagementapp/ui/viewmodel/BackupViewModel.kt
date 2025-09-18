package com.example.subcriptionmanagementapp.ui.viewmodel

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.domain.usecase.backup.*
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class BackupViewModel
@Inject
constructor(
        private val createBackupUseCase: CreateBackupUseCase,
        private val restoreBackupUseCase: RestoreBackupUseCase,
        private val exportBackupUseCase: ExportBackupUseCase,
        private val getBackupFilePickerIntentUseCase: GetBackupFilePickerIntentUseCase,
        private val getShareBackupIntentUseCase: GetShareBackupIntentUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _events = MutableSharedFlow<BackupUiEvent>()
    val events: SharedFlow<BackupUiEvent> = _events.asSharedFlow()

    fun onShareBackupClicked() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val uri = createBackupUseCase()
                if (uri != null) {
                    _events.emit(BackupUiEvent.ShareBackup(uri))
                    _events.emit(BackupUiEvent.Success(R.string.backup_created_success))
                } else {
                    _events.emit(BackupUiEvent.Error(R.string.backup_create_failed))
                }
            } catch (e: Exception) {
                _events.emit(BackupUiEvent.Error(R.string.backup_create_failed))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSaveBackupClicked() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _events.emit(BackupUiEvent.RequestExport(suggestedFileName()))
        }
    }

    fun exportBackup(destination: Uri) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = exportBackupUseCase(destination)
                if (success) {
                    _events.emit(BackupUiEvent.Success(R.string.backup_saved_success))
                } else {
                    _events.emit(BackupUiEvent.Error(R.string.backup_save_failed))
                }
            } catch (e: Exception) {
                _events.emit(BackupUiEvent.Error(R.string.backup_save_failed))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun restoreBackup(uri: Uri) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = restoreBackupUseCase(uri)
                if (success) {
                    _events.emit(BackupUiEvent.Success(R.string.backup_restored_success))
                } else {
                    _events.emit(BackupUiEvent.Error(R.string.backup_restore_failed))
                }
            } catch (e: Exception) {
                _events.emit(BackupUiEvent.Error(R.string.backup_restore_failed))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getBackupFilePickerIntent(): Intent {
        return getBackupFilePickerIntentUseCase()
    }

    fun getShareBackupIntent(uri: Uri): Intent {
        return getShareBackupIntentUseCase(uri)
    }

    private fun suggestedFileName(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.getDefault())
        val timestamp = formatter.format(LocalDateTime.now())
        return "subscription_backup_$timestamp.json"
    }
}

sealed interface BackupUiEvent {
    data class ShareBackup(val uri: Uri) : BackupUiEvent
    data class Success(@StringRes val messageResId: Int) : BackupUiEvent
    data class Error(@StringRes val messageResId: Int) : BackupUiEvent
    data class RequestExport(val suggestedFileName: String) : BackupUiEvent
}

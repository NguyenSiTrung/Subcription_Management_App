package com.example.subcriptionmanagementapp.domain.usecase.backup

import android.net.Uri
import com.example.subcriptionmanagementapp.data.backup.BackupManager
import javax.inject.Inject

class RestoreBackupUseCase @Inject constructor(
    private val backupManager: BackupManager
) {
    suspend operator fun invoke(uri: Uri): Boolean {
        return backupManager.restoreBackup(uri)
    }
}
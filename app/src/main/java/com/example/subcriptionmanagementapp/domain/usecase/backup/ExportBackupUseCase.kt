package com.example.subcriptionmanagementapp.domain.usecase.backup

import android.net.Uri
import com.example.subcriptionmanagementapp.data.backup.BackupManager
import javax.inject.Inject

class ExportBackupUseCase @Inject constructor(
    private val backupManager: BackupManager
) {
    suspend operator fun invoke(destination: Uri): Boolean {
        return backupManager.exportBackup(destination)
    }
}

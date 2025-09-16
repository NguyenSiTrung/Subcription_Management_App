package com.example.subcriptionmanagementapp.domain.usecase.backup

import android.net.Uri
import com.example.subcriptionmanagementapp.data.backup.BackupManager
import javax.inject.Inject

class CreateBackupUseCase @Inject constructor(
    private val backupManager: BackupManager
) {
    suspend operator fun invoke(): Uri? {
        return backupManager.createBackup()
    }
}
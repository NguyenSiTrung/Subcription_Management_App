package com.example.subcriptionmanagementapp.domain.usecase.backup

import android.content.Intent
import com.example.subcriptionmanagementapp.data.backup.BackupManager
import javax.inject.Inject

class GetBackupFilePickerIntentUseCase @Inject constructor(
    private val backupManager: BackupManager
) {
    operator fun invoke(): Intent {
        return backupManager.getBackupFilePickerIntent()
    }
}
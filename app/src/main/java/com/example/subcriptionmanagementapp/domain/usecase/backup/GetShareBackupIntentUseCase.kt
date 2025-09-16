package com.example.subcriptionmanagementapp.domain.usecase.backup

import android.content.Intent
import android.net.Uri
import com.example.subcriptionmanagementapp.data.backup.BackupManager
import javax.inject.Inject

class GetShareBackupIntentUseCase @Inject constructor(
    private val backupManager: BackupManager
) {
    operator fun invoke(uri: Uri): Intent {
        return backupManager.getShareBackupIntent(uri)
    }
}
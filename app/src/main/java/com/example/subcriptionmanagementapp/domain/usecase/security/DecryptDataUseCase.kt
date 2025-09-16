package com.example.subcriptionmanagementapp.domain.usecase.security

import com.example.subcriptionmanagementapp.data.security.SecurityManager
import javax.inject.Inject

class DecryptDataUseCase @Inject constructor(
    private val securityManager: SecurityManager
) {
    operator fun invoke(encryptedData: String): String {
        return securityManager.decryptData(encryptedData)
    }
}
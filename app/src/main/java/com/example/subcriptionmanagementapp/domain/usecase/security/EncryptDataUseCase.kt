package com.example.subcriptionmanagementapp.domain.usecase.security

import com.example.subcriptionmanagementapp.data.security.SecurityManager
import javax.inject.Inject

class EncryptDataUseCase @Inject constructor(
    private val securityManager: SecurityManager
) {
    operator fun invoke(data: String): String {
        return securityManager.encryptData(data)
    }
}
package com.example.subcriptionmanagementapp.data.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private const val KEYSTORE_ALIAS = "SubscriptionManagementAppKeyStore"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val SHARED_PREFS_NAME = "encrypted_shared_prefs"
        private const val KEY_MASTER_KEY = "master_key"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_ENCRYPTION_ENABLED = "encryption_enabled"
    }
    
    private val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
    private val sharedPreferences: SharedPreferences
    
    init {
        // Create or get the master key
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        
        // Create encrypted shared preferences
        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            SHARED_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        
        // Generate secret key if not exists
        if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
            generateSecretKey()
        }
    }
    
    private fun generateSecretKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(false)
            .build()
        
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }
    
    private fun getSecretKey(): SecretKey {
        return keyStore.getKey(KEYSTORE_ALIAS, null) as SecretKey
    }
    
    fun encryptData(data: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())
        val encryptedBytes = cipher.doFinal(data.toByteArray())
        return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT)
    }
    
    fun decryptData(encryptedData: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey())
        val encryptedBytes = android.util.Base64.decode(encryptedData, android.util.Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }
    
    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }
    
    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }
    
    fun isAppLockEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_APP_LOCK_ENABLED, false)
    }
    
    fun setAppLockEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_APP_LOCK_ENABLED, enabled).apply()
    }
    
    fun isEncryptionEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_ENCRYPTION_ENABLED, true)
    }
    
    fun setEncryptionEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_ENCRYPTION_ENABLED, enabled).apply()
    }
    
    fun saveEncryptedString(key: String, value: String) {
        val encryptedValue = encryptData(value)
        sharedPreferences.edit().putString(key, encryptedValue).apply()
    }
    
    fun getEncryptedString(key: String, defaultValue: String = ""): String {
        val encryptedValue = sharedPreferences.getString(key, null)
        return if (encryptedValue != null) {
            try {
                decryptData(encryptedValue)
            } catch (e: Exception) {
                defaultValue
            }
        } else {
            defaultValue
        }
    }
    
    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }
}
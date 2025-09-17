package com.example.subcriptionmanagementapp.data.backup

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.subcriptionmanagementapp.data.local.entity.*
import com.example.subcriptionmanagementapp.domain.repository.CategoryRepository
import com.example.subcriptionmanagementapp.domain.repository.PaymentHistoryRepository
import com.example.subcriptionmanagementapp.domain.repository.ReminderRepository
import com.example.subcriptionmanagementapp.domain.repository.SubscriptionRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

@Singleton
class BackupManager
@Inject
constructor(
        @ApplicationContext private val context: Context,
        private val subscriptionRepository: SubscriptionRepository,
        private val categoryRepository: CategoryRepository,
        private val reminderRepository: ReminderRepository,
        private val paymentHistoryRepository: PaymentHistoryRepository
) {

    companion object {
        private const val BACKUP_FILE_NAME = "subscription_backup.json"
        private const val BACKUP_MIME_TYPE = "application/json"
    }

    data class BackupData(
            val subscriptions: List<Subscription>,
            val categories: List<Category>,
            val reminders: List<Reminder>,
            val paymentHistory: List<PaymentHistory>,
            val backupDate: Long = System.currentTimeMillis()
    )

    private val gson: Gson =
            GsonBuilder().registerTypeAdapter(Date::class.java, DateTypeAdapter()).create()

    suspend fun createBackup(): Uri? =
            withContext(Dispatchers.IO) {
                try {
                    // Get all data from repositories
                    val subscriptions = subscriptionRepository.getAllSubscriptions().first()
                    val categories = categoryRepository.getAllCategories().first()
                    val reminders = reminderRepository.getAllReminders().first()
                    val paymentHistory = paymentHistoryRepository.getAllPaymentHistory().first()

                    // Create backup data object
                    val backupData =
                            BackupData(
                                    subscriptions = subscriptions,
                                    categories = categories,
                                    reminders = reminders,
                                    paymentHistory = paymentHistory
                            )

                    // Convert to JSON
                    val json = gson.toJson(backupData)

                    // Save to file
                    val file = File(context.filesDir, BACKUP_FILE_NAME)
                    file.writeText(json)

                    // Return URI for the file
                    Uri.fromFile(file)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }

    suspend fun restoreBackup(uri: Uri): Boolean =
            withContext(Dispatchers.IO) {
                try {
                    // Read JSON from URI
                    val json =
                            context.contentResolver.openInputStream(uri)?.bufferedReader()?.use {
                                it.readText()
                            }
                                    ?: return@withContext false

                    // Parse JSON
                    val backupData =
                            gson.fromJson<BackupData>(
                                    json,
                                    object : TypeToken<BackupData>() {}.type
                            )

                    // Clear existing data
                    subscriptionRepository.clearAllSubscriptions()
                    categoryRepository.clearAllCategories()
                    reminderRepository.clearAllReminders()
                    paymentHistoryRepository.clearAllPaymentHistory()

                    // Insert categories first (since subscriptions depend on them)
                    backupData.categories.forEach { category ->
                        categoryRepository.insertCategory(category)
                    }

                    // Insert subscriptions
                    backupData.subscriptions.forEach { subscription ->
                        subscriptionRepository.insertSubscription(subscription)
                    }

                    // Insert reminders
                    backupData.reminders.forEach { reminder ->
                        reminderRepository.insertReminder(reminder)
                    }

                    // Insert payment history
                    backupData.paymentHistory.forEach { paymentHistory ->
                        paymentHistoryRepository.insertPaymentHistory(paymentHistory)
                    }

                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

    fun getBackupFilePickerIntent(): Intent {
        return Intent(Intent.ACTION_GET_CONTENT).apply {
            type = BACKUP_MIME_TYPE
            addCategory(Intent.CATEGORY_OPENABLE)
        }
    }

    fun getShareBackupIntent(uri: Uri): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = BACKUP_MIME_TYPE
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private class DateTypeAdapter :
            com.google.gson.JsonSerializer<Date>, com.google.gson.JsonDeserializer<Date> {
        override fun serialize(
                src: Date?,
                typeOfSrc: java.lang.reflect.Type?,
                context: com.google.gson.JsonSerializationContext?
        ): com.google.gson.JsonElement {
            return com.google.gson.JsonPrimitive(src?.time)
        }

        override fun deserialize(
                json: com.google.gson.JsonElement?,
                typeOfT: java.lang.reflect.Type?,
                context: com.google.gson.JsonDeserializationContext?
        ): Date? {
            return json?.asLong?.let { Date(it) }
        }
    }
}

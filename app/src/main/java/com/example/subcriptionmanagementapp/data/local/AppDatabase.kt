package com.example.subcriptionmanagementapp.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.subcriptionmanagementapp.data.local.dao.SubscriptionDao
import com.example.subcriptionmanagementapp.data.local.dao.CategoryDao
import com.example.subcriptionmanagementapp.data.local.dao.ReminderDao
import com.example.subcriptionmanagementapp.data.local.dao.PaymentHistoryDao
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory

@Database(
    entities = [
        Subscription::class,
        Category::class,
        Reminder::class,
        PaymentHistory::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun reminderDao(): ReminderDao
    abstract fun paymentHistoryDao(): PaymentHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "subscription_management_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration logic when upgrading database version
            }
        }
    }
}
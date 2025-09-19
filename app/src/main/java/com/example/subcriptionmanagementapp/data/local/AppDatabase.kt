package com.example.subcriptionmanagementapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.subcriptionmanagementapp.data.local.dao.CategoryDao
import com.example.subcriptionmanagementapp.data.local.dao.PaymentHistoryDao
import com.example.subcriptionmanagementapp.data.local.dao.ReminderDao
import com.example.subcriptionmanagementapp.data.local.dao.SubscriptionDao
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.data.local.entity.Reminder
import com.example.subcriptionmanagementapp.data.local.entity.Subscription

@Database(
        entities = [Subscription::class, Category::class, Reminder::class, PaymentHistory::class],
        version = 3,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun reminderDao(): ReminderDao
    abstract fun paymentHistoryDao(): PaymentHistoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        internal val MIGRATION_1_2 =
                object : Migration(1, 2) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL(
                                """
                                CREATE TABLE IF NOT EXISTS `subscriptions_new` (
                                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                        `name` TEXT NOT NULL,
                                        `description` TEXT,
                                        `price` REAL NOT NULL,
                                        `currency` TEXT NOT NULL,
                                        `billing_cycle` TEXT NOT NULL,
                                        `start_date` INTEGER NOT NULL,
                                        `next_billing_date` INTEGER NOT NULL,
                                        `end_date` INTEGER,
                                        `reminder_days` INTEGER NOT NULL,
                                        `is_active` INTEGER NOT NULL,
                                        `category_id` INTEGER,
                                        `website_url` TEXT,
                                        `app_package_name` TEXT,
                                        `notes` TEXT,
                                        `created_at` INTEGER NOT NULL,
                                        `updated_at` INTEGER NOT NULL,
                                        FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON DELETE CASCADE
                                )
                                """
                        )
                        database.execSQL(
                                """
                                INSERT INTO `subscriptions_new` (
                                        `id`,
                                        `name`,
                                        `description`,
                                        `price`,
                                        `currency`,
                                        `billing_cycle`,
                                        `start_date`,
                                        `next_billing_date`,
                                        `end_date`,
                                        `reminder_days`,
                                        `is_active`,
                                        `category_id`,
                                        `website_url`,
                                        `app_package_name`,
                                        `notes`,
                                        `created_at`,
                                        `updated_at`
                                )
                                SELECT
                                        `id`,
                                        `name`,
                                        `description`,
                                        `price`,
                                        `currency`,
                                        `billing_cycle`,
                                        `start_date`,
                                        `next_billing_date`,
                                        `end_date`,
                                        `reminder_days`,
                                        `is_active`,
                                        NULLIF(`category_id`, 0),
                                        `website_url`,
                                        `app_package_name`,
                                        `notes`,
                                        `created_at`,
                                        `updated_at`
                                FROM `subscriptions`
                                """
                        )
                        database.execSQL("DROP TABLE `subscriptions`")
                        database.execSQL("ALTER TABLE `subscriptions_new` RENAME TO `subscriptions`")
                        database.execSQL(
                                "CREATE INDEX IF NOT EXISTS `index_subscriptions_category_id` ON `subscriptions`(`category_id`)"
                        )
                        database.execSQL(
                                "CREATE INDEX IF NOT EXISTS `index_subscriptions_next_billing_date` ON `subscriptions`(`next_billing_date`)"
                        )
                        database.execSQL(
                                "CREATE INDEX IF NOT EXISTS `index_subscriptions_is_active` ON `subscriptions`(`is_active`)"
                        )
                    }
                }

        internal val MIGRATION_2_3 =
                object : Migration(2, 3) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL(
                                "ALTER TABLE `subscriptions` ADD COLUMN `reminder_hour` INTEGER NOT NULL DEFAULT ${Subscription.DEFAULT_REMINDER_HOUR}"
                        )
                        database.execSQL(
                                "ALTER TABLE `subscriptions` ADD COLUMN `reminder_minute` INTEGER NOT NULL DEFAULT ${Subscription.DEFAULT_REMINDER_MINUTE}"
                        )
                    }
                }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE
                    ?: synchronized(this) {
                        val instance =
                                Room.databaseBuilder(
                                                context.applicationContext,
                                                AppDatabase::class.java,
                                                "subscription_management_database"
                                        )
                                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                                        .build()
                        INSTANCE = instance
                        instance
                    }
        }
    }
}

package com.example.subcriptionmanagementapp.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    private val testDb = "migration-test"

    @get:Rule
    val helper = MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase::class.java.canonicalName,
            FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate2To3_addsReminderTimeColumnsWithDefaults() {
        helper.createDatabase(testDb, 2).apply {
            execSQL(
                    """
                    INSERT INTO subscriptions (
                            id,
                            name,
                            description,
                            price,
                            currency,
                            billing_cycle,
                            start_date,
                            next_billing_date,
                            end_date,
                            reminder_days,
                            is_active,
                            category_id,
                            website_url,
                            app_package_name,
                            notes,
                            created_at,
                            updated_at
                    ) VALUES (
                            1,
                            'Test Service',
                            'Test description',
                            9.99,
                            'USD',
                            'MONTHLY',
                            1,
                            2,
                            NULL,
                            3,
                            1,
                            NULL,
                            NULL,
                            NULL,
                            NULL,
                            1,
                            1
                    )
                    """
            )
            close()
        }

        helper.runMigrationsAndValidate(
                testDb,
                3,
                true,
                AppDatabase.MIGRATION_2_3
        ).use { database ->
            database.query("PRAGMA table_info(subscriptions)").use { cursor ->
                var hasReminderHour = false
                var hasReminderMinute = false
                while (cursor.moveToNext()) {
                    val columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                    if (columnName == "reminder_hour") {
                        hasReminderHour = true
                    }
                    if (columnName == "reminder_minute") {
                        hasReminderMinute = true
                    }
                }
                assertTrue(hasReminderHour, "reminder_hour column should exist after migration")
                assertTrue(hasReminderMinute, "reminder_minute column should exist after migration")
            }

            database.query(
                    "SELECT reminder_hour, reminder_minute FROM subscriptions WHERE id = 1"
            ).use { cursor ->
                assertTrue(cursor.moveToFirst(), "Inserted subscription should exist after migration")
                val reminderHour = cursor.getInt(0)
                val reminderMinute = cursor.getInt(1)
                assertEquals(Subscription.DEFAULT_REMINDER_HOUR, reminderHour)
                assertEquals(Subscription.DEFAULT_REMINDER_MINUTE, reminderMinute)
            }
        }
    }
}

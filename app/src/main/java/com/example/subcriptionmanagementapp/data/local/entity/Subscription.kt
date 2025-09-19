package com.example.subcriptionmanagementapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["category_id"]),
        Index(value = ["next_billing_date"]),
        Index(value = ["is_active"])
    ]
)
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "currency") val currency: String,
    @ColumnInfo(name = "billing_cycle") val billingCycle: BillingCycle,
    @ColumnInfo(name = "start_date") val startDate: Long,
    @ColumnInfo(name = "next_billing_date") val nextBillingDate: Long,
    @ColumnInfo(name = "end_date") val endDate: Long?,
    @ColumnInfo(name = "reminder_days") val reminderDays: Int,
    @ColumnInfo(name = "reminder_hour") val reminderHour: Int = DEFAULT_REMINDER_HOUR,
    @ColumnInfo(name = "reminder_minute") val reminderMinute: Int = DEFAULT_REMINDER_MINUTE,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "category_id") val categoryId: Long?,
    @ColumnInfo(name = "website_url") val websiteUrl: String?,
    @ColumnInfo(name = "app_package_name") val appPackageName: String?,
    @ColumnInfo(name = "notes") val notes: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
) {
    companion object {
        const val DEFAULT_REMINDER_HOUR = 9
        const val DEFAULT_REMINDER_MINUTE = 0
    }
}

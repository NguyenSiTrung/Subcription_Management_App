package com.example.subcriptionmanagementapp.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(
    tableName = "reminders",
    foreignKeys = [
        ForeignKey(
            entity = Subscription::class,
            parentColumns = ["id"],
            childColumns = ["subscription_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["subscription_id"]),
        Index(value = ["reminder_date"]),
        Index(value = ["is_notified"])
    ]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "subscription_id") val subscriptionId: Long,
    @ColumnInfo(name = "reminder_date") val reminderDate: Long,
    @ColumnInfo(name = "reminder_type") val reminderType: ReminderType,
    @ColumnInfo(name = "is_notified") val isNotified: Boolean,
    @ColumnInfo(name = "notification_id") val notificationId: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long
)
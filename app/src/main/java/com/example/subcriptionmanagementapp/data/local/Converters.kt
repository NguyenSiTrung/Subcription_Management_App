package com.example.subcriptionmanagementapp.data.local

import androidx.room.TypeConverter
import com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
import com.example.subcriptionmanagementapp.data.local.entity.ReminderType

class Converters {
    @TypeConverter
    fun fromBillingCycle(billingCycle: BillingCycle): String {
        return billingCycle.name
    }

    @TypeConverter
    fun toBillingCycle(billingCycle: String): BillingCycle {
        return BillingCycle.valueOf(billingCycle)
    }

    @TypeConverter
    fun fromReminderType(reminderType: ReminderType): String {
        return reminderType.name
    }

    @TypeConverter
    fun toReminderType(reminderType: String): ReminderType {
        return ReminderType.valueOf(reminderType)
    }
}
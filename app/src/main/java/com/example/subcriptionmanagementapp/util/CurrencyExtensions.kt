package com.example.subcriptionmanagementapp.util

import com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.manager.CurrencyRateManager
import kotlinx.coroutines.CoroutineScope

fun Double.formatCurrency(currencyCode: String = "USD"): String {
    return CurrencyUtils.formatCurrency(this, currencyCode)
}

fun Double.formatCurrency(): String {
    return CurrencyUtils.formatCurrency(this)
}

fun String.parseCurrencyAmount(currencyCode: String = "USD"): Double? {
    return CurrencyUtils.parseCurrencyAmount(this, currencyCode)
}

fun String.parseCurrencyAmount(): Double? {
    return CurrencyUtils.parseCurrencyAmount(this)
}

suspend fun Double.formatCurrencyWithConversion(
        fromCurrency: String,
        toCurrency: String,
        currencyRateManager: CurrencyRateManager,
        coroutineScope: CoroutineScope
): String {
    return if (fromCurrency == toCurrency) {
        this.formatCurrency(toCurrency)
    } else {
        val convertedAmount = currencyRateManager.convertCurrency(this, fromCurrency, toCurrency)
        convertedAmount?.let { it.formatCurrency(toCurrency) } ?: this.formatCurrency(toCurrency)
    }
}

suspend fun Subscription.convertPriceToCurrency(
        targetCurrency: String,
        currencyRateManager: CurrencyRateManager
): Double {
    return if (this.currency == targetCurrency) {
        this.price
    } else {
        try {
            currencyRateManager.convertCurrency(this.price, this.currency, targetCurrency)
                    ?: this.price
        } catch (e: Exception) {
            // If conversion fails, return original amount
            this.price
        }
    }
}

fun Subscription.convertToMonthlyEquivalent(): Double {
    return when (this.billingCycle) {
        BillingCycle.DAILY -> this.price * 30.44 // Average days in a month
        BillingCycle.WEEKLY -> this.price * 4.33 // Average weeks in a month
        BillingCycle.MONTHLY -> this.price
        BillingCycle.YEARLY -> this.price / 12.0
    }
}

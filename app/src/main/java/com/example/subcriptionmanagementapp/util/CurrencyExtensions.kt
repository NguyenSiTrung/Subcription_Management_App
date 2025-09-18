package com.example.subcriptionmanagementapp.util

import com.example.subcriptionmanagementapp.data.manager.CurrencyRateManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
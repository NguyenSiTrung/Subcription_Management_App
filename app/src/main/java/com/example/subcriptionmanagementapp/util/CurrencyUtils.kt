package com.example.subcriptionmanagementapp.util

import com.example.subcriptionmanagementapp.data.manager.CurrencyRateManager
import java.text.NumberFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
object CurrencyUtils {
    private const val DEFAULT_CURRENCY = "USD"
    
    fun formatCurrency(amount: Double, currencyCode: String = DEFAULT_CURRENCY): String {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        try {
            format.currency = Currency.getInstance(currencyCode)
        } catch (e: IllegalArgumentException) {
            // If the currency code is invalid, use the default currency
            format.currency = Currency.getInstance(DEFAULT_CURRENCY)
        }
        return format.format(amount)
    }
    
    fun formatCurrency(amount: Double): String {
        return formatCurrency(amount, DEFAULT_CURRENCY)
    }
    
    fun getCurrencySymbol(currencyCode: String = DEFAULT_CURRENCY): String {
        return try {
            Currency.getInstance(currencyCode).symbol
        } catch (e: IllegalArgumentException) {
            Currency.getInstance(DEFAULT_CURRENCY).symbol
        }
    }
    
    fun parseCurrencyAmount(amountString: String, currencyCode: String = DEFAULT_CURRENCY): Double? {
        return try {
            val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
            format.currency = Currency.getInstance(currencyCode)
            format.parse(amountString)?.toDouble()
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun formatCurrencyWithConversion(
        amount: Double, 
        fromCurrency: String, 
        toCurrency: String,
        currencyRateManager: CurrencyRateManager
    ): String {
        return if (fromCurrency == toCurrency) {
            formatCurrency(amount, toCurrency)
        } else {
            val convertedAmount = currencyRateManager.convertCurrency(amount, fromCurrency, toCurrency)
            convertedAmount?.let { formatCurrency(it, toCurrency) } ?: formatCurrency(amount, toCurrency)
        }
    }
}
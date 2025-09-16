package com.example.subcriptionmanagementapp.util

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
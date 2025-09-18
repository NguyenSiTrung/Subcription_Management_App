package com.example.subcriptionmanagementapp.data.manager

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

@Singleton
class CurrencyRateManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val TAG = "CurrencyRateManager"
    private val API_URL = "https://open.er-api.com/v6/latest/USD"

    // Cache exchange rates to avoid frequent API calls
    private var cachedRates: Map<String, Double>? = null
    private var lastFetchTime: Long = 0
    private val CACHE_DURATION_MS = TimeUnit.HOURS.toMillis(6) // Cache for 6 hours

    // Fallback exchange rates when API fails (USD as base)
    private val fallbackRates =
            mapOf(
                    "USD" to 1.0,
                    "EUR" to 0.85,
                    "GBP" to 0.73,
                    "JPY" to 110.0,
                    "CAD" to 1.25,
                    "AUD" to 1.35,
                    "CHF" to 0.92,
                    "CNY" to 6.45,
                    "INR" to 74.5,
                    "VND" to 23000.0,
                    "KRW" to 1180.0,
                    "SGD" to 1.35,
                    "MYR" to 4.15,
                    "THB" to 33.5,
                    "PHP" to 50.5,
                    "IDR" to 14300.0,
                    "BRL" to 5.2,
                    "MXN" to 20.0,
                    "RUB" to 73.5,
                    "ZAR" to 14.8
            )

    suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): Double? =
            withContext(Dispatchers.IO) {
                if (fromCurrency == toCurrency) {
                    return@withContext 1.0
                }

                // Check if we need to fetch fresh rates
                if (cachedRates == null ||
                                System.currentTimeMillis() - lastFetchTime > CACHE_DURATION_MS
                ) {
                    fetchExchangeRates()
                }

                return@withContext (cachedRates ?: fallbackRates).let { rates ->
                    val fromRate = rates[fromCurrency] ?: return@withContext null
                    val toRate = rates[toCurrency] ?: return@withContext null

                    // Convert from source currency to USD, then to target currency
                    (1.0 / fromRate) * toRate
                }
            }

    private suspend fun fetchExchangeRates() =
            withContext(Dispatchers.IO) {
                try {
                    val url = URL(API_URL)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000

                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val response =
                                BufferedReader(InputStreamReader(connection.inputStream)).use {
                                    it.readText()
                                }
                        val jsonResponse = JSONObject(response)

                        if (jsonResponse.getString("result") == "success") {
                            val rates = jsonResponse.getJSONObject("rates")
                            val rateMap = mutableMapOf<String, Double>()

                            // Add USD as base currency
                            rateMap["USD"] = 1.0

                            // Extract all other rates
                            val keys = rates.keys()
                            while (keys.hasNext()) {
                                val key = keys.next()
                                rateMap[key] = rates.getDouble(key)
                            }

                            cachedRates = rateMap
                            lastFetchTime = System.currentTimeMillis()
                            Log.d(TAG, "Successfully fetched exchange rates")
                        } else {
                            Log.e(TAG, "API returned unsuccessful response")
                            // Use fallback rates when API fails
                            cachedRates = fallbackRates
                            Log.d(TAG, "Using fallback exchange rates")
                        }
                    } else {
                        Log.e(TAG, "API request failed with code: ${connection.responseCode}")
                        // Use fallback rates when API fails
                        cachedRates = fallbackRates
                        Log.d(TAG, "Using fallback exchange rates due to HTTP error")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching exchange rates", e)
                    // Use fallback rates when API fails
                    cachedRates = fallbackRates
                    Log.d(TAG, "Using fallback exchange rates due to exception")
                }
            }

    suspend fun convertCurrency(amount: Double, fromCurrency: String, toCurrency: String): Double? {
        val rate = getExchangeRate(fromCurrency, toCurrency)
        return rate?.let { amount * it }
    }
}

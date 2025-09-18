package com.example.subcriptionmanagementapp.data.manager

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "CurrencyRateManager"
    private val API_URL = "https://open.er-api.com/v6/latest/USD"
    
    // Cache exchange rates to avoid frequent API calls
    private var cachedRates: Map<String, Double>? = null
    private var lastFetchTime: Long = 0
    private val CACHE_DURATION_MS = TimeUnit.HOURS.toMillis(6) // Cache for 6 hours
    
    suspend fun getExchangeRate(fromCurrency: String, toCurrency: String): Double? = withContext(Dispatchers.IO) {
        if (fromCurrency == toCurrency) {
            return@withContext 1.0
        }
        
        // Check if we need to fetch fresh rates
        if (cachedRates == null || System.currentTimeMillis() - lastFetchTime > CACHE_DURATION_MS) {
            fetchExchangeRates()
        }
        
        return@withContext cachedRates?.let { rates ->
            val fromRate = rates[fromCurrency] ?: return@let null
            val toRate = rates[toCurrency] ?: return@let null
            
            // Convert from source currency to USD, then to target currency
            (1.0 / fromRate) * toRate
        }
    }
    
    private suspend fun fetchExchangeRates() = withContext(Dispatchers.IO) {
        try {
            val url = URL(API_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
                val jsonResponse = JSONObject(response)
                
                if (jsonResponse.getBoolean("success")) {
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
                }
            } else {
                Log.e(TAG, "API request failed with code: ${connection.responseCode}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching exchange rates", e)
        }
    }
    
    suspend fun convertCurrency(amount: Double, fromCurrency: String, toCurrency: String): Double? {
        val rate = getExchangeRate(fromCurrency, toCurrency)
        return rate?.let { amount * it }
    }
}

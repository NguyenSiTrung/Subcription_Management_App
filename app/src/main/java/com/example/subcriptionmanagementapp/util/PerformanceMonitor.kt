package com.example.subcriptionmanagementapp.util

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import kotlin.system.measureTimeMillis

/**
 * Performance monitoring utility for tracking animation and rendering performance
 */
object PerformanceMonitor {

    private const val TAG = "PerformanceMonitor"
    private const val ENABLE_MONITORING = true // Set to false in production
    private const val LOG_THRESHOLD_MS = 16L // 60fps = 16.67ms per frame

    private val timingData = ConcurrentHashMap<String, MutableList<Long>>()
    private val recompositionCounts = ConcurrentHashMap<String, Int>()

    /**
     * Measures the execution time of a block of code
     */
    fun <T> measurePerformance(
            operationName: String,
            block: () -> T
    ): T {
        if (!ENABLE_MONITORING) return block()

        val result: T
        val executionTime = measureTimeMillis {
            result = block()
        }

        if (executionTime > LOG_THRESHOLD_MS) {
            Log.w(TAG, "$operationName took ${executionTime}ms (>$LOG_THRESHOLD_MS ms)")
        }

        // Store timing data
        timingData.getOrPut(operationName) { mutableListOf() }.add(executionTime)

        return result
    }

    /**
     * Tracks recomposition count for a composable
     */
    @Composable
    fun TrackRecomposition(composableName: String) {
        if (!ENABLE_MONITORING) return

        val count = remember { 
            recompositionCounts.getOrPut(composableName) { 0 }
        }
        
        LaunchedEffect(Unit) {
            recompositionCounts[composableName] = count + 1
            if (count > 10) { // Log if recomposed more than 10 times
                Log.d(TAG, "$composableName recomposed $count times")
            }
        }
    }

    /**
     * Monitors animation frame timing
     */
    fun monitorAnimationFrame(animationName: String, frameTime: Long) {
        if (!ENABLE_MONITORING) return

        if (frameTime > LOG_THRESHOLD_MS) {
            Log.w(TAG, "Animation $animationName frame took ${frameTime}ms (>$LOG_THRESHOLD_MS ms)")
        }
    }

    /**
     * Gets average execution time for an operation
     */
    fun getAverageExecutionTime(operationName: String): Double {
        val times = timingData[operationName] ?: return 0.0
        return if (times.isNotEmpty()) times.average() else 0.0
    }

    /**
     * Gets recomposition count for a composable
     */
    fun getRecompositionCount(composableName: String): Int {
        return recompositionCounts[composableName] ?: 0
    }

    /**
     * Clears all performance data
     */
    fun clearAllData() {
        timingData.clear()
        recompositionCounts.clear()
    }

    /**
     * Logs performance summary
     */
    fun logPerformanceSummary() {
        if (!ENABLE_MONITORING) return

        Log.i(TAG, "=== Performance Summary ===")
        
        // Log timing data
        timingData.forEach { (operation, times) ->
            if (times.isNotEmpty()) {
                val avg = times.average()
                val max = times.max()
                val min = times.min()
                Log.i(TAG, "$operation: avg=${avg}ms, min=${min}ms, max=${max}ms, samples=${times.size}")
            }
        }

        // Log recomposition data
        recompositionCounts.forEach { (composable, count) ->
            Log.i(TAG, "$composable: recomposed $count times")
        }

        Log.i(TAG, "==========================")
    }

    /**
     * Performance monitoring for subscription card animations
     */
    object SubscriptionCardMonitor {

        private const val CARD_EXPAND = "card_expand"
        private const val CARD_COLLAPSE = "card_collapse"
        private const val GRADIENT_CALCULATION = "gradient_calculation"
        private const val CATEGORY_LOOKUP = "category_lookup"

        fun monitorCardExpand(block: () -> Unit) {
            measurePerformance(CARD_EXPAND, block)
        }

        fun monitorCardCollapse(block: () -> Unit) {
            measurePerformance(CARD_COLLAPSE, block)
        }

        fun monitorGradientCalculation(block: () -> Unit) {
            measurePerformance(GRADIENT_CALCULATION, block)
        }

        fun monitorCategoryLookup(block: () -> Unit) {
            measurePerformance(CATEGORY_LOOKUP, block)
        }

        fun getCardAnimationStats(): Map<String, Double> {
            return mapOf(
                    "expand_avg" to getAverageExecutionTime(CARD_EXPAND),
                    "collapse_avg" to getAverageExecutionTime(CARD_COLLAPSE),
                    "gradient_avg" to getAverageExecutionTime(GRADIENT_CALCULATION),
                    "category_lookup_avg" to getAverageExecutionTime(CATEGORY_LOOKUP)
            )
        }
    }
}

/**
 * Composable performance tracking extension
 */
@Composable
fun <T> rememberPerformanceTracking(
        key: String,
        calculation: () -> T
): T {
    return PerformanceMonitor.measurePerformance("remember_$key", calculation)
}
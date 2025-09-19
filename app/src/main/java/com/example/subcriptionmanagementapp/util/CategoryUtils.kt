package com.example.subcriptionmanagementapp.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.ui.theme.*

/**
 * Utility functions for handling category-related operations
 */
object CategoryUtils {

    /**
     * Parses a category color string to Compose Color
     * Supports hex colors (#RRGGBB, #AARRGGBB) and predefined color names
     */
    fun parseColor(colorString: String?): Color {
        if (colorString.isNullOrBlank()) {
            return getDefaultCategoryColor()
        }

        return try {
            when {
                colorString.startsWith("#") -> {
                    // Handle hex colors
                    val cleanHex = colorString.removePrefix("#")
                    when {
                        cleanHex.length == 6 && isValidHex(cleanHex) -> {
                            Color(android.graphics.Color.parseColor("#$cleanHex"))
                        }
                        cleanHex.length == 8 && isValidHex(cleanHex) -> {
                            Color(android.graphics.Color.parseColor("#$cleanHex"))
                        }
                        else -> getDefaultCategoryColor()
                    }
                }
                else -> {
                    // Handle predefined color names
                    getPredefinedColor(colorString)
                }
            }
        } catch (e: IllegalArgumentException) {
            // Log the error in debug builds
            android.util.Log.w("CategoryUtils", "Invalid color format: $colorString", e)
            getDefaultCategoryColor()
        } catch (e: Exception) {
            android.util.Log.w("CategoryUtils", "Unexpected error parsing color: $colorString", e)
            getDefaultCategoryColor()
        }
    }

    /**
     * Validates if a string contains only valid hexadecimal characters
     */
    private fun isValidHex(hex: String): Boolean {
        return hex.all { it in '0'..'9' || it.lowercaseChar() in 'a'..'f' }
    }

    /**
     * Gets appropriate text color (light/dark) based on background color for accessibility
     * Uses WCAG guidelines for better contrast ratios
     */
    fun getContrastingTextColor(backgroundColor: Color): Color {
        // WCAG recommends 0.179 as the threshold for better accessibility
        return if (backgroundColor.luminance() > 0.179f) {
            Color.Black
        } else {
            Color.White
        }
    }

    /**
     * Calculates contrast ratio between two colors for accessibility compliance
     */
    fun getContrastRatio(color1: Color, color2: Color): Float {
        val luminance1 = color1.luminance()
        val luminance2 = color2.luminance()
        val lighter = maxOf(luminance1, luminance2)
        val darker = minOf(luminance1, luminance2)
        return (lighter + 0.05f) / (darker + 0.05f)
    }

    /**
     * Checks if color combination meets WCAG AA standards (4.5:1 ratio)
     */
    fun meetsAccessibilityStandards(foreground: Color, background: Color): Boolean {
        return getContrastRatio(foreground, background) >= 4.5f
    }

    /**
     * Gets the default category color for uncategorized items
     */
    fun getDefaultCategoryColor(): Color {
        return OtherColor
    }

    /**
     * Gets predefined category colors by name
     */
    private fun getPredefinedColor(colorName: String): Color {
        return when (colorName.lowercase()) {
            "streaming" -> StreamingColor
            "music" -> MusicColor
            "software" -> SoftwareColor
            "gaming" -> GamingColor
            "news" -> NewsColor
            "education" -> EducationColor
            "health" -> HealthColor
            "finance" -> FinanceColor
            "primary" -> PrimaryColor
            "secondary" -> SecondaryColor
            "tertiary" -> TertiaryColor
            "success" -> SuccessColor
            "warning" -> WarningColor
            "error" -> ErrorColor
            "info" -> InfoColor
            else -> getDefaultCategoryColor()
        }
    }

    /**
     * Gets category color with proper fallback handling
     */
    fun getCategoryColor(category: Category?): Color {
        return if (category != null) {
            parseColor(category.color)
        } else {
            getDefaultCategoryColor()
        }
    }

    /**
     * Gets category display name with proper fallback
     */
    fun getCategoryDisplayName(category: Category?, isUncategorized: Boolean): String {
        return when {
            isUncategorized -> "Uncategorized"
            category != null -> category.name
            else -> "No Category"
        }
    }

    /**
     * Determines if a category should be displayed as uncategorized style
     */
    fun shouldShowAsUncategorized(category: Category?, isUncategorized: Boolean): Boolean {
        return isUncategorized || category == null
    }

    /**
     * Gets a lighter version of the category color for backgrounds
     */
    fun getCategoryBackgroundColor(category: Category?, alpha: Float = 0.1f): Color {
        val baseColor = getCategoryColor(category)
        return baseColor.copy(alpha = alpha)
    }

    /**
     * Gets category icon if available, returns null if not set
     */
    fun getCategoryIcon(category: Category?): String? {
        return category?.icon?.takeIf { it.isNotBlank() }
    }

    /**
     * Validates if a color string is valid
     */
    fun isValidColorString(colorString: String?): Boolean {
        if (colorString.isNullOrBlank()) return false
        
        return try {
            when {
                colorString.startsWith("#") -> {
                    val cleanHex = colorString.removePrefix("#")
                    (cleanHex.length == 6 || cleanHex.length == 8) && isValidHex(cleanHex)
                }
                else -> {
                    getPredefinedColor(colorString) != getDefaultCategoryColor() || 
                    colorString.lowercase() == "other"
                }
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Cache for parsed colors to improve performance
     */
    private val colorCache = mutableMapOf<String, Color>()

    /**
     * Gets category color with caching for better performance
     */
    fun getCategoryColorCached(category: Category?): Color {
        if (category?.color == null) return getDefaultCategoryColor()
        
        return colorCache.getOrPut(category.color) {
            parseColor(category.color)
        }
    }

    /**
     * Clears the color cache (useful for testing or memory management)
     */
    fun clearColorCache() {
        colorCache.clear()
    }
}

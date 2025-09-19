package com.example.subcriptionmanagementapp.data.local

/**
 * Provides the default category definitions that should be available when the database is first
 * created, as well as shared defaults for custom categories created by the user at runtime.
 */
object CategoryDefaults {
    data class CategorySeed(
        val name: String,
        val colorHex: String,
        val keywords: String?
    )

    /** Default list of predefined categories inserted when the database is created. */
    val predefinedCategories = listOf(
        CategorySeed(
            name = "AI",
            colorHex = "#6366F1",
            keywords = "technology, artificial intelligence, machine learning"
        ),
        CategorySeed(
            name = "Life",
            colorHex = "#F59E0B",
            keywords = "lifestyle, personal growth, wellbeing"
        ),
        CategorySeed(
            name = "Work",
            colorHex = "#22C55E",
            keywords = "productivity, collaboration, tools"
        ),
        CategorySeed(
            name = "Learning",
            colorHex = "#3B82F6",
            keywords = "education, courses, skill building"
        )
    )

    /** Default color applied to user generated categories when none is specified. */
    const val DEFAULT_CUSTOM_CATEGORY_COLOR = "#64748B"
}

package com.example.subcriptionmanagementapp.ui.model

data class CategoryFilter(
    val id: Long,
    val name: String,
    val color: String,
    val isSelected: Boolean = false
) {
    companion object {
        val ALL_CATEGORIES = CategoryFilter(
            id = -1L,
            name = "All",
            color = "#6C757D",
            isSelected = true
        )
    }
}

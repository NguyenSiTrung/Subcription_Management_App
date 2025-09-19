package com.example.subcriptionmanagementapp.ui.model

data class FilterState(
    val selectedCategoryId: Long? = null,
    val selectedCategoryName: String? = null,
    val showActiveOnly: Boolean = false
)

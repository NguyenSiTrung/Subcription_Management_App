package com.example.subcriptionmanagementapp.data.repository

import com.example.subcriptionmanagementapp.data.local.entity.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun getCategoryById(id: Long): Category?
    suspend fun insertCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    fun getPredefinedCategories(): Flow<List<Category>>
    suspend fun getCategoriesByKeyword(keyword: String): List<Category>
}
package com.example.subcriptionmanagementapp.data.local.dao

import androidx.room.*
import com.example.subcriptionmanagementapp.data.local.entity.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): Category?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories WHERE is_predefined = 1")
    fun getPredefinedCategories(): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE keywords LIKE '%' || :keyword || '%'")
    suspend fun getCategoriesByKeyword(keyword: String): List<Category>
}
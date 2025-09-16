package com.example.subcriptionmanagementapp.data.repository

import com.example.subcriptionmanagementapp.data.local.dao.CategoryDao
import com.example.subcriptionmanagementapp.data.local.entity.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }

    override suspend fun getCategoryById(id: Long): Category? {
        return categoryDao.getCategoryById(id)
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category)
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    override fun getPredefinedCategories(): Flow<List<Category>> {
        return categoryDao.getPredefinedCategories()
    }

    override suspend fun getCategoriesByKeyword(keyword: String): List<Category> {
        return categoryDao.getCategoriesByKeyword(keyword)
    }
}
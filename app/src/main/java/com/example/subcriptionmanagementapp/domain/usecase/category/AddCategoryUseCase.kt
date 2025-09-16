package com.example.subcriptionmanagementapp.domain.usecase.category

import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.data.repository.CategoryRepository
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Long {
        return categoryRepository.insertCategory(category)
    }
}
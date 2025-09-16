package com.example.subcriptionmanagementapp.domain.usecase.category

import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.data.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(id: Long): Flow<Category?> {
        return categoryRepository.getCategoryById(id)
    }
}
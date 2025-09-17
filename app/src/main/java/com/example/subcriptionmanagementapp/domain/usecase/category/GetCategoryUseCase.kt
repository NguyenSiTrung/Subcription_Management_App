package com.example.subcriptionmanagementapp.domain.usecase.category

import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.data.repository.CategoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCategoryUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {
    operator fun invoke(id: Long): Flow<Category?> {
        return flow { emit(categoryRepository.getCategoryById(id)) }
    }
}

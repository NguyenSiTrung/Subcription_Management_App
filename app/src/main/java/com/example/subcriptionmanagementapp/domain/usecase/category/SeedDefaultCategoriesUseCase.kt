package com.example.subcriptionmanagementapp.domain.usecase.category

import com.example.subcriptionmanagementapp.data.local.CategoryDefaults
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.data.repository.CategoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class SeedDefaultCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke() {
        val existing = categoryRepository.getAllCategories().first()
        val missingSeeds = CategoryDefaults.predefinedCategories.filter { seed ->
            existing.none { it.name.equals(seed.name, ignoreCase = true) }
        }
        if (missingSeeds.isEmpty()) return

        val timestamp = System.currentTimeMillis()
        missingSeeds.forEach { seed ->
            categoryRepository.insertCategory(
                Category(
                    name = seed.name,
                    color = seed.colorHex,
                    icon = null,
                    isPredefined = true,
                    keywords = seed.keywords,
                    createdAt = timestamp,
                    updatedAt = timestamp
                )
            )
        }
    }
}

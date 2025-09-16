package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.domain.usecase.category.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val addCategoryUseCase: AddCategoryUseCase,
    private val getCategoryUseCase: GetCategoryUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getAllCategoriesUseCase()
                    .collect { categoryList ->
                        _categories.value = categoryList
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load categories"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadCategory(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getCategoryUseCase(id)
                    .collect { category ->
                        _selectedCategory.value = category
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load category"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                addCategoryUseCase(category)
                loadCategories()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add category"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                updateCategoryUseCase(category)
                loadCategories()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update category"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                deleteCategoryUseCase(category)
                loadCategories()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete category"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
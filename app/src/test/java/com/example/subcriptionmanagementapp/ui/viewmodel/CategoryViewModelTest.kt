package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.domain.usecase.category.*
import com.example.subcriptionmanagementapp.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class CategoryViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var addCategoryUseCase: AddCategoryUseCase

    @Mock
    private lateinit var getCategoryUseCase: GetCategoryUseCase

    @Mock
    private lateinit var getAllCategoriesUseCase: GetAllCategoriesUseCase

    @Mock
    private lateinit var updateCategoryUseCase: UpdateCategoryUseCase

    @Mock
    private lateinit var deleteCategoryUseCase: DeleteCategoryUseCase

    private lateinit var viewModel: CategoryViewModel

    @Before
    fun setUp() {
        viewModel = CategoryViewModel(
            addCategoryUseCase,
            getCategoryUseCase,
            getAllCategoriesUseCase,
            updateCategoryUseCase,
            deleteCategoryUseCase
        )
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(
            addCategoryUseCase,
            getCategoryUseCase,
            getAllCategoriesUseCase,
            updateCategoryUseCase,
            deleteCategoryUseCase
        )
    }

    @Test
    fun `loadCategories should update categories state`() = runTest {
        // Given
        val categories = listOf(
            Category(
                id = 1,
                name = "Entertainment",
                color = "#FF0000",
                isActive = true
            ),
            Category(
                id = 2,
                name = "Productivity",
                color = "#00FF00",
                isActive = true
            )
        )
        
        whenever(getAllCategoriesUseCase()).thenReturn(flow { emit(categories) })

        // When
        viewModel.loadCategories()

        // Then
        viewModel.categories.test {
            assertEquals(categories, awaitItem())
            ensureAllEventsConsumed()
        }
        
        verify(getAllCategoriesUseCase).invoke()
    }

    @Test
    fun `loadCategory should update selectedCategory state`() = runTest {
        // Given
        val category = Category(
            id = 1,
            name = "Entertainment",
            color = "#FF0000",
            isActive = true
        )
        
        whenever(getCategoryUseCase(1)).thenReturn(flow { emit(category) })

        // When
        viewModel.loadCategory(1)

        // Then
        viewModel.selectedCategory.test {
            assertEquals(category, awaitItem())
            ensureAllEventsConsumed()
        }
        
        verify(getCategoryUseCase).invoke(1)
    }

    @Test
    fun `addCategory should call use case and reload categories`() = runTest {
        // Given
        val category = Category(
            id = 0,
            name = "Entertainment",
            color = "#FF0000",
            isActive = true
        )
        
        val categories = listOf(category.copy(id = 1))
        whenever(getAllCategoriesUseCase()).thenReturn(flow { emit(categories) })

        // When
        viewModel.addCategory(category)

        // Then
        verify(addCategoryUseCase).invoke(category)
        verify(getAllCategoriesUseCase).invoke()
        
        viewModel.categories.test {
            assertEquals(categories, awaitItem())
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `updateCategory should call use case and reload categories`() = runTest {
        // Given
        val category = Category(
            id = 1,
            name = "Entertainment",
            color = "#FF0000",
            isActive = true
        )
        
        val categories = listOf(category)
        whenever(getAllCategoriesUseCase()).thenReturn(flow { emit(categories) })

        // When
        viewModel.updateCategory(category)

        // Then
        verify(updateCategoryUseCase).invoke(category)
        verify(getAllCategoriesUseCase).invoke()
        
        viewModel.categories.test {
            assertEquals(categories, awaitItem())
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `deleteCategory should call use case and reload categories`() = runTest {
        // Given
        val category = Category(
            id = 1,
            name = "Entertainment",
            color = "#FF0000",
            isActive = true
        )
        
        val categories = emptyList<Category>()
        whenever(getAllCategoriesUseCase()).thenReturn(flow { emit(categories) })

        // When
        viewModel.deleteCategory(category)

        // Then
        verify(deleteCategoryUseCase).invoke(category)
        verify(getAllCategoriesUseCase).invoke()
        
        viewModel.categories.test {
            assertEquals(categories, awaitItem())
            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `clearError should set error state to null`() = runTest {
        // Given
        viewModel._error.value = "Test error"

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.error.value)
    }
}
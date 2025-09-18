package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.domain.usecase.category.AddCategoryUseCase
import com.example.subcriptionmanagementapp.domain.usecase.category.DeleteCategoryUseCase
import com.example.subcriptionmanagementapp.domain.usecase.category.GetAllCategoriesUseCase
import com.example.subcriptionmanagementapp.domain.usecase.category.GetCategoryUseCase
import com.example.subcriptionmanagementapp.domain.usecase.category.UpdateCategoryUseCase
import com.example.subcriptionmanagementapp.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

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
        whenever(getAllCategoriesUseCase()).thenReturn(flowOf(emptyList()))

        viewModel = CategoryViewModel(
            addCategoryUseCase,
            getCategoryUseCase,
            getAllCategoriesUseCase,
            updateCategoryUseCase,
            deleteCategoryUseCase
        )

        clearInvocations(
            addCategoryUseCase,
            getCategoryUseCase,
            getAllCategoriesUseCase,
            updateCategoryUseCase,
            deleteCategoryUseCase
        )
    }

    @Test
    fun `loadCategories should update categories state`() = runTest {
        val categories = listOf(
            sampleCategory(id = 1, name = "Entertainment", color = "#FF0000"),
            sampleCategory(id = 2, name = "Productivity", color = "#00FF00")
        )

        whenever(getAllCategoriesUseCase()).thenReturn(flowOf(categories))

        viewModel.loadCategories()

        viewModel.categories.test {
            skipItems(1)
            assertEquals(categories, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        verify(getAllCategoriesUseCase).invoke()
    }

    @Test
    fun `loadCategory should update selectedCategory state`() = runTest {
        val category = sampleCategory(id = 1, name = "Entertainment", color = "#FF0000")

        whenever(getCategoryUseCase(1)).thenReturn(flowOf(category))

        viewModel.loadCategory(1)

        viewModel.selectedCategory.test {
            skipItems(1)
            assertEquals(category, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }

        verify(getCategoryUseCase).invoke(1)
    }

    @Test
    fun `addCategory should call use case and reload categories`() = runTest {
        val category = sampleCategory(id = 0, name = "Entertainment")
        val categories = listOf(category.copy(id = 1))

        whenever(getAllCategoriesUseCase()).thenReturn(flowOf(categories))

        viewModel.addCategory(category)

        verify(addCategoryUseCase).invoke(category)
        verify(getAllCategoriesUseCase).invoke()

        viewModel.categories.test {
            skipItems(1)
            assertEquals(categories, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `updateCategory should call use case and reload categories`() = runTest {
        val category = sampleCategory(id = 1, name = "Entertainment")

        whenever(getAllCategoriesUseCase()).thenReturn(flowOf(listOf(category)))

        viewModel.updateCategory(category)

        verify(updateCategoryUseCase).invoke(category)
        verify(getAllCategoriesUseCase).invoke()

        viewModel.categories.test {
            skipItems(1)
            assertEquals(listOf(category), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteCategory should call use case and reload categories`() = runTest {
        val category = sampleCategory(id = 1, name = "Entertainment")

        whenever(getAllCategoriesUseCase()).thenReturn(flowOf(emptyList()))

        viewModel.deleteCategory(category)

        verify(deleteCategoryUseCase).invoke(category)
        verify(getAllCategoriesUseCase).invoke()

        viewModel.categories.test {
            skipItems(1)
            assertEquals(emptyList<Category>(), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clearError should set error state to null`() = runTest {
        val exception = IllegalStateException("Save failed")
        val category = sampleCategory(id = 0)

        doAnswer { throw exception }.`when`(addCategoryUseCase).invoke(category)

        viewModel.addCategory(category)
        assertEquals(exception.message, viewModel.error.value)

        viewModel.clearError()

        assertNull(viewModel.error.value)
    }

    private fun sampleCategory(
        id: Long,
        name: String = "Category $id",
        color: String = "#FF0000"
    ): Category = Category(
        id = id,
        name = name,
        color = color,
        icon = null,
        isPredefined = false,
        keywords = null,
        createdAt = DEFAULT_TIMESTAMP,
        updatedAt = DEFAULT_TIMESTAMP
    )

    private companion object {
        const val DEFAULT_TIMESTAMP = 1_700_000_000_000L
    }
}

package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.subcriptionmanagementapp.domain.usecase.settings.ObserveDarkModeUseCase
import com.example.subcriptionmanagementapp.domain.usecase.settings.UpdateDarkModeUseCase
import com.example.subcriptionmanagementapp.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.coEvery
import org.mockito.kotlin.coVerify
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class SettingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var observeDarkModeUseCase: ObserveDarkModeUseCase

    @Mock
    private lateinit var updateDarkModeUseCase: UpdateDarkModeUseCase

    private lateinit var darkModeFlow: MutableSharedFlow<Boolean>

    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        darkModeFlow = MutableSharedFlow()
        whenever(observeDarkModeUseCase()).thenReturn(darkModeFlow)
        viewModel = SettingsViewModel(
            observeDarkModeUseCase,
            updateDarkModeUseCase
        )
    }

    @Test
    fun `uiState updates when dark mode flow emits`() = runTest {
        darkModeFlow.emit(true)
        advanceUntilIdle()

        verify(observeDarkModeUseCase).invoke()
        assertTrue(viewModel.uiState.value.isDarkMode)
    }

    @Test
    fun `onDarkModeToggled triggers use case and resets loading`() = runTest {
        coEvery { updateDarkModeUseCase(true) } returns Unit

        darkModeFlow.emit(false)
        advanceUntilIdle()

        viewModel.onDarkModeToggled(true)
        advanceUntilIdle()

        coVerify { updateDarkModeUseCase(true) }
        assertTrue(viewModel.uiState.value.isDarkMode)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `onDarkModeToggled skips repository call when state unchanged`() = runTest {
        darkModeFlow.emit(true)
        advanceUntilIdle()

        viewModel.onDarkModeToggled(true)
        advanceUntilIdle()

        coVerify(exactly = 0) { updateDarkModeUseCase.invoke(any()) }
    }

    @Test
    fun `onDarkModeToggled surfaces error when update fails`() = runTest {
        val exception = IllegalStateException("Update failed")
        coEvery { updateDarkModeUseCase(true) } throws exception

        darkModeFlow.emit(false)
        advanceUntilIdle()

        viewModel.onDarkModeToggled(true)
        advanceUntilIdle()

        assertEquals(exception.message, viewModel.uiState.value.errorMessage)

        viewModel.clearError()
        assertNull(viewModel.uiState.value.errorMessage)
    }
}

package com.example.subcriptionmanagementapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subcriptionmanagementapp.domain.usecase.settings.ObserveDarkModeUseCase
import com.example.subcriptionmanagementapp.domain.usecase.settings.UpdateDarkModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val observeDarkModeUseCase: ObserveDarkModeUseCase,
    private val updateDarkModeUseCase: UpdateDarkModeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeDarkModeChanges()
    }

    private fun observeDarkModeChanges() {
        observeDarkModeUseCase()
            .onEach { isDarkMode ->
                _uiState.update { current ->
                    current.copy(isDarkMode = isDarkMode)
                }
            }
            .catch { throwable ->
                _uiState.update { current ->
                    current.copy(errorMessage = throwable.message)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onDarkModeToggled(enabled: Boolean) {
        val currentState = _uiState.value
        if (!currentState.isLoading && currentState.isDarkMode == enabled) {
            return
        }

        viewModelScope.launch {
            val previousDarkMode = currentState.isDarkMode
            _uiState.update { current ->
                current.copy(
                    isDarkMode = enabled,
                    isLoading = true,
                    errorMessage = null
                )
            }

            runCatching { updateDarkModeUseCase(enabled) }
                .onFailure { exception ->
                    _uiState.update { current ->
                        current.copy(
                            isDarkMode = previousDarkMode,
                            errorMessage = exception.message ?: "Unable to update dark mode"
                        )
                    }
                }

            _uiState.update { current -> current.copy(isLoading = false) }
        }
    }

    fun clearError() {
        _uiState.update { current -> current.copy(errorMessage = null) }
    }
}

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

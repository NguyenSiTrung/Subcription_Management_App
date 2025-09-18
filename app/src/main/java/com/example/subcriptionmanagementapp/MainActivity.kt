package com.example.subcriptionmanagementapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.subcriptionmanagementapp.ui.navigation.AppNavigation
import com.example.subcriptionmanagementapp.ui.theme.SubcriptionManagementAppTheme
import com.example.subcriptionmanagementapp.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()

            SubcriptionManagementAppTheme(darkTheme = settingsUiState.isDarkMode) {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
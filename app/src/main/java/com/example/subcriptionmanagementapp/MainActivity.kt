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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.subcriptionmanagementapp.ui.components.AppBottomBar
import com.example.subcriptionmanagementapp.ui.navigation.AppNavigation
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.theme.SubscriptionManagementAppTheme
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

            SubscriptionManagementAppTheme(darkTheme = settingsUiState.isDarkMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        // Only show bottom bar for main screens
                        when (currentRoute?.substringBefore("/")) {
                            Screen.Home.route,
                            Screen.SubscriptionList.route,
                            Screen.CategoryList.route,
                            Screen.Statistics.route,
                            Screen.Settings.route -> {
                                AppBottomBar(
                                    navController = navController,
                                    currentRoute = currentRoute
                                )
                            }
                            else -> {}
                        }
                    }
                ) { paddingValues ->
                    AppNavigation(
                        navController = navController,
                        settingsViewModel = settingsViewModel,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}
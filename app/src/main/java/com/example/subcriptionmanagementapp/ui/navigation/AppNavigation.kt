package com.example.subcriptionmanagementapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.subcriptionmanagementapp.ui.screens.HomeScreen
import com.example.subcriptionmanagementapp.ui.screens.subscriptions.SubscriptionListScreen
import com.example.subcriptionmanagementapp.ui.screens.subscriptions.SubscriptionDetailScreen
import com.example.subcriptionmanagementapp.ui.screens.subscriptions.AddEditSubscriptionScreen
import com.example.subcriptionmanagementapp.ui.screens.categories.CategoryListScreen
import com.example.subcriptionmanagementapp.ui.screens.statistics.StatisticsScreen
import com.example.subcriptionmanagementapp.ui.screens.settings.SettingsScreen
import com.example.subcriptionmanagementapp.ui.screens.about.AboutScreen
import com.example.subcriptionmanagementapp.ui.viewmodel.SettingsViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
    paddingValues: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.SubscriptionList.route) {
            SubscriptionListScreen(navController = navController)
        }
        composable(
            route = Screen.SubscriptionDetail.route,
            arguments = listOf(
                navArgument("subscriptionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val subscriptionId = backStackEntry.arguments?.getLong("subscriptionId")
            subscriptionId?.let {
                SubscriptionDetailScreen(
                    navController = navController,
                    subscriptionId = it
                )
            }
        }
        composable(
            route = Screen.AddEditSubscription.route,
            arguments = listOf(
                navArgument("subscriptionId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val subscriptionId =
                backStackEntry.arguments?.getLong("subscriptionId")?.takeIf { it != -1L }

            AddEditSubscriptionScreen(
                navController = navController,
                subscriptionId = subscriptionId
            )
        }
        composable(Screen.CategoryList.route) {
            CategoryListScreen(navController = navController)
        }
        composable(Screen.Statistics.route) {
            StatisticsScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                navController = navController,
                viewModel = settingsViewModel
            )
        }
        composable(Screen.About.route) {
            AboutScreen(navController = navController)
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object SubscriptionList : Screen("subscription_list")
    object SubscriptionDetail : Screen("subscription_detail/{subscriptionId}") {
        fun createRoute(subscriptionId: Long) = "subscription_detail/$subscriptionId"
    }
    object AddEditSubscription : Screen("add_edit_subscription/{subscriptionId}") {
        fun createRoute(subscriptionId: Long) = "add_edit_subscription/$subscriptionId"
    }
    object CategoryList : Screen("category_list")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
    object About : Screen("about")
}
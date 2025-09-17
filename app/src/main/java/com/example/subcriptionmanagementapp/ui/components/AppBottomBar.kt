package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.ui.navigation.Screen

@Composable
fun AppBottomBar(navController: NavController, currentRoute: String?) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.screen.route
            NavigationBarItem(
                    icon = { Icon(item.icon, contentDescription = null) },
                    label = { Text(stringResource(item.labelResId)) },
                    selected = isSelected,
                    onClick = {
                        if (!isSelected) {
                            navController.navigate(item.screen.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
            )
        }
    }
}

data class BottomNavItem(val screen: Screen, val icon: ImageVector, val labelResId: Int)

val bottomNavItems =
        listOf(
                BottomNavItem(
                        screen = Screen.Home,
                        icon = Icons.Default.Home,
                        labelResId = R.string.home
                ),
                BottomNavItem(
                        screen = Screen.SubscriptionList,
                        icon = Icons.AutoMirrored.Filled.List,
                        labelResId = R.string.subscriptions
                ),
                BottomNavItem(
                        screen = Screen.CategoryList,
                        icon = Icons.Default.Menu,
                        labelResId = R.string.categories
                ),
                BottomNavItem(
                        screen = Screen.Statistics,
                        icon = Icons.Default.Star,
                        labelResId = R.string.statistics
                ),
                BottomNavItem(
                        screen = Screen.Settings,
                        icon = Icons.Default.Settings,
                        labelResId = R.string.settings
                )
        )

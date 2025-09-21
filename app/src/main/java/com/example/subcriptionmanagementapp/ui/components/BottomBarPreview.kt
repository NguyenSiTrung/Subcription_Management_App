package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.theme.SubscriptionManagementAppTheme

@Preview(name = "Bottom Bar - Light Mode", showBackground = true)
@Composable
private fun BottomBarLightPreview() {
    SubscriptionManagementAppTheme(darkTheme = false) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
            ) {
                val navController = rememberNavController()
                AppBottomBar(navController = navController, currentRoute = Screen.SubscriptionList.route)
            }
        }
    }
}

@Preview(name = "Bottom Bar - Dark Mode", showBackground = true)
@Composable
private fun BottomBarDarkPreview() {
    SubscriptionManagementAppTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
            ) {
                val navController = rememberNavController()
                AppBottomBar(navController = navController, currentRoute = Screen.SubscriptionList.route)
            }
        }
    }
}

@Preview(name = "Bottom Bar - Subscriptions Selected Light", showBackground = true)
@Composable
private fun BottomBarSubscriptionsLightPreview() {
    SubscriptionManagementAppTheme(darkTheme = false) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
            ) {
                val navController = rememberNavController()
                AppBottomBar(
                        navController = navController,
                        currentRoute = Screen.SubscriptionList.route
                )
            }
        }
    }
}

@Preview(name = "Bottom Bar - Subscriptions Selected Dark", showBackground = true)
@Composable
private fun BottomBarSubscriptionsDarkPreview() {
    SubscriptionManagementAppTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
            ) {
                val navController = rememberNavController()
                AppBottomBar(
                        navController = navController,
                        currentRoute = Screen.SubscriptionList.route
                )
            }
        }
    }
}

@Preview(name = "Bottom Bar - Full Scaffold Light", showBackground = true, showSystemUi = true)
@Composable
private fun BottomBarInScaffoldLightPreview() {
    SubscriptionManagementAppTheme(darkTheme = false) {
        val navController = rememberNavController()
        Scaffold(
                bottomBar = {
                    AppBottomBar(
                            navController = navController,
                            currentRoute = Screen.Statistics.route
                    )
                }
        ) { paddingValues ->
            Surface(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    color = MaterialTheme.colorScheme.background
            ) {
                // Empty content for preview
            }
        }
    }
}

@Preview(name = "Bottom Bar - Full Scaffold Dark", showBackground = true, showSystemUi = true)
@Composable
private fun BottomBarInScaffoldDarkPreview() {
    SubscriptionManagementAppTheme(darkTheme = true) {
        val navController = rememberNavController()
        Scaffold(
                bottomBar = {
                    AppBottomBar(
                            navController = navController,
                            currentRoute = Screen.Settings.route
                    )
                }
        ) { paddingValues ->
            Surface(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    color = MaterialTheme.colorScheme.background
            ) {
                // Empty content for preview
            }
        }
    }
}

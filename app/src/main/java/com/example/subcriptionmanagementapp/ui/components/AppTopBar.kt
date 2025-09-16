package com.example.subcriptionmanagementapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    navController: NavController,
    currentRoute: String?,
    showBackButton: Boolean = false,
    showActions: Boolean = true,
    onSearchClick: (() -> Unit)? = null,
    onAddClick: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }
    
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                }
            }
        },
        actions = {
            if (showActions) {
                when (currentRoute) {
                    Screen.SubscriptionList.route -> {
                        IconButton(onClick = { onSearchClick?.invoke() }) {
                            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                        }
                        IconButton(onClick = { onAddClick?.invoke() }) {
                            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
                        }
                    }
                    Screen.CategoryList.route -> {
                        IconButton(onClick = { onAddClick?.invoke() }) {
                            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
                        }
                    }
                }
                
                IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more_options))
                }
                
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.settings)) },
                        onClick = {
                            showMenu = false
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.about)) },
                        onClick = {
                            showMenu = false
                            // Navigate to about screen
                        }
                    )
                }
            }
        }
    )
}
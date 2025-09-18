package com.example.subcriptionmanagementapp.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.ui.components.AppTopBar
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var notificationsEnabled by remember { mutableStateOf(true) }
    var currency by remember { mutableStateOf("USD") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearError()
    }

    Scaffold(
            topBar = {
                AppTopBar(
                        title = stringResource(R.string.settings),
                        navController = navController,
                        currentRoute = Screen.Settings.route,
                        showBackButton = true,
                        showActions = false
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                        text = stringResource(R.string.general),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Dark mode setting
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                        text = stringResource(R.string.dark_mode),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )

                                Text(
                                        text = stringResource(R.string.dark_mode_description),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Switch(
                                    checked = uiState.isDarkMode,
                                    onCheckedChange = { isChecked -> viewModel.onDarkModeToggled(isChecked) },
                                    enabled = !uiState.isLoading
                            )
                        }

                        HorizontalDivider()

                        // Currency setting
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                        text = stringResource(R.string.currency),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )

                                Text(
                                        text = stringResource(R.string.currency_description),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                    text = currency,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                Text(
                        text = stringResource(R.string.notifications),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Notifications enabled setting
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                        text = stringResource(R.string.enable_notifications),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )

                                Text(
                                        text =
                                                stringResource(
                                                        R.string.enable_notifications_description
                                                ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Switch(
                                    checked = notificationsEnabled,
                                    onCheckedChange = { notificationsEnabled = it }
                            )
                        }

                        if (notificationsEnabled) {
                            HorizontalDivider()

                            // Reminder time setting
                            Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                            text = stringResource(R.string.reminder_time),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                            text =
                                                    stringResource(
                                                            R.string.reminder_time_description
                                                    ),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Text(
                                        text = "09:00",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                        text = stringResource(R.string.data),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Backup data setting
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                        text = stringResource(R.string.backup_data),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )

                                Text(
                                        text = stringResource(R.string.backup_data_description),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(onClick = { /* Backup data */}) {
                                Text(stringResource(R.string.backup))
                            }
                        }

                        HorizontalDivider()

                        // Restore data setting
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                        text = stringResource(R.string.restore_data),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )

                                Text(
                                        text = stringResource(R.string.restore_data_description),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            OutlinedButton(onClick = { /* Restore data */}) {
                                Text(stringResource(R.string.restore))
                            }
                        }

                        HorizontalDivider()

                        // Clear data setting
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                        text = stringResource(R.string.clear_data),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                )

                                Text(
                                        text = stringResource(R.string.clear_data_description),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            OutlinedButton(
                                    onClick = { /* Clear data */},
                                    colors =
                                            ButtonDefaults.outlinedButtonColors(
                                                    contentColor = MaterialTheme.colorScheme.error
                                            )
                            ) { Text(stringResource(R.string.clear)) }
                        }
                    }
                }
            }

            item {
                Text(
                        text = stringResource(R.string.about),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // App version
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                    text = stringResource(R.string.app_version),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                            )

                            Text(
                                    text = "1.0.0",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        HorizontalDivider()

                        // Privacy policy
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                    text = stringResource(R.string.privacy_policy),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                            )

                            Text(
                                    text = ">",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        HorizontalDivider()

                        // Terms of service
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                    text = stringResource(R.string.terms_of_service),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                            )

                            Text(
                                    text = ">",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

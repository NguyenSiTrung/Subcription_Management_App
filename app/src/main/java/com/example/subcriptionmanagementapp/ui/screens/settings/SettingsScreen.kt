package com.example.subcriptionmanagementapp.ui.screens.settings

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.ui.components.AppTopBar
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.viewmodel.BackupUiEvent
import com.example.subcriptionmanagementapp.ui.viewmodel.BackupViewModel
import com.example.subcriptionmanagementapp.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
        navController: NavController,
        viewModel: SettingsViewModel = hiltViewModel(),
        backupViewModel: BackupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isBackupLoading by backupViewModel.isLoading.collectAsStateWithLifecycle()
    var notificationsEnabled by remember { mutableStateOf(true) }
    var currency by remember { mutableStateOf("USD") }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isBackupMenuExpanded by remember { mutableStateOf(false) }

    val restoreLauncher =
            rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode != Activity.RESULT_OK)
                        return@rememberLauncherForActivityResult
                val uri = result.data?.data ?: return@rememberLauncherForActivityResult
                backupViewModel.restoreBackup(uri)
            }

    val exportLauncher =
            rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.CreateDocument("application/json")
            ) { uri ->
                if (uri != null) {
                    backupViewModel.exportBackup(uri)
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                                context.getString(R.string.backup_save_cancelled)
                        )
                    }
                }
            }

    LaunchedEffect(backupViewModel) {
        backupViewModel.events.collect { event ->
            when (event) {
                is BackupUiEvent.ShareBackup -> {
                    val shareIntent =
                            Intent.createChooser(
                                    backupViewModel.getShareBackupIntent(event.uri),
                                    context.getString(R.string.backup_share_title)
                            )
                    val result = runCatching { context.startActivity(shareIntent) }
                    if (result.isFailure) {
                        snackbarHostState.showSnackbar(
                                context.getString(R.string.backup_share_unavailable)
                        )
                    }
                }
                is BackupUiEvent.RequestExport -> exportLauncher.launch(event.suggestedFileName)
                is BackupUiEvent.Success ->
                        snackbarHostState.showSnackbar(context.getString(event.messageResId))
                is BackupUiEvent.Error ->
                        snackbarHostState.showSnackbar(context.getString(event.messageResId))
            }
        }
    }

    LaunchedEffect(isBackupLoading) {
        if (isBackupLoading) {
            isBackupMenuExpanded = false
        }
    }

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
                                    onCheckedChange = { isChecked ->
                                        viewModel.onDarkModeToggled(isChecked)
                                    },
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

                            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                                Button(
                                        onClick = { isBackupMenuExpanded = true },
                                        enabled = !isBackupLoading
                                ) { Text(stringResource(R.string.backup)) }

                                DropdownMenu(
                                        expanded = isBackupMenuExpanded,
                                        onDismissRequest = { isBackupMenuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                            text = { Text(stringResource(R.string.backup_share)) },
                                            leadingIcon = {
                                                Icon(Icons.Filled.Share, contentDescription = null)
                                            },
                                            enabled = !isBackupLoading,
                                            onClick = {
                                                isBackupMenuExpanded = false
                                                backupViewModel.onShareBackupClicked()
                                            }
                                    )
                                    DropdownMenuItem(
                                            text = {
                                                Text(stringResource(R.string.backup_save_to_device))
                                            },
                                            leadingIcon = {
                                                Icon(
                                                        Icons.Filled.Download,
                                                        contentDescription = null
                                                )
                                            },
                                            enabled = !isBackupLoading,
                                            onClick = {
                                                isBackupMenuExpanded = false
                                                backupViewModel.onSaveBackupClicked()
                                            }
                                    )
                                }
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

                            OutlinedButton(
                                    onClick = {
                                        val intent = backupViewModel.getBackupFilePickerIntent()
                                        if (intent.resolveActivity(context.packageManager) != null
                                        ) {
                                            restoreLauncher.launch(intent)
                                        } else {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar(
                                                        context.getString(
                                                                R.string.backup_picker_unavailable
                                                        )
                                                )
                                            }
                                        }
                                    },
                                    enabled = !isBackupLoading
                            ) { Text(stringResource(R.string.restore)) }
                        }

                        HorizontalDivider()

                        if (isBackupLoading) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }

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
                                    enabled = !isBackupLoading,
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

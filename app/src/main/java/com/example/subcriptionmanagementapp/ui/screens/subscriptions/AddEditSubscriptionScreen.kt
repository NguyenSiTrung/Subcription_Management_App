package com.example.subcriptionmanagementapp.ui.screens.subscriptions

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
import com.example.subcriptionmanagementapp.data.local.entity.Category
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.ui.components.AppTopBar
import com.example.subcriptionmanagementapp.ui.components.ErrorMessage
import com.example.subcriptionmanagementapp.ui.components.LoadingIndicator
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.viewmodel.CategoryViewModel
import com.example.subcriptionmanagementapp.ui.viewmodel.SubscriptionViewModel
import com.example.subcriptionmanagementapp.util.formatCurrency
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubscriptionScreen(
    navController: NavController,
    subscriptionId: Long?,
    subscriptionViewModel: SubscriptionViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    categoryViewModel: CategoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val categories by categoryViewModel.categories.collectAsStateWithLifecycle()
    val isLoadingCategories by categoryViewModel.isLoading.collectAsStateWithLifecycle()
    val errorCategories by categoryViewModel.error.collectAsStateWithLifecycle()
    
    val subscription by subscriptionViewModel.selectedSubscription.collectAsStateWithLifecycle()
    val isLoadingSubscription by subscriptionViewModel.isLoading.collectAsStateWithLifecycle()
    val errorSubscription by subscriptionViewModel.error.collectAsStateWithLifecycle()
    
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var billingCycle by remember { mutableStateOf(BillingCycle.MONTHLY) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var nextBillingDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var isActive by remember { mutableStateOf(true) }
    var reminderDays by remember { mutableStateOf(3) }
    var websiteUrl by remember { mutableStateOf("") }
    var appPackageName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    LaunchedEffect(subscriptionId) {
        if (subscriptionId != null) {
            subscriptionViewModel.loadSubscription(subscriptionId)
        }
    }
    
    LaunchedEffect(subscription) {
        subscription?.let {
            name = it.name
            description = it.description ?: ""
            price = it.price.toString()
            billingCycle = it.billingCycle
            nextBillingDate = it.nextBillingDate
            isActive = it.isActive
            reminderDays = it.reminderDays
            websiteUrl = it.websiteUrl ?: ""
            appPackageName = it.appPackageName ?: ""
            notes = it.notes ?: ""
        }
    }
    
    LaunchedEffect(Unit) {
        categoryViewModel.loadCategories()
    }
    
    LaunchedEffect(errorCategories) {
        if (errorCategories != null) {
            Toast.makeText(context, errorCategories, Toast.LENGTH_SHORT).show()
            categoryViewModel.clearError()
        }
    }
    
    LaunchedEffect(errorSubscription) {
        if (errorSubscription != null) {
            Toast.makeText(context, errorSubscription, Toast.LENGTH_SHORT).show()
            subscriptionViewModel.clearError()
        }
    }
    
    val isLoading = isLoadingCategories || isLoadingSubscription
    val error = errorCategories ?: errorSubscription
    
    Scaffold(
        topBar = {
            AppTopBar(
                title = if (subscriptionId != null) stringResource(R.string.edit_subscription) else stringResource(R.string.add_subscription),
                navController = navController,
                currentRoute = Screen.AddEditSubscription.route,
                showBackButton = true,
                showActions = false
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> LoadingIndicator()
                error != null -> ErrorMessage(message = error!!)
                else -> AddEditSubscriptionContent(
                    name = name,
                    onNameChange = { name = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    price = price,
                    onPriceChange = { price = it },
                    billingCycle = billingCycle,
                    onBillingCycleChange = { billingCycle = it },
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategoryChange = { selectedCategory = it },
                    nextBillingDate = nextBillingDate,
                    onNextBillingDateChange = { nextBillingDate = it },
                    isActive = isActive,
                    onIsActiveChange = { isActive = it },
                    reminderDays = reminderDays,
                    onReminderDaysChange = { reminderDays = it },
                    websiteUrl = websiteUrl,
                    onWebsiteUrlChange = { websiteUrl = it },
                    appPackageName = appPackageName,
                    onAppPackageNameChange = { appPackageName = it },
                    notes = notes,
                    onNotesChange = { notes = it },
                    onSaveClick = {
                        if (validateInputs(name, price)) {
                            val subscription = Subscription(
                                id = subscriptionId ?: 0,
                                name = name,
                                description = description.ifBlank { null },
                                price = price.toDouble(),
                                billingCycle = billingCycle,
                                categoryId = selectedCategory?.id,
                                nextBillingDate = nextBillingDate,
                                isActive = isActive,
                                reminderDays = reminderDays,
                                websiteUrl = websiteUrl.ifBlank { null },
                                appPackageName = appPackageName.ifBlank { null },
                                notes = notes.ifBlank { null },
                                createdAt = if (subscriptionId != null) subscription!!.createdAt else System.currentTimeMillis(),
                                updatedAt = System.currentTimeMillis()
                            )
                            
                            if (subscriptionId != null) {
                                subscriptionViewModel.updateSubscription(subscription)
                            } else {
                                subscriptionViewModel.addSubscription(subscription)
                            }
                            
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, R.string.please_fill_required_fields, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubscriptionContent(
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    price: String,
    onPriceChange: (String) -> Unit,
    billingCycle: BillingCycle,
    onBillingCycleChange: (BillingCycle) -> Unit,
    categories: List<Category>,
    selectedCategory: Category?,
    onCategoryChange: (Category?) -> Unit,
    nextBillingDate: Long,
    onNextBillingDateChange: (Long) -> Unit,
    isActive: Boolean,
    onIsActiveChange: (Boolean) -> Unit,
    reminderDays: Int,
    onReminderDaysChange: (Int) -> Unit,
    websiteUrl: String,
    onWebsiteUrlChange: (String) -> Unit,
    appPackageName: String,
    onAppPackageNameChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Name field
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = name.isBlank()
        )
        
        // Description field
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(stringResource(R.string.description)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        
        // Price field
        OutlinedTextField(
            value = price,
            onValueChange = onPriceChange,
            label = { Text(stringResource(R.string.price)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = price.isBlank() || price.toDoubleOrNull() == null || price.toDouble() <= 0,
            prefix = { Text("$") }
        )
        
        // Billing cycle dropdown
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { }
        ) {
            OutlinedTextField(
                value = when (billingCycle) {
                    BillingCycle.DAILY -> stringResource(R.string.daily)
                    BillingCycle.WEEKLY -> stringResource(R.string.weekly)
                    BillingCycle.MONTHLY -> stringResource(R.string.monthly)
                    BillingCycle.YEARLY -> stringResource(R.string.yearly)
                },
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(R.string.billing_cycle)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) }
            )
            
            DropdownMenu(
                expanded = false,
                onDismissRequest = { }
            ) {
                BillingCycle.values().forEach { cycle ->
                    DropdownMenuItem(
                        text = { Text(when (cycle) {
                            BillingCycle.DAILY -> stringResource(R.string.daily)
                            BillingCycle.WEEKLY -> stringResource(R.string.weekly)
                            BillingCycle.MONTHLY -> stringResource(R.string.monthly)
                            BillingCycle.YEARLY -> stringResource(R.string.yearly)
                        }) },
                        onClick = { onBillingCycleChange(cycle) }
                    )
                }
            }
        }
        
        // Category dropdown
        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { }
        ) {
            OutlinedTextField(
                value = selectedCategory?.name ?: "",
                onValueChange = { },
                readOnly = true,
                label = { Text(stringResource(R.string.category)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) }
            )
            
            DropdownMenu(
                expanded = false,
                onDismissRequest = { }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.no_category)) },
                    onClick = { onCategoryChange(null) }
                )
                
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = { onCategoryChange(category) }
                    )
                }
            }
        }
        
        // Next billing date
        OutlinedTextField(
            value = java.text.SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date(nextBillingDate)),
            onValueChange = { },
            readOnly = true,
            label = { Text(stringResource(R.string.next_billing_date)) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = { 
                IconButton(onClick = { /* Show date picker */ }) {
                    // Icon for date picker
                }
            }
        )
        
        // Active switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.active),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Switch(
                checked = isActive,
                onCheckedChange = onIsActiveChange
            )
        }
        
        // Reminder days
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.reminder_days_before),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (reminderDays > 0) onReminderDaysChange(reminderDays - 1) },
                    enabled = reminderDays > 0
                ) {
                    // Minus icon
                }
                
                Text(
                    text = reminderDays.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = { onReminderDaysChange(reminderDays + 1) }
                ) {
                    // Plus icon
                }
            }
        }
        
        // Website URL
        OutlinedTextField(
            value = websiteUrl,
            onValueChange = onWebsiteUrlChange,
            label = { Text(stringResource(R.string.website_url)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // App package name
        OutlinedTextField(
            value = appPackageName,
            onValueChange = onAppPackageNameChange,
            label = { Text(stringResource(R.string.app_package_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        // Notes
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = { Text(stringResource(R.string.notes)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 5
        )
        
        // Save button
        Button(
            onClick = onSaveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.save))
        }
    }
}

private fun validateInputs(name: String, price: String): Boolean {
    return name.isNotBlank() && 
           price.isNotBlank() && 
           price.toDoubleOrNull() != null && 
           price.toDouble() > 0
}
package com.example.subcriptionmanagementapp.ui.screens.subscriptions

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarViewWeek
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubscriptionScreen(
        navController: NavController,
        subscriptionId: Long?,
        subscriptionViewModel: SubscriptionViewModel = hiltViewModel(),
        categoryViewModel: CategoryViewModel = hiltViewModel()
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
    var initialCategoryId by remember(subscriptionId) { mutableStateOf<Long?>(null) }
    var hasAppliedInitialCategory by remember(subscriptionId) { mutableStateOf(false) }

    LaunchedEffect(subscriptionId) {
        if (subscriptionId != null) {
            subscriptionViewModel.loadSubscription(subscriptionId)
        }
    }

    LaunchedEffect(subscription) {
        subscription?.let { currentSubscription ->
            name = currentSubscription.name
            description = currentSubscription.description ?: ""
            price = currentSubscription.price.toString()
            billingCycle = currentSubscription.billingCycle
            nextBillingDate = currentSubscription.nextBillingDate
            isActive = currentSubscription.isActive
            reminderDays = currentSubscription.reminderDays
            websiteUrl = currentSubscription.websiteUrl ?: ""
            appPackageName = currentSubscription.appPackageName ?: ""
            notes = currentSubscription.notes ?: ""
            initialCategoryId = currentSubscription.categoryId
            hasAppliedInitialCategory = false
        } ?: run {
            initialCategoryId = null
            hasAppliedInitialCategory = true
            selectedCategory = null
        }
    }

    LaunchedEffect(categories, initialCategoryId, hasAppliedInitialCategory) {
        if (!hasAppliedInitialCategory) {
            val targetCategoryId = initialCategoryId
            if (targetCategoryId == null) {
                selectedCategory = null
                hasAppliedInitialCategory = true
            } else if (categories.isNotEmpty()) {
                selectedCategory = categories.find { it.id == targetCategoryId }
                hasAppliedInitialCategory = true
            }
        }
    }

    LaunchedEffect(Unit) { categoryViewModel.loadCategories() }

    LaunchedEffect(Unit) {
        subscriptionViewModel.subscriptionSaved.collect {
            navController.popBackStack()
        }
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
                        title =
                                if (subscriptionId != null)
                                        stringResource(R.string.edit_subscription)
                                else stringResource(R.string.add_subscription),
                        navController = navController,
                        currentRoute = Screen.AddEditSubscription.route,
                        showBackButton = true,
                        showActions = false
                )
            }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> LoadingIndicator()
                error != null -> ErrorMessage(message = error!!)
                else ->
                        AddEditSubscriptionContent(
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
                                        val existingSubscription = subscription

                                        val subscriptionToPersist =
                                                Subscription(
                                                        id = subscriptionId ?: 0,
                                                        name = name,
                                                        description = description.ifBlank { null },
                                                        price = price.toDouble(),
                                                        currency = "USD",
                                                        billingCycle = billingCycle,
                                                        startDate =
                                                                existingSubscription?.startDate
                                                                        ?: System.currentTimeMillis(),
                                                        nextBillingDate = nextBillingDate,
                                                        endDate = null,
                                                        reminderDays = reminderDays,
                                                        isActive = isActive,
                                                        categoryId =
                                                                selectedCategory?.id
                                                                        ?: existingSubscription?.categoryId,
                                                        websiteUrl = websiteUrl.ifBlank { null },
                                                        appPackageName =
                                                                appPackageName.ifBlank { null },
                                                        notes = notes.ifBlank { null },
                                                        createdAt =
                                                                existingSubscription?.createdAt
                                                                        ?: System.currentTimeMillis(),
                                                        updatedAt = System.currentTimeMillis()
                                                )

                                        if (subscriptionId != null) {
                                            subscriptionViewModel.updateSubscription(
                                                    subscriptionToPersist
                                            )
                                        } else {
                                            subscriptionViewModel.addSubscription(
                                                    subscriptionToPersist
                                            )
                                        }
                                    } else {
                                        Toast.makeText(
                                                        context,
                                                        R.string.please_fill_required_fields,
                                                        Toast.LENGTH_SHORT
                                                )
                                                .show()
                                    }
                                }
                        )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    calendar.timeInMillis = nextBillingDate

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val formattedDate = remember(nextBillingDate) { dateFormatter.format(Date(nextBillingDate)) }

    val currencyFormatter = remember { NumberFormat.getCurrencyInstance() }
    val priceValue = price.toDoubleOrNull()
    val formattedCycleCost = remember(priceValue) { priceValue?.let(currencyFormatter::format) }

    val monthlyEstimate = remember(priceValue, billingCycle) {
        priceValue?.let {
            when (billingCycle) {
                BillingCycle.DAILY -> it * 30.0
                BillingCycle.WEEKLY -> it * 4.0
                BillingCycle.MONTHLY -> it
                BillingCycle.YEARLY -> it / 12.0
            }
        }
    }
    val yearlyEstimate = remember(priceValue, billingCycle) {
        priceValue?.let {
            when (billingCycle) {
                BillingCycle.DAILY -> it * 365.0
                BillingCycle.WEEKLY -> it * 52.0
                BillingCycle.MONTHLY -> it * 12.0
                BillingCycle.YEARLY -> it
            }
        }
    }

    val monthlyText = monthlyEstimate?.let(currencyFormatter::format)
    val yearlyText = yearlyEstimate?.let(currencyFormatter::format)

    val billingOptions = listOf(
            BillingCycle.MONTHLY to stringResource(R.string.monthly),
            BillingCycle.YEARLY to stringResource(R.string.yearly),
            BillingCycle.WEEKLY to stringResource(R.string.weekly),
            BillingCycle.DAILY to stringResource(R.string.daily)
    )
    val reminderQuickPicks = listOf(0, 1, 3, 7, 14)
    val isSaveEnabled = name.isNotBlank() && priceValue != null && priceValue > 0.0

    val datePickerDialog = remember(context, nextBillingDate) {
        DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    onNextBillingDateChange(calendar.timeInMillis)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            // Limit the form width on large screens so the hero card remains readable
            val maxContentWidth = 640.dp
            val contentWidth = maxWidth.coerceAtMost(maxContentWidth)
            val shouldCenterContent = maxWidth > maxContentWidth

            val listModifier = if (shouldCenterContent) {
                Modifier
                        .width(contentWidth)
                        .align(Alignment.TopCenter)
                        .fillMaxHeight()
            } else {
                Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .fillMaxHeight()
            }

            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 140.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = listModifier
            ) {
            item {
                ElevatedCard(
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                            brush = Brush.linearGradient(
                                                    colors = listOf(
                                                            MaterialTheme.colorScheme.primary,
                                                            MaterialTheme.colorScheme.primaryContainer
                                                    )
                                            )
                                    )
                                    .padding(24.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(
                                    text = if (name.isBlank()) stringResource(R.string.subscription_details) else name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                            )
                            Crossfade(targetState = formattedCycleCost) { value ->
                                if (value != null) {
                                    val billingLabel = billingOptions.first { it.first == billingCycle }.second
                                    Text(
                                            text = "$value • $billingLabel",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text(
                                            text = stringResource(R.string.add_subscription_top_bar_subtitle),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                                    )
                                }
                            }
                            if (monthlyText != null || yearlyText != null) {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    if (monthlyText != null) {
                                        SummaryMetric(
                                                label = stringResource(R.string.estimated_monthly_cost),
                                                value = monthlyText,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                    if (yearlyText != null) {
                                        SummaryMetric(
                                                label = stringResource(R.string.estimated_yearly_cost),
                                                value = yearlyText,
                                                contentColor = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                ElevatedCard(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SectionHeader(
                                icon = Icons.Outlined.Subscriptions,
                                title = stringResource(R.string.subscription_details),
                                subtitle = stringResource(R.string.add_subscription_top_bar_subtitle)
                        )
                        OutlinedTextField(
                                value = name,
                                onValueChange = onNameChange,
                                label = { Text(stringResource(R.string.name)) },
                                placeholder = { Text(stringResource(R.string.subscription_name_placeholder)) },
                                leadingIcon = { Icon(Icons.Outlined.Subscriptions, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = name.isBlank(),
                                supportingText = {
                                    if (name.isBlank()) {
                                        Text(text = stringResource(R.string.please_fill_required_fields))
                                    }
                                }
                        )
                        OutlinedTextField(
                                value = description,
                                onValueChange = onDescriptionChange,
                                label = { Text(stringResource(R.string.description)) },
                                placeholder = { Text(stringResource(R.string.subscription_description_placeholder)) },
                                leadingIcon = { Icon(Icons.Outlined.Description, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2,
                                maxLines = 4
                        )
                        OutlinedTextField(
                                value = price,
                                onValueChange = onPriceChange,
                                label = { Text(stringResource(R.string.price)) },
                                placeholder = { Text(stringResource(R.string.subscription_price_placeholder)) },
                                leadingIcon = { Icon(Icons.Outlined.AttachMoney, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = priceValue == null || priceValue <= 0.0,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                supportingText = {
                                    Text(stringResource(R.string.price_field_hint))
                                }
                        )
                    }
                }
            }

            item {
                ElevatedCard(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SectionHeader(
                                icon = Icons.Outlined.AttachMoney,
                                title = stringResource(R.string.billing_cycle),
                                subtitle = stringResource(R.string.next_billing_date)
                        )
                        BillingCycleSelector(
                                billingOptions = billingOptions,
                                selectedCycle = billingCycle,
                                onOptionSelected = onBillingCycleChange
                        )
                        Text(
                                text = stringResource(R.string.category),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val isNoneSelected = selectedCategory == null
                            FilterChip(
                                    selected = isNoneSelected,
                                    onClick = { onCategoryChange(null) },
                                    label = { Text(stringResource(R.string.no_category)) },
                                    leadingIcon = if (isNoneSelected) {
                                        {
                                            Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                                        }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                            )
                            categories.forEach { category ->
                                val isSelected = category.id == selectedCategory?.id
                                FilterChip(
                                        selected = isSelected,
                                        onClick = { onCategoryChange(category) },
                                        label = { Text(category.name) },
                                        leadingIcon = if (isSelected) {
                                            {
                                                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                                            }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                )
                            }
                        }
                        if (categories.isEmpty()) {
                            Text(
                                    text = stringResource(R.string.no_categories_description),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                                onClick = { datePickerDialog.show() },
                                shape = RoundedCornerShape(16.dp),
                                tonalElevation = 2.dp,
                                modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                    modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                            text = stringResource(R.string.next_billing_date),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                            text = formattedDate,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Icon(
                                        imageVector = Icons.Filled.DateRange,
                                        contentDescription = stringResource(R.string.next_billing_date),
                                        tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Surface(
                                shape = RoundedCornerShape(16.dp),
                                tonalElevation = 2.dp,
                                modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                    modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                            text = stringResource(R.string.status),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                            text = if (isActive) stringResource(R.string.active) else stringResource(R.string.inactive),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Switch(
                                        checked = isActive,
                                        onCheckedChange = onIsActiveChange
                                )
                            }
                        }
                    }
                }
            }

            item {
                ElevatedCard(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SectionHeader(
                                icon = Icons.Outlined.NotificationsActive,
                                title = stringResource(R.string.reminder_days_before),
                                subtitle = stringResource(R.string.subscription_reminder)
                        )
                        FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            reminderQuickPicks.forEach { option ->
                                val selected = reminderDays == option
                                FilterChip(
                                        selected = selected,
                                        onClick = { onReminderDaysChange(option) },
                                        label = {
                                            Text(text = if (option == 0) "0d" else "${option}d")
                                        },
                                        leadingIcon = if (selected) {
                                            {
                                                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                                            }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                )
                            }
                        }
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalIconButton(
                                    onClick = { if (reminderDays > 0) onReminderDaysChange(reminderDays - 1) },
                                    enabled = reminderDays > 0
                            ) {
                                Icon(imageVector = Icons.Filled.Remove, contentDescription = null)
                            }
                            Text(
                                    text = "${reminderDays}d",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                            )
                            FilledTonalIconButton(onClick = { onReminderDaysChange(reminderDays + 1) }) {
                                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                            }
                        }
                    }
                }
            }

            item {
                ElevatedCard(
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SectionHeader(
                                icon = Icons.Outlined.Language,
                                title = stringResource(R.string.access)
                        )
                        OutlinedTextField(
                                value = websiteUrl,
                                onValueChange = onWebsiteUrlChange,
                                label = { Text(stringResource(R.string.website_url)) },
                                placeholder = { Text("https://") },
                                leadingIcon = { Icon(Icons.Outlined.Language, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                        )
                        OutlinedTextField(
                                value = appPackageName,
                                onValueChange = onAppPackageNameChange,
                                label = { Text(stringResource(R.string.app_package_name)) },
                                placeholder = { Text("com.example.app") },
                                leadingIcon = { Icon(Icons.Outlined.Android, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                        )
                        OutlinedTextField(
                                value = notes,
                                onValueChange = onNotesChange,
                                label = { Text(stringResource(R.string.notes)) },
                                placeholder = { Text(stringResource(R.string.notes)) },
                                leadingIcon = { Icon(Icons.Outlined.NoteAlt, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                maxLines = 6
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }
        }

            val bottomBarModifier = if (shouldCenterContent) {
                Modifier
                        .width(contentWidth)
                        .align(Alignment.BottomCenter)
            } else {
                Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
            }

            Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 12.dp,
                    modifier = bottomBarModifier
            ) {
                Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isSaveEnabled) {
                        Text(
                                text = stringResource(R.string.please_fill_required_fields),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                        )
                    }
                    Button(
                            onClick = onSaveClick,
                            enabled = isSaveEnabled,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Composable
private fun BillingCycleSelector(
        billingOptions: List<Pair<BillingCycle, String>>,
        selectedCycle: BillingCycle,
        onOptionSelected: (BillingCycle) -> Unit,
        modifier: Modifier = Modifier
) {
    val itemSpacing = 12.dp
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val optionWidth = remember(maxWidth) {
            val availableWidth = maxWidth - itemSpacing
            maxOf(0.dp, availableWidth / 2)
        }

        Column(verticalArrangement = Arrangement.spacedBy(itemSpacing)) {
            for (rowOptions in billingOptions.chunked(2)) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(itemSpacing)
                ) {
                    for ((cycle, label) in rowOptions) {
                        BillingCycleOption(
                                cycle = cycle,
                                label = label,
                                isSelected = cycle == selectedCycle,
                                onClick = { onOptionSelected(cycle) },
                                modifier = Modifier.width(optionWidth)
                        )
                    }
                    if (rowOptions.size == 1) {
                        Spacer(modifier = Modifier.width(optionWidth))
                    }
                }
            }
        }
    }
}

@Composable
private fun BillingCycleOption(
        cycle: BillingCycle,
        label: String,
        isSelected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    val iconTint = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val subtitleColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            color = containerColor,
            border = BorderStroke(1.dp, borderColor),
            tonalElevation = if (isSelected) 4.dp else 0.dp,
            modifier = modifier.height(96.dp),
            contentColor = contentColor
    ) {
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                    imageVector = billingCycleIcon(cycle),
                    contentDescription = null,
                    tint = iconTint
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )
                Text(
                        text = stringResource(id = billingCycleSubtitleRes(cycle)),
                        style = MaterialTheme.typography.bodySmall,
                        color = subtitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun billingCycleIcon(cycle: BillingCycle): ImageVector {
    return when (cycle) {
        BillingCycle.DAILY -> Icons.Outlined.Schedule
        BillingCycle.WEEKLY -> Icons.Outlined.CalendarViewWeek
        BillingCycle.MONTHLY -> Icons.Outlined.CalendarMonth
        BillingCycle.YEARLY -> Icons.Outlined.Event
    }
}

private fun billingCycleSubtitleRes(cycle: BillingCycle): Int {
    return when (cycle) {
        BillingCycle.DAILY -> R.string.billing_cycle_daily_hint
        BillingCycle.WEEKLY -> R.string.billing_cycle_weekly_hint
        BillingCycle.MONTHLY -> R.string.billing_cycle_monthly_hint
        BillingCycle.YEARLY -> R.string.billing_cycle_yearly_hint
    }
}

@Composable
private fun SummaryMetric(
        label: String,
        value: String,
        contentColor: Color,
        modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.75f)
        )
        Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
        )
    }
}

@Composable
private fun SectionHeader(
        icon: ImageVector,
        title: String,
        subtitle: String? = null,
        modifier: Modifier = Modifier
) {
    Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        ) {
            Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
            )
            subtitle?.let {
                Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun validateInputs(name: String, price: String): Boolean {
    return name.isNotBlank() &&
            price.isNotBlank() &&
            price.toDoubleOrNull() != null &&
            price.toDouble() > 0
}

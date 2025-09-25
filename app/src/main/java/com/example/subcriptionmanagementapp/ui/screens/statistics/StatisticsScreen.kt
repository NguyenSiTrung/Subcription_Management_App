package com.example.subcriptionmanagementapp.ui.screens.statistics

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Cached
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.domain.usecase.statistics.CategorySpending
import com.example.subcriptionmanagementapp.domain.usecase.statistics.MonthlySpending
import com.example.subcriptionmanagementapp.ui.components.CompactScreenTopBar
import com.example.subcriptionmanagementapp.ui.components.CompactTopBarAction
import com.example.subcriptionmanagementapp.ui.components.ErrorMessage
import com.example.subcriptionmanagementapp.ui.components.LoadingIndicator
import com.example.subcriptionmanagementapp.ui.components.charts.LineChart
import com.example.subcriptionmanagementapp.ui.components.charts.LineData
import com.example.subcriptionmanagementapp.ui.components.charts.PieChart
import com.example.subcriptionmanagementapp.ui.components.charts.PieData
import com.example.subcriptionmanagementapp.ui.viewmodel.StatisticsPeriod
import com.example.subcriptionmanagementapp.ui.viewmodel.StatisticsUiState
import com.example.subcriptionmanagementapp.ui.viewmodel.StatisticsViewModel
import com.example.subcriptionmanagementapp.util.CategoryUtils
import com.example.subcriptionmanagementapp.util.formatCurrency
import com.example.subcriptionmanagementapp.util.formatDate
import java.text.DateFormatSymbols

@Composable
fun StatisticsScreen(
    navController: NavController,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val hasRenderableContent = remember(uiState) {
        uiState.monthlyTotal != null ||
            uiState.yearlyTotal != null ||
            uiState.categorySpending.isNotEmpty() ||
            uiState.recentPayments.isNotEmpty() ||
            uiState.monthlyTrend.isNotEmpty()
    }

    LaunchedEffect(uiState.error, hasRenderableContent) {
        val message = uiState.error
        if (message != null && hasRenderableContent) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CompactScreenTopBar(
                title = stringResource(R.string.statistics),
                actions = listOf(
                    CompactTopBarAction(
                        icon = Icons.Outlined.Cached,
                        contentDescription = stringResource(R.string.refresh),
                        onClick = { viewModel.refresh() }
                    )
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading && !hasRenderableContent -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LoadingIndicator()
                }
            }

            uiState.error != null && !hasRenderableContent -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    ErrorMessage(
                        message = uiState.error!!,
                        onRetry = {
                            viewModel.clearError()
                            viewModel.refresh()
                        }
                    )
                }
            }

            else -> {
                StatisticsContent(
                    state = uiState,
                    onPeriodChange = viewModel::onPeriodSelected,
                    onMonthChange = viewModel::onMonthChanged,
                    onYearChange = viewModel::onYearChanged,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatisticsContent(
    state: StatisticsUiState,
    onPeriodChange: (StatisticsPeriod) -> Unit,
    onMonthChange: (Int) -> Unit,
    onYearChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val monthNames = remember { DateFormatSymbols().months.toList() }
    val monthShortNames = remember { DateFormatSymbols().shortMonths.toList() }
    val yearOptions = remember(state.selectedYear) {
        val baseYear = state.selectedYear
        (baseYear - 4..baseYear + 1).toList().sortedDescending()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PeriodSelector(
                    period = state.period,
                    selectedMonthIndex = state.selectedMonth,
                    selectedYear = state.selectedYear,
                    monthNames = monthShortNames,
                    yearOptions = yearOptions,
                    onPeriodChange = onPeriodChange,
                    onMonthChange = onMonthChange,
                    onYearChange = onYearChange
                )

                AnimatedVisibility(visible = state.isLoading || state.isRefreshing) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }

        item {
            when (state.period) {
                StatisticsPeriod.MONTH -> MonthlyHighlightsSection(
                    total = state.monthlyTotal ?: 0.0,
                    paymentCount = state.monthlyPaymentCount,
                    topCategory = state.categorySpending.firstOrNull()
                )

                StatisticsPeriod.YEAR -> YearlyHighlightsSection(
                    total = state.yearlyTotal ?: 0.0,
                    monthlyTrend = state.monthlyTrend
                )
            }
        }

        when (state.period) {
            StatisticsPeriod.MONTH -> {
                item {
                    CategoryDistributionCard(
                        categories = state.categorySpending,
                        monthName = monthNames.getOrNull(state.selectedMonth) ?: "",
                        year = state.selectedYear
                    )
                }

                item {
                    RecentPaymentsCard(
                        payments = state.recentPayments,
                        isEmptyState = state.recentPayments.isEmpty()
                    )
                }
            }

            StatisticsPeriod.YEAR -> {
                item {
                    SpendingTrendCard(
                        trend = state.monthlyTrend,
                        monthShortNames = monthShortNames
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodSelector(
    period: StatisticsPeriod,
    selectedMonthIndex: Int,
    selectedYear: Int,
    monthNames: List<String>,
    yearOptions: List<Int>,
    onPeriodChange: (StatisticsPeriod) -> Unit,
    onMonthChange: (Int) -> Unit,
    onYearChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(R.string.statistics_time_range_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatisticsPeriod.values().forEach { option ->
                val isSelected = option == period
                ElevatedCard(
                    modifier = Modifier
                        .weight(1f),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    ),
                    onClick = { onPeriodChange(option) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val icon = if (option == StatisticsPeriod.MONTH) Icons.Outlined.CalendarMonth else Icons.Outlined.DateRange
                        Icon(imageVector = icon, contentDescription = null)
                        Column {
                            Text(
                                text = stringResource(
                                    if (option == StatisticsPeriod.MONTH) R.string.statistics_period_monthly else R.string.statistics_period_yearly
                                ),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = stringResource(
                                    if (option == StatisticsPeriod.MONTH) R.string.statistics_period_monthly_description else R.string.statistics_period_yearly_description
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (period == StatisticsPeriod.MONTH) {
                DropdownSelector(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.statistics_month_label),
                    value = monthNames.getOrNull(selectedMonthIndex) ?: "",
                    options = monthNames.mapIndexed { index, name -> index to name },
                    onOptionSelected = { onMonthChange(it.first) }
                )
            }

            DropdownSelector(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.statistics_year_label),
                value = selectedYear.toString(),
                options = yearOptions.map { year -> year to year.toString() },
                onOptionSelected = { onYearChange(it.first) }
            )
        }
    }
}

@Composable
private fun <T> DropdownSelector(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    options: List<Pair<T, String>>,
    onOptionSelected: (Pair<T, String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        ElevatedCard(onClick = { expanded = true }) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text(option.second) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun MonthlyHighlightsSection(
    total: Double,
    paymentCount: Int,
    topCategory: CategorySpending?
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(R.string.statistics_monthly_overview_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickStatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.total_spent),
                value = total.formatCurrency(),
                subtitle = stringResource(R.string.statistics_total_spent_caption),
                icon = Icons.Outlined.Leaderboard
            )

            val average = if (paymentCount == 0) 0.0 else total / paymentCount
            QuickStatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.average_payment),
                value = average.formatCurrency(),
                subtitle = stringResource(R.string.statistics_average_payment_caption),
                icon = Icons.Outlined.TrendingUp
            )
        }

        QuickStatCard(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.payment_count),
            value = paymentCount.toString(),
            subtitle = topCategory?.let {
                stringResource(
                    R.string.statistics_top_category_caption,
                    it.category.name,
                    it.amount.formatCurrency()
                )
            } ?: stringResource(R.string.statistics_payments_count_caption),
            icon = Icons.Outlined.BarChart
        )
    }
}

@Composable
private fun YearlyHighlightsSection(total: Double, monthlyTrend: List<MonthlySpending>) {
    val averagePerMonth = if (monthlyTrend.isEmpty()) 0.0 else total / monthlyTrend.size
    val bestMonth = monthlyTrend.maxByOrNull { it.amount }
    val dateSymbols = remember { DateFormatSymbols().months }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(R.string.statistics_year_overview_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickStatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.total_spent),
                value = total.formatCurrency(),
                subtitle = stringResource(R.string.statistics_year_to_date_caption),
                icon = Icons.Outlined.Leaderboard
            )

            QuickStatCard(
                modifier = Modifier.weight(1f),
                title = stringResource(R.string.average_payment),
                value = averagePerMonth.formatCurrency(),
                subtitle = stringResource(R.string.statistics_average_month_caption),
                icon = Icons.Outlined.TrendingUp
            )
        }

        QuickStatCard(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.statistics_best_month_label),
            value = bestMonth?.let { dateSymbols.getOrNull(it.month) ?: "" } ?: stringResource(R.string.statistics_no_data_label),
            subtitle = bestMonth?.amount?.formatCurrency()
                ?: stringResource(R.string.statistics_best_month_caption),
            icon = Icons.Outlined.DateRange
        )
    }
}

@Composable
private fun QuickStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.labelLarge)
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryDistributionCard(
    categories: List<CategorySpending>,
    monthName: String,
    year: Int
) {
    ElevatedCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.statistics_category_distribution_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.statistics_category_distribution_caption, monthName, year),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Outlined.PieChart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (categories.isEmpty()) {
                Text(
                    text = stringResource(R.string.statistics_no_category_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val pieData = remember(categories) {
                    categories.map { categorySpending ->
                        PieData(
                            label = categorySpending.category.name,
                            value = categorySpending.amount.toFloat(),
                            color = CategoryUtils.getCategoryColor(categorySpending.category)
                        )
                    }
                }

                PieChart(
                    data = pieData,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SpendingTrendCard(
    trend: List<MonthlySpending>,
    monthShortNames: List<String>
) {
    ElevatedCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.statistics_spending_trend_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.statistics_spending_trend_caption),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (trend.isEmpty()) {
                Text(
                    text = stringResource(R.string.statistics_no_trend_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val lineData = remember(trend) {
                    trend.map { monthly ->
                        val label = monthShortNames.getOrNull(monthly.month)?.take(3) ?: ""
                        LineData(label = label, value = monthly.amount.toFloat())
                    }
                }

                LineChart(
                    data = lineData,
                    modifier = Modifier.fillMaxWidth(),
                    showValues = false
                )
            }
        }
    }
}

@Composable
private fun RecentPaymentsCard(payments: List<PaymentHistory>, isEmptyState: Boolean) {
    ElevatedCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.statistics_recent_payments_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = stringResource(R.string.statistics_recent_payments_caption),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            if (isEmptyState) {
                Text(
                    text = stringResource(R.string.no_payment_history),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                payments.forEachIndexed { index, paymentHistory ->
                    PaymentHistoryRow(payment = paymentHistory)
                    if (index != payments.lastIndex) {
                        Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentHistoryRow(payment: PaymentHistory) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = payment.paymentDate.formatDate(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
            payment.notes?.let { notes ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = payment.amount.formatCurrency(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

package com.example.subcriptionmanagementapp.ui.screens.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.subcriptionmanagementapp.R
import com.example.subcriptionmanagementapp.data.local.entity.PaymentHistory
import com.example.subcriptionmanagementapp.ui.components.AppTopBar
import com.example.subcriptionmanagementapp.ui.components.CompactTopBar
import com.example.subcriptionmanagementapp.ui.components.ErrorMessage
import com.example.subcriptionmanagementapp.ui.components.LoadingIndicator
import com.example.subcriptionmanagementapp.ui.navigation.Screen
import com.example.subcriptionmanagementapp.ui.theme.PrimaryColor
import com.example.subcriptionmanagementapp.ui.viewmodel.StatisticsViewModel
import com.example.subcriptionmanagementapp.util.formatCurrency
import com.example.subcriptionmanagementapp.util.formatDate
import com.example.subcriptionmanagementapp.util.getFirstDayOfMonth
import com.example.subcriptionmanagementapp.util.getLastDayOfMonth
import java.util.*

@Composable
fun StatisticsScreen(
        navController: NavController,
        viewModel: StatisticsViewModel = hiltViewModel()
) {
    val paymentHistory by viewModel.paymentHistory.collectAsStateWithLifecycle()
    val totalPayment by viewModel.totalPayment.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    val startDate = getFirstDayOfMonth(currentMonth, currentYear)
    val endDate = getLastDayOfMonth(currentMonth, currentYear)

    LaunchedEffect(Unit) { viewModel.loadMonthlyStatistics(currentYear, currentMonth) }

    Scaffold(
            topBar = {
                CompactTopBar(
                        title = stringResource(R.string.statistics),
                        navController = navController
                )
            }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                isLoading -> LoadingIndicator()
                error != null ->
                        ErrorMessage(message = error!!) {
                            viewModel.loadMonthlyStatistics(currentYear, currentMonth)
                        }
                else ->
                        StatisticsContent(
                                paymentHistory = paymentHistory,
                                totalPayment = totalPayment ?: 0.0,
                                currentMonth = currentMonth,
                                currentYear = currentYear
                        )
            }
        }
    }
}

@Composable
fun StatisticsContent(
        paymentHistory: List<PaymentHistory>,
        totalPayment: Double,
        currentMonth: Int,
        currentYear: Int
) {
    LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SummaryCard(
                    totalPayment = totalPayment,
                    paymentCount = paymentHistory.size,
                    currentMonth = currentMonth,
                    currentYear = currentYear
            )
        }

        item {
            Text(
                    text = stringResource(R.string.payment_history),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )
        }

        if (paymentHistory.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                    ) {
                        Text(
                                text = stringResource(R.string.no_payment_history),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(paymentHistory) { payment -> PaymentHistoryItem(payment = payment) }
        }
    }
}

@Composable
fun SummaryCard(totalPayment: Double, paymentCount: Int, currentMonth: Int, currentYear: Int) {
    val monthNames =
            arrayOf(
                    stringResource(R.string.january),
                    stringResource(R.string.february),
                    stringResource(R.string.march),
                    stringResource(R.string.april),
                    stringResource(R.string.may),
                    stringResource(R.string.june),
                    stringResource(R.string.july),
                    stringResource(R.string.august),
                    stringResource(R.string.september),
                    stringResource(R.string.october),
                    stringResource(R.string.november),
                    stringResource(R.string.december)
            )

    Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PrimaryColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                    text = "${monthNames[currentMonth]} $currentYear",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                            text = stringResource(R.string.total_spent),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                    )

                    Text(
                            text = totalPayment.formatCurrency(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                            text = stringResource(R.string.payment_count),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                    )

                    Text(
                            text = paymentCount.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                    )
                }
            }

            if (paymentCount > 0) {
                Spacer(modifier = Modifier.height(16.dp))

                val averagePayment = totalPayment / paymentCount
                Text(
                        text =
                                stringResource(
                                        R.string.average_payment,
                                        averagePayment.formatCurrency()
                                ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun PaymentHistoryItem(payment: PaymentHistory) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = payment.paymentDate.formatDate(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                )

                if (payment.notes != null) {
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                            text = payment.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                    text = payment.amount.formatCurrency(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
            )
        }
    }
}

package com.example.subcriptionmanagementapp.domain.usecase.subscription

import com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.manager.CurrencyRateManager
import com.example.subcriptionmanagementapp.domain.usecase.settings.GetSelectedCurrencyUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetMonthlySpendingUseCase
@Inject
constructor(
        private val getActiveSubscriptionsUseCase: GetActiveSubscriptionsUseCase,
        private val getSelectedCurrencyUseCase: GetSelectedCurrencyUseCase,
        private val currencyRateManager: CurrencyRateManager
) {
    operator fun invoke(): Flow<Double> =
            combine(getActiveSubscriptionsUseCase(), getSelectedCurrencyUseCase()) {
                    subscriptions,
                    selectedCurrency ->
                calculateMonthlySpendingWithConversion(subscriptions, selectedCurrency)
            }

    private suspend fun calculateMonthlySpendingWithConversion(
            subscriptions: List<Subscription>,
            selectedCurrency: String
    ): Double {
        return subscriptions.sumOf { subscription ->
            convertToMonthlyAmount(subscription, selectedCurrency)
        }
    }

    private suspend fun convertToMonthlyAmount(
            subscription: Subscription,
            targetCurrency: String
    ): Double {
        val monthlyEquivalent =
                when (subscription.billingCycle) {
                    BillingCycle.DAILY -> subscription.price * 30.44 // Average days in a month
                    BillingCycle.WEEKLY -> subscription.price * 4.33 // Average weeks in a month
                    BillingCycle.MONTHLY -> subscription.price
                    BillingCycle.YEARLY -> subscription.price / 12.0
                }

        return if (subscription.currency == targetCurrency) {
            monthlyEquivalent
        } else {
            try {
                currencyRateManager.convertCurrency(
                        monthlyEquivalent,
                        subscription.currency,
                        targetCurrency
                )
                        ?: monthlyEquivalent
            } catch (e: Exception) {
                // If conversion fails, return original amount
                monthlyEquivalent
            }
        }
    }
}

package com.example.subcriptionmanagementapp.domain.usecase.subscription

import com.example.subcriptionmanagementapp.data.local.entity.BillingCycle
import com.example.subcriptionmanagementapp.data.local.entity.Subscription
import com.example.subcriptionmanagementapp.data.manager.CurrencyRateManager
import com.example.subcriptionmanagementapp.domain.usecase.settings.GetSelectedCurrencyUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ConvertSubscriptionPriceUseCase
@Inject
constructor(
        private val getSelectedCurrencyUseCase: GetSelectedCurrencyUseCase,
        private val currencyRateManager: CurrencyRateManager
) {

    operator fun invoke(subscription: Subscription): Flow<Double> =
            getSelectedCurrencyUseCase().map { selectedCurrency ->
                convertPrice(subscription, selectedCurrency)
            }

    private suspend fun convertPrice(subscription: Subscription, targetCurrency: String): Double {
        return if (subscription.currency == targetCurrency) {
            subscription.price
        } else {
            currencyRateManager.convertCurrency(
                    subscription.price,
                    subscription.currency,
                    targetCurrency
            )
                    ?: subscription.price
        }
    }

    fun convertToMonthlyEquivalent(subscription: Subscription, targetCurrency: String): Double {
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
            // This would need to be called from a suspend context
            monthlyEquivalent // For now, return unconverted for non-suspend usage
        }
    }
}

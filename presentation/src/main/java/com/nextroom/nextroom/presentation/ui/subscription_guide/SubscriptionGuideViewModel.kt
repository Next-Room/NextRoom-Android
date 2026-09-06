package com.nextroom.nextroom.presentation.ui.subscription_guide

import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import com.nextroom.nextroom.presentation.ui.Constants
import com.nextroom.nextroom.presentation.ui.billing.BillingPeriod
import com.nextroom.nextroom.presentation.ui.billing.SubscriptionOffer
import com.nextroom.nextroom.presentation.ui.billing.SubscriptionOfferLoader
import com.nextroom.nextroom.presentation.ui.billing.selectLeastPricedOffer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class SubscriptionGuideViewModel @Inject constructor(
    private val subscriptionOfferLoader: SubscriptionOfferLoader,
) : NewBaseViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionGuideUiState())
    val uiState = _uiState.asStateFlow()

    val subscriptionProductId: String = Constants.MEMBERSHIP_PRODUCT

    init {
        loadPlan()
    }

    /**
     * 화면에 표시할 기간·가격을 [SubscriptionOfferLoader]가 고른 offer에서 읽어온다.
     *
     * 결제에 쓰이는 offer와 어긋나지 않도록 [selectLeastPricedOffer]로 동일하게 고른다.
     */
    private fun loadPlan() {
        baseViewModelScope.launch {
            val offer = withTimeoutOrNull(PRODUCT_DETAILS_TIMEOUT_MS) {
                subscriptionOfferLoader.load(subscriptionProductId)
            }

            if (offer == null) {
                handleError(
                    IllegalStateException("Could not load subscription offer. productId: $subscriptionProductId"),
                    ErrorAction.POP_BACK_STACK,
                )
                return@launch
            }

            _uiState.update { it.copy(plan = offer.toPlan()) }
        }
    }

    private fun SubscriptionOffer.toPlan() = SubscriptionGuideUiState.Plan(
        freeTrialPeriod = freeTrialPeriod,
        trialEndDate = freeTrialPeriod?.let { calculateTrialEndDate(it) }.orEmpty(),
        displayPrice = freeTrialPrice ?: recurringPrice,
        recurringPrice = recurringPrice,
        recurringPeriod = recurringPeriod,
    )

    fun onPurchaseStarted() {
        _uiState.update { it.copy(loading = true) }
    }

    fun onPurchaseFinished() {
        _uiState.update { it.copy(loading = false) }
    }

    /** 무료 체험 종료일: 오늘(KST)로부터 [period]만큼 뒤 */
    private fun calculateTrialEndDate(period: BillingPeriod): String {
        val timeZone = TimeZone.getTimeZone(TIME_ZONE_KST)
        val endDate = Calendar.getInstance(timeZone, Locale.KOREA)
            .apply { period.addTo(this) }
            .time

        return SimpleDateFormat(DATE_PATTERN, Locale.KOREA)
            .apply { this.timeZone = timeZone }
            .format(endDate)
    }

    companion object {
        private const val DATE_PATTERN = "yyyy. MM. dd."
        private const val TIME_ZONE_KST = "Asia/Seoul"
        private const val PRODUCT_DETAILS_TIMEOUT_MS = 5_000L
    }
}

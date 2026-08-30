package com.nextroom.nextroom.presentation.ui.subscription_guide

import com.nextroom.nextroom.presentation.ui.billing.BillingPeriod

/**
 * @property plan Play에서 읽어온 요금제 정보. 아직 조회되지 않았으면 null
 * @property loading 결제 플로우 진행 중 여부
 */
data class SubscriptionGuideUiState(
    val plan: Plan? = null,
    val loading: Boolean = false,
) {

    /**
     * @property freeTrialPeriod 무료 체험 기간. null이면 체험 자격이 없어 곧바로 정가가 결제된다.
     * @property trialEndDate 무료 체험 종료일 (yyyy. MM. dd.). 체험 자격이 없으면 빈 문자열
     * @property recurringPrice 체험 종료 후 결제되는 정가 (예: "₩29,000")
     * @property recurringPeriod 정가 결제 주기
     * @property displayPrice 가격 카드에 크게 보여줄 금액. 체험 중이면 체험 구간 가격("₩0")
     */
    data class Plan(
        val freeTrialPeriod: BillingPeriod?,
        val trialEndDate: String,
        val displayPrice: String,
        val recurringPrice: String,
        val recurringPeriod: BillingPeriod?,
    ) {
        val isFreeTrial: Boolean get() = freeTrialPeriod != null
    }
}

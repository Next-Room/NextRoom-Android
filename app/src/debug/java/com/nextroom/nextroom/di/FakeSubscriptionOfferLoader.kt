package com.nextroom.nextroom.di

import com.nextroom.nextroom.presentation.ui.billing.BillingPeriod
import com.nextroom.nextroom.presentation.ui.billing.SubscriptionOffer
import com.nextroom.nextroom.presentation.ui.billing.SubscriptionOfferLoader

/**
 * 디버그 빌드에서 Play 대신 사용하는 가짜 offer.
 *
 * 디버그 빌드는 applicationId에 `.debug`가 붙어 Play가 상품을 내려주지 않는다.
 * 무료 체험 표시, 체험 종료일 계산, 정가 문구 같은 화면 로직을 로컬에서 확인하기 위한 용도다.
 *
 * 확인하고 싶은 상황에 맞춰 [scenario]만 바꿔서 쓴다.
 */
class FakeSubscriptionOfferLoader(
    private val scenario: Scenario = Scenario.FREE_TRIAL,
) : SubscriptionOfferLoader {

    enum class Scenario {
        /** 무료 체험 자격이 있는 사용자 */
        FREE_TRIAL,

        /** 체험을 이미 소진해 정가로 바로 결제되는 사용자 */
        NO_FREE_TRIAL,

        /** 구매 가능한 offer가 없는 경우 (에러 다이얼로그 확인용) */
        UNAVAILABLE,
    }

    override suspend fun load(productId: String): SubscriptionOffer? = when (scenario) {
        Scenario.FREE_TRIAL -> SubscriptionOffer(
            offerToken = FAKE_OFFER_TOKEN,
            basePlanId = FAKE_BASE_PLAN_ID,
            offerId = FAKE_OFFER_ID,
            freeTrialPeriod = BillingPeriod.parse(FREE_TRIAL_PERIOD),
            freeTrialPrice = "₩0",
            recurringPrice = RECURRING_PRICE,
            recurringPeriod = BillingPeriod.parse(RECURRING_PERIOD),
        )

        Scenario.NO_FREE_TRIAL -> SubscriptionOffer(
            offerToken = FAKE_OFFER_TOKEN,
            basePlanId = FAKE_BASE_PLAN_ID,
            offerId = null,
            freeTrialPeriod = null,
            freeTrialPrice = null,
            recurringPrice = RECURRING_PRICE,
            recurringPeriod = BillingPeriod.parse(RECURRING_PERIOD),
        )

        Scenario.UNAVAILABLE -> null
    }

    companion object {
        /** 무료 체험 1개월. 체험 종료일은 오늘로부터 1개월 뒤로 계산된다. */
        private const val FREE_TRIAL_PERIOD = "P1M"
        private const val RECURRING_PERIOD = "P1M"
        private const val RECURRING_PRICE = "₩29,900"

        private const val FAKE_OFFER_TOKEN = "fake-offer-token"
        private const val FAKE_BASE_PLAN_ID = "fake-monthly"
        private const val FAKE_OFFER_ID = "fake-free-trial"
    }
}

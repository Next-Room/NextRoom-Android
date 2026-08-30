package com.nextroom.nextroom.presentation.ui.billing

import com.android.billingclient.api.ProductDetails
import timber.log.Timber

/**
 * 결제에 사용할 offer와 그 offer를 화면에 표시하기 위한 가격 정보.
 *
 * @property offerToken 결제 시 Play에 넘길 토큰
 * @property freeTrialPeriod 무료 체험 기간. null이면 체험 자격이 없어 곧바로 정가가 결제된다.
 * @property freeTrialPrice 체험 구간의 표시 가격 (예: "₩0")
 * @property recurringPrice 체험 종료 후 결제되는 정가 (예: "₩29,000")
 * @property recurringPeriod 정가 결제 주기
 */
data class SubscriptionOffer(
    val offerToken: String,
    val basePlanId: String,
    val offerId: String?,
    val freeTrialPeriod: BillingPeriod?,
    val freeTrialPrice: String?,
    val recurringPrice: String,
    val recurringPeriod: BillingPeriod?,
) {

    /**
     * 무료 체험 자격 보유 여부.
     *
     * Play는 콘솔에서 "신규 고객" 등으로 자격을 제한한 offer를 자격이 있는 사용자에게만 내려주므로,
     * 체험 구간이 있는 offer가 선택됐다는 것은 곧 이 사용자가 체험 자격을 가졌다는 뜻이다.
     */
    val hasFreeTrial: Boolean get() = freeTrialPeriod != null
}

/**
 * 사용자가 자격을 가진 offer 중 가장 저렴한 offer를 고른다.
 *
 * [ProductDetails.getSubscriptionOfferDetails]에는 base plan 자체(offerId == null)와
 * 사용자가 자격을 가진 offer들이 함께 담겨 오고, 그 순서는 보장되지 않는다.
 * 따라서 무료 체험처럼 0원 구간이 있는 offer가 있어도 목록의 첫 번째를 그대로 쓰면
 * base plan이 선택되어 정가로 결제될 수 있다.
 *
 * 결제와 화면 표시가 서로 다른 offer를 가리키지 않도록 두 곳 모두 이 함수를 통해 offer를 고른다.
 *
 * @param basePlanId 서버가 내려준 base plan id. null이거나 일치하는 offer가 없으면 전체에서 고른다.
 *
 * @return 선택된 offer. 구매 가능한 offer가 없으면 null.
 */
fun ProductDetails.selectLeastPricedOffer(basePlanId: String? = null): SubscriptionOffer? {
    val offers = subscriptionOfferDetails.orEmpty()
    if (offers.isEmpty()) return null

    val matched = offers.filter { basePlanId == null || it.basePlanId == basePlanId }
    val candidates = if (matched.isNotEmpty()) {
        matched
    } else {
        Timber.w("selectLeastPricedOffer: basePlanId($basePlanId)와 일치하는 offer가 없어 전체에서 선택합니다.")
        offers
    }

    val selected = candidates.minByOrNull { offer ->
        // 무료 체험 offer는 0원 구간을 포함하므로 최소 가격 기준으로 고르면 자연스럽게 선택된다.
        offer.pricingPhases.pricingPhaseList.minOfOrNull { it.priceAmountMicros } ?: Long.MAX_VALUE
    } ?: return null

    val phases = selected.pricingPhases.pricingPhaseList
    // 마지막 구간이 체험/할인이 끝난 뒤 계속 결제되는 정가 구간이다.
    val recurring = phases.lastOrNull() ?: return null
    // 정가 구간이 곧 0원인 경우(무료 base plan)는 체험이 아니다.
    val freeTrial = phases.firstOrNull { it.priceAmountMicros == 0L }?.takeIf { it !== recurring }

    return SubscriptionOffer(
        offerToken = selected.offerToken,
        basePlanId = selected.basePlanId,
        offerId = selected.offerId,
        freeTrialPeriod = freeTrial?.let { BillingPeriod.parse(it.billingPeriod) },
        freeTrialPrice = freeTrial?.formattedPrice,
        recurringPrice = recurring.formattedPrice,
        recurringPeriod = BillingPeriod.parse(recurring.billingPeriod),
    )
}

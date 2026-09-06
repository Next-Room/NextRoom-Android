package com.nextroom.nextroom.presentation.ui.billing

import com.nextroom.nextroom.presentation.ui.Constants
import com.nextroom.nextroom.presentation.util.BillingClientLifecycle
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

/**
 * 화면에 표시할 구독 offer를 가져온다.
 *
 * Play는 앱의 패키지명과 서명이 콘솔에 등록된 것과 일치해야만 상품 정보를 내려준다.
 * 디버그 빌드는 applicationId에 `.debug`가 붙어 조회 자체가 불가능하므로,
 * 빌드 타입별로 구현을 갈아끼워 로컬에서도 화면을 확인할 수 있게 한다.
 */
interface SubscriptionOfferLoader {

    /**
     * @return 사용자가 구매 가능한 offer. 없으면 null.
     */
    suspend fun load(productId: String): SubscriptionOffer?
}

class PlaySubscriptionOfferLoader(
    private val billingClientLifecycle: BillingClientLifecycle,
) : SubscriptionOfferLoader {

    override suspend fun load(productId: String): SubscriptionOffer? {
        val productDetailsFlow = when (productId) {
            Constants.MEMBERSHIP_PRODUCT -> billingClientLifecycle.membershipProductDetails
            else -> return null
        }

        // 연결 전이면 빈 결과가 오지만, 연결이 끝나면 상품 정보가 흘러들어온다.
        billingClientLifecycle.refreshSubscriptionProductDetails()

        return productDetailsFlow.filterNotNull().first().selectLeastPricedOffer()
    }
}

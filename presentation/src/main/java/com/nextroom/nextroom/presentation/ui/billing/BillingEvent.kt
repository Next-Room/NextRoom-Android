package com.nextroom.nextroom.presentation.ui.billing

sealed interface BillingEvent {
    data object PurchaseAcknowledged : BillingEvent

    /**
     * 사용자가 Play 결제 시트를 그냥 닫은 경우.
     *
     * 오류가 아니므로 안내 없이 로딩만 걷어낸다.
     */
    data object PurchaseCanceled : BillingEvent

    data class PurchaseFailed(val errorMessage: String = "", val purchaseState: Int? = null) : BillingEvent
}

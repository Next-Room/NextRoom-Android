package com.nextroom.nextroom.presentation.ui.purchase

import com.nextroom.nextroom.domain.model.SubscribeStatus

data class PurchaseState(
    val subscribeStatus: SubscribeStatus = SubscribeStatus.None,
)

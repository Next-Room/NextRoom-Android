package com.nextroom.nextroom.presentation.ui

import com.nextroom.nextroom.domain.model.SubscriptionPlan

data class SubscriptionPromotionState(
    val loading: Boolean = false,
    val plan: SubscriptionPlan = SubscriptionPlan("", emptyList())
)
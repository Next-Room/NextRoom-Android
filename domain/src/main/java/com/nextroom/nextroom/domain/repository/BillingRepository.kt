package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.Plan

interface BillingRepository {
    suspend fun getPlans(): Result<List<Plan>>
    suspend fun postPurchaseToken(purchaseToken: String): Result<Unit>
}

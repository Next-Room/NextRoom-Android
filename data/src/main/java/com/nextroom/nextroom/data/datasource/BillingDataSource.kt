package com.nextroom.nextroom.data.datasource

import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.network.request.PurchaseToken
import com.nextroom.nextroom.domain.model.Result
import javax.inject.Inject

class BillingDataSource @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun postPurchaseToken(purchaseToken: PurchaseToken): Result<Unit> {
        return apiService.postPurchaseToken(purchaseToken)
    }
}

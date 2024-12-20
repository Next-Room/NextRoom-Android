package com.nextroom.nextroom.data.repository

import com.nextroom.nextroom.data.datasource.BillingDataSource
import com.nextroom.nextroom.data.datasource.SubscriptionDataSource
import com.nextroom.nextroom.data.network.request.PurchaseToken
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.Ticket
import com.nextroom.nextroom.domain.repository.BillingRepository
import javax.inject.Inject

class BillingRepositoryImpl @Inject constructor(
    private val billingDataSource: BillingDataSource,
    private val subscriptionDataSource: SubscriptionDataSource,
) : BillingRepository {

    override suspend fun getTicketInfo(): Result<Ticket> {
        return subscriptionDataSource.getTicketInfo()
    }

    override suspend fun postPurchaseToken(purchaseToken: String): Result<Unit> {
        return billingDataSource.postPurchaseToken(purchaseToken = PurchaseToken(purchaseToken))
    }
}

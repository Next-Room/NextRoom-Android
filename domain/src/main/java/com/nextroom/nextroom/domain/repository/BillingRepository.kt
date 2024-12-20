package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.Ticket

interface BillingRepository {
    suspend fun getTicketInfo(): Result<Ticket>
    suspend fun postPurchaseToken(purchaseToken: String): Result<Unit>
}

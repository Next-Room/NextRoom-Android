package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.Ticket

interface BillingRepository {

    suspend fun getTickets(): Result<List<Ticket>>
}

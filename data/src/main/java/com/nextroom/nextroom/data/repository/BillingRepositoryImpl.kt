package com.nextroom.nextroom.data.repository

import com.nextroom.nextroom.data.datasource.BillingDataSource
import com.nextroom.nextroom.domain.repository.BillingRepository
import javax.inject.Inject

class BillingRepositoryImpl @Inject constructor(
    private val billingDataSource: BillingDataSource
) : BillingRepository {

}
package com.nextroom.nextroom.data.datasource

import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.UserSubscribeStatus
import com.nextroom.nextroom.domain.model.mapOnSuccess
import javax.inject.Inject

class SubscriptionDataSource @Inject constructor(
    private val apiService: ApiService,
) {
    suspend fun getUserSubscriptionStatus(): Result<UserSubscribeStatus> {
        return apiService.getUserSubscriptionStatus().mapOnSuccess {
            it.data.toDomain()
        }
    }
}
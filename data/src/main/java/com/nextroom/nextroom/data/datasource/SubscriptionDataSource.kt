package com.nextroom.nextroom.data.datasource

import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.domain.model.Mypage
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.Ticket
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

    suspend fun getUserSubscription(): Result<Mypage> {
        return apiService.getMypageInfo().mapOnSuccess { it.data.toDomain() }
    }

    suspend fun getTicketInfo(): Result<Ticket> {
        return apiService.getSubscriptionTicketInfo().mapOnSuccess { it.data.toDomainModel() }
    }
}

package com.nextroom.nextroom.data.repository

import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.network.response.toDomain
import com.nextroom.nextroom.domain.model.Banner
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.mapOnSuccess
import com.nextroom.nextroom.domain.repository.BannerRepository
import javax.inject.Inject

class BannerRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : BannerRepository {

    override suspend fun getBanners(): Result<List<Banner>> {
        return apiService.getBanners().mapOnSuccess { it.data.toDomain() }
    }
}
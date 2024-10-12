package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.Banner
import com.nextroom.nextroom.domain.model.Result

interface BannerRepository {

    suspend fun getBanners(): Result<List<Banner>>
}
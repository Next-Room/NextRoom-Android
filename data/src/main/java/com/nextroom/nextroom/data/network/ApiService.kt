package com.nextroom.nextroom.data.network

import com.nextroom.nextroom.data.model.TokenDto
import com.nextroom.nextroom.data.network.request.LoginRequest
import com.nextroom.nextroom.data.network.response.BaseListResponse
import com.nextroom.nextroom.data.network.response.BaseResponse
import com.nextroom.nextroom.data.network.response.HintDto
import com.nextroom.nextroom.data.network.response.LoginDto
import com.nextroom.nextroom.data.network.response.MypageDto
import com.nextroom.nextroom.data.network.response.ThemeDto
import com.nextroom.nextroom.data.network.response.TicketDto
import com.nextroom.nextroom.data.network.response.UserSubscriptionStatusDto
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.request.TokenRefreshRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("api/v1/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Result<BaseResponse<LoginDto>>

    @GET("api/v1/auth/reissue")
    suspend fun refreshToken(refreshRequest: TokenRefreshRequest): Result<BaseResponse<TokenDto>>

    @GET("api/v1/theme")
    suspend fun getThemes(): Result<BaseListResponse<ThemeDto>>

    @GET("api/v1/hint")
    suspend fun getHint(@Query("themeId") themeId: Int): Result<BaseListResponse<HintDto>>

    @GET("api/v1/subscription/status")
    suspend fun getUserSubscriptionStatus(): Result<BaseResponse<UserSubscriptionStatusDto>>

    @GET("api/v1/subscription/mypage")
    suspend fun getMypageInfo(): Result<BaseResponse<MypageDto>>

    @GET("api/v1/subscription/plan")
    suspend fun getSubscriptionPlans(): Result<BaseListResponse<TicketDto>>
}

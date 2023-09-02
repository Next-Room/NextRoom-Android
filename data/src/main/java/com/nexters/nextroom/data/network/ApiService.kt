package com.nexters.nextroom.data.network

import com.nexters.nextroom.data.network.request.LoginRequest
import com.nexters.nextroom.data.network.response.HintDto
import com.nexters.nextroom.data.network.response.ListDto
import com.nexters.nextroom.data.network.response.LoginResponse
import com.nexters.nextroom.data.network.response.ThemeDto
import com.nexters.nextroom.domain.model.Result
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("api/v1/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Result<LoginResponse>

    suspend fun refreshToken(refreshToken: String): Result<String> // TODO Refresh Tokens

    @GET("api/v1/theme")
    suspend fun getThemes(@Query("adminCode") adminCode: String): Result<ListDto<ThemeDto>>

    @GET("api/v1/hint")
    suspend fun getHint(@Query("themeId") themeId: Int): Result<ListDto<HintDto>>
}

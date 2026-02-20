package com.nextroom.nextroom.data.datasource

import com.nextroom.nextroom.data.model.ThemeBackgroundActivationId
import com.nextroom.nextroom.data.network.ApiService
import com.nextroom.nextroom.data.network.request.AddThemeRequestDto
import com.nextroom.nextroom.data.network.request.EditThemeRequestDto
import com.nextroom.nextroom.data.network.request.RemoveThemeRequestDto
import com.nextroom.nextroom.data.network.response.toDomain
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.ThemeInfo
import com.nextroom.nextroom.domain.model.mapOnSuccess
import com.nextroom.nextroom.domain.request.AddThemeRequest
import com.nextroom.nextroom.domain.request.EditThemeRequest
import javax.inject.Inject

class ThemeRemoteDataSource @Inject constructor(
    private val apiService: ApiService,
) {

    suspend fun getThemes(): Result<List<ThemeInfo>> {
        return apiService.getThemes()
            .mapOnSuccess { it.data.toDomain() }
    }

    suspend fun putActiveThemeBackgroundImage(themeBackgroundActivationId: ThemeBackgroundActivationId): Result<Unit> {
        return apiService.putActiveThemeBackgroundImage(themeBackgroundActivationId)
    }

    suspend fun addTheme(request: AddThemeRequest): Result<Unit> {
        return apiService.addTheme(
            AddThemeRequestDto(
                title = request.title,
                timeLimit = request.timeLimit,
                hintLimit = request.hintLimit,
            )
        )
    }

    suspend fun editTheme(request: EditThemeRequest): Result<Unit> {
        return apiService.editTheme(
            EditThemeRequestDto(
                id = request.id,
                title = request.title,
                timeLimit = request.timeLimit,
                hintLimit = request.hintLimit,
            )
        )
    }

    suspend fun deleteTheme(themeId: Int): Result<Unit> {
        return apiService.deleteTheme(RemoveThemeRequestDto(id = themeId))
    }
}

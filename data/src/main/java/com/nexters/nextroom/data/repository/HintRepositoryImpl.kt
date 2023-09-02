package com.nexters.nextroom.data.repository

import com.nexters.nextroom.data.datasource.HintLocalDataSource
import com.nexters.nextroom.data.datasource.HintRemoteDataSource
import com.nexters.nextroom.data.datasource.SettingDataSource
import com.nexters.nextroom.data.datasource.ThemeLocalDataSource
import com.nexters.nextroom.data.model.toDomain
import com.nexters.nextroom.domain.model.Hint
import com.nexters.nextroom.domain.model.Result
import com.nexters.nextroom.domain.model.mapOnSuccess
import com.nexters.nextroom.domain.model.suspendOnSuccess
import com.nexters.nextroom.domain.repository.HintRepository
import javax.inject.Inject

class HintRepositoryImpl @Inject constructor(
    private val hintLocalDataSource: HintLocalDataSource,
    private val hintRemoteDataSource: HintRemoteDataSource,
    private val themeLocalDataSource: ThemeLocalDataSource,
    private val settingDataSource: SettingDataSource,
) : HintRepository {

    override suspend fun getHint(hintCode: String): Hint? {
        val themeId = settingDataSource.getLatestGameCode()
        return hintLocalDataSource.getHint(themeId, hintCode)?.toDomain()
    }

    override suspend fun saveHints(themeId: Int): Result<Long> {
        val updatedTime = System.currentTimeMillis()
        return hintRemoteDataSource.getHints(themeId)
            .suspendOnSuccess {
                themeLocalDataSource.updateUpdatedInfo(themeId, updatedTime)
                hintLocalDataSource.saveHints(themeId, it)
            }.mapOnSuccess { updatedTime }
    }
}

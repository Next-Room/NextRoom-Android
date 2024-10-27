package com.nextroom.nextroom.data.repository

import com.nextroom.nextroom.data.datasource.HintLocalDataSource
import com.nextroom.nextroom.data.datasource.HintRemoteDataSource
import com.nextroom.nextroom.data.datasource.SettingDataSource
import com.nextroom.nextroom.data.datasource.ThemeLocalDataSource
import com.nextroom.nextroom.data.model.toDomain
import com.nextroom.nextroom.domain.model.Hint
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.mapOnSuccess
import com.nextroom.nextroom.domain.model.suspendOnSuccess
import com.nextroom.nextroom.domain.repository.HintRepository
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

    //TODO : 리팩토링 필요. hint를 저장하는데, 최신의 updatedTime이 나올거라고 예상할수가 없고 그래서도 안됨
    override suspend fun saveHints(themeId: Int): Result<Pair<Int, Long>> {
        val updatedTime = System.currentTimeMillis()
        return hintRemoteDataSource.getHints(themeId)
            .suspendOnSuccess {
                themeLocalDataSource.updateUpdatedInfo(themeId, updatedTime)
                hintLocalDataSource.saveHints(themeId, it)
            }.mapOnSuccess { Pair(themeId, updatedTime) }
    }
}

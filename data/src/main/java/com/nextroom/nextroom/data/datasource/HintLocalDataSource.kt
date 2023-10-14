package com.nextroom.nextroom.data.datasource

import com.nextroom.nextroom.data.db.HintDao
import com.nextroom.nextroom.data.model.HintEntity
import com.nextroom.nextroom.data.model.toHintEntity
import com.nextroom.nextroom.domain.model.Hint
import javax.inject.Inject

class HintLocalDataSource @Inject constructor(
    private val hintDao: HintDao,
) {

    suspend fun getHint(themeId: Int, hintCode: String): HintEntity? {
        return hintDao.getHint(themeId, hintCode)
    }

    suspend fun saveHints(themeId: Int, hints: List<Hint>) {
        hintDao.replaceHints(themeId, hints.map { it.toHintEntity(themeId) })
    }
}

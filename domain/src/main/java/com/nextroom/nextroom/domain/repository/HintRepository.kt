package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.Hint
import com.nextroom.nextroom.domain.model.Result

interface HintRepository {
    suspend fun getHint(hintCode: String): Hint?
    suspend fun saveHints(themeId: Int): Result<Long>
}

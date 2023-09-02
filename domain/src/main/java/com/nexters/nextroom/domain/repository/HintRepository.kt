package com.nexters.nextroom.domain.repository

import com.nexters.nextroom.domain.model.Hint
import com.nexters.nextroom.domain.model.Result

interface HintRepository {
    suspend fun getHint(hintCode: String): Hint?
    suspend fun saveHints(themeId: Int): Result<Long>
}

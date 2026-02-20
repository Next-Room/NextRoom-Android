package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.Hint
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.request.AddHintRequest
import com.nextroom.nextroom.domain.request.EditHintRequest

interface HintRepository {
    suspend fun getHint(hintCode: String): Hint?
    suspend fun saveHints(themeId: Int): Result<Long>
    suspend fun getHintsForTheme(themeId: Int): Result<List<Hint>>
    suspend fun addHint(request: AddHintRequest): Result<Unit>
    suspend fun editHint(request: EditHintRequest): Result<Unit>
    suspend fun deleteHint(hintId: Int): Result<Unit>
}

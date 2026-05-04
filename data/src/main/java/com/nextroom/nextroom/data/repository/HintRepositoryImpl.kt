package com.nextroom.nextroom.data.repository

import com.nextroom.nextroom.data.datasource.HintLocalDataSource
import com.nextroom.nextroom.data.datasource.HintRemoteDataSource
import com.nextroom.nextroom.data.datasource.ImageUploadDataSource
import com.nextroom.nextroom.data.datasource.SettingDataSource
import com.nextroom.nextroom.data.datasource.ThemeLocalDataSource
import com.nextroom.nextroom.data.model.toDomain
import com.nextroom.nextroom.domain.model.Hint
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.mapOnSuccess
import com.nextroom.nextroom.domain.model.suspendOnSuccess
import com.nextroom.nextroom.domain.repository.HintRepository
import com.nextroom.nextroom.domain.repository.UploadImagesResult
import com.nextroom.nextroom.domain.request.AddHintRequest
import com.nextroom.nextroom.domain.request.EditHintRequest
import java.io.File
import javax.inject.Inject

class HintRepositoryImpl @Inject constructor(
    private val hintLocalDataSource: HintLocalDataSource,
    private val hintRemoteDataSource: HintRemoteDataSource,
    private val themeLocalDataSource: ThemeLocalDataSource,
    private val settingDataSource: SettingDataSource,
    private val imageUploadDataSource: ImageUploadDataSource,
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

    override suspend fun getHintsForTheme(themeId: Int): Result<List<Hint>> {
        return hintRemoteDataSource.getHints(themeId)
    }

    override suspend fun addHint(request: AddHintRequest): Result<Unit> {
        return hintRemoteDataSource.addHint(request)
    }

    override suspend fun editHint(request: EditHintRequest): Result<Unit> {
        return hintRemoteDataSource.editHint(request)
    }

    override suspend fun deleteHint(hintId: Int): Result<Unit> {
        return hintRemoteDataSource.deleteHint(hintId)
    }

    override suspend fun uploadImages(
        themeId: Int,
        hintImageFiles: List<File>,
        answerImageFiles: List<File>
    ): Result<UploadImagesResult> {
        return imageUploadDataSource.uploadImages(
            themeId = themeId,
            hintImageFiles = hintImageFiles,
            answerImageFiles = answerImageFiles
        ).mapOnSuccess {
            UploadImagesResult(
                hintImageFileNames = it.hintImageFileNames,
                answerImageFileNames = it.answerImageFileNames
            )
        }
    }
}

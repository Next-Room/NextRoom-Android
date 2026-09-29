package com.nextroom.nextroom.data.repository

import com.nextroom.nextroom.data.datasource.SubscriptionNoticeDataSource
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.repository.SubscriptionNoticeRepository
import timber.log.Timber
import javax.inject.Inject

class SubscriptionNoticeRepositoryImpl @Inject constructor(
    private val dataSource: SubscriptionNoticeDataSource,
) : SubscriptionNoticeRepository {

    override suspend fun hasConfirmedSubscriptionRequiredNotice(shopId: String): Result<Boolean> {
        return runCatchingToResult {
            dataSource.hasSubscriptionRequiredNoticeConfirmation(shopId)
        }
    }

    override suspend fun recordSubscriptionRequiredNoticeConfirmed(
        shopId: String,
        shopName: String,
        confirmedAt: Long,
    ): Result<Unit> {
        return runCatchingToResult {
            dataSource.putSubscriptionRequiredNoticeConfirmation(shopId, shopName, confirmedAt)
        }
    }

    private inline fun <T> runCatchingToResult(block: () -> T): Result<T> {
        return try {
            Result.Success(block())
        } catch (e: Exception) {
            Timber.e(e)
            Result.Failure.OperationError(e)
        }
    }
}

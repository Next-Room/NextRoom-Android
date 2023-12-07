package com.nextroom.nextroom.data.repository

import com.nextroom.nextroom.data.datasource.FirebaseRemoteConfigDataSource
import com.nextroom.nextroom.domain.repository.FirebaseRemoteConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseRemoteConfigRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseRemoteConfigDataSource,
) : FirebaseRemoteConfigRepository {
    override suspend fun getFirebaseRemoteConfigValue(key: String): Flow<String> =
        flow {
            dataSource
                .getFirebaseRemoteConfigValue(key)
                .let { remoteConfigValue ->
                    remoteConfigValue?.asString() ?: ""
                }.also { version ->
                    emit(version)
                }
        }
}

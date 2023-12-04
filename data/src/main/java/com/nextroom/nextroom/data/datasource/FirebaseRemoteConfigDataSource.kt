package com.nextroom.nextroom.data.datasource

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import com.google.firebase.remoteconfig.get
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseRemoteConfigDataSource(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
) {
    suspend fun getFirebaseRemoteConfigValue(key: String): FirebaseRemoteConfigValue? =
        suspendCoroutine { continuation ->
            firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(value = firebaseRemoteConfig[key])
                } else {
                    continuation.resume(value = null)
                }
            }
        }
}

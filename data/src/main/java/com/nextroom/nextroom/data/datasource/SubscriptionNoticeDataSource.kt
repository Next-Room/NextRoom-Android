package com.nextroom.nextroom.data.datasource

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.nextroom.nextroom.data.BuildConfig
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SubscriptionNoticeDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    /**
     * 매장당 문서 하나로 기록한다. 같은 매장이 다시 눌러도 덮어쓰기만 되고 문서가 늘어나지 않는다.
     *
     * [FieldValue.serverTimestamp]는 기기 시계가 틀어진 경우를 대비한 보조 값이고,
     * 기획에서 요구한 "누른 시각"은 [confirmedAt]이다.
     */
    suspend fun putSubscriptionRequiredNoticeConfirmation(
        shopId: String,
        shopName: String,
        confirmedAt: Long,
    ) {
        val data = mapOf(
            FIELD_SHOP_ID to shopId,
            FIELD_SHOP_NAME to shopName,
            FIELD_CONFIRMED_AT to confirmedAt,
            FIELD_RECORDED_AT to FieldValue.serverTimestamp(),
            FIELD_APP_VERSION to BuildConfig.APP_VERSION,
        )

        suspendCancellableCoroutine { continuation ->
            confirmationDocument(shopId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener { continuation.resume(Unit) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

    /**
     * 확인 기록 존재 여부.
     *
     * Firestore는 오프라인 캐시가 기본으로 켜져 있어 네트워크가 없어도 캐시로 응답하고,
     * 쓰기는 로컬에 먼저 반영되므로 방금 남긴 기록도 바로 존재하는 것으로 조회된다.
     */
    suspend fun hasSubscriptionRequiredNoticeConfirmation(shopId: String): Boolean {
        return suspendCancellableCoroutine { continuation ->
            confirmationDocument(shopId)
                .get()
                .addOnSuccessListener { continuation.resume(it.exists()) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

    private fun confirmationDocument(shopId: String): DocumentReference {
        return firestore
            .collection(COLLECTION_SUBSCRIPTION_REQUIRED_NOTICE)
            .document(shopId)
    }

    companion object {
        private const val COLLECTION_SUBSCRIPTION_REQUIRED_NOTICE =
            "subscription_required_notice_confirmations"
        private const val FIELD_SHOP_ID = "shopId"
        private const val FIELD_SHOP_NAME = "shopName"
        private const val FIELD_CONFIRMED_AT = "confirmedAt"
        private const val FIELD_RECORDED_AT = "recordedAt"
        private const val FIELD_APP_VERSION = "appVersion"
    }
}

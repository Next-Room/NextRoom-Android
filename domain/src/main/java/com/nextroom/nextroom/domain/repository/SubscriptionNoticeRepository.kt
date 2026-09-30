package com.nextroom.nextroom.domain.repository

import com.nextroom.nextroom.domain.model.Result

/**
 * 유료화 전환 안내에 대한 매장의 확인 이력.
 *
 * 서버 리소스를 쓰지 않고 전환 기간에만 필요한 데이터라서 Firestore에 직접 적재한다.
 * 확인 여부의 기준도 이 기록 하나뿐이라, 기기를 바꾸거나 앱을 다시 설치해도 중복 안내하지 않는다.
 */
interface SubscriptionNoticeRepository {

    /** [shopId] 매장이 구독 필수 안내 팝업의 확인 버튼을 누른 적이 있는지 여부 */
    suspend fun hasConfirmedSubscriptionRequiredNotice(shopId: String): Result<Boolean>

    /**
     * 구독 필수 안내 팝업의 확인 버튼을 누른 사실과 누른 시각을 기록한다.
     *
     * @param shopId 확인 버튼을 누른 매장 식별자
     * @param shopName 확인 버튼을 누른 매장 이름. 콘솔에서 매장을 알아보기 위한 값이다.
     * @param confirmedAt 확인 버튼을 누른 시각(epoch millis)
     */
    suspend fun recordSubscriptionRequiredNoticeConfirmed(
        shopId: String,
        shopName: String,
        confirmedAt: Long,
    ): Result<Unit>
}

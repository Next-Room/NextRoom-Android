package com.nextroom.nextroom.presentation.ui.mypage

import com.nextroom.nextroom.domain.model.UserSubscribeStatus
import com.nextroom.nextroom.domain.model.UserSubscription

data class MypageState(
    val shopName: String = "",
    val userSubscribeStatus: UserSubscribeStatus = UserSubscribeStatus(),
    val userSubscription: UserSubscription? = null,
) {
    val period: String
        get() = run {
            userSubscription?.let { subs ->
                subs.createdAt.substringBefore(' ').replace("-", ".") +
                    subs.expiryDate.replace("-", ".")
            } ?: ""
        }
}

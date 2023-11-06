package com.nextroom.nextroom.presentation.ui.mypage

import com.nextroom.nextroom.domain.model.UserSubscribeStatus
import com.nextroom.nextroom.domain.model.UserSubscription

data class MypageState(
    val loading: Boolean = false,
    val shopName: String = "",
    val userSubscribeStatus: UserSubscribeStatus = UserSubscribeStatus(),
    val userSubscription: UserSubscription? = null,
) {
    val period: String
        get() = run {
            userSubscription?.let { subs ->
                StringBuilder(subs.createdAt.substringBefore(' ').replace("-", "."))
                    .append(" ~ ")
                    .append(subs.expiryDate.replace("-", "."))
                    .toString()
            } ?: ""
        }
}

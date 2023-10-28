package com.nextroom.nextroom.presentation.ui.mypage

import com.nextroom.nextroom.domain.model.UserSubscribe
import com.nextroom.nextroom.domain.model.UserSubscribeStatus

data class MypageState(
    val shopName: String = "",
    val userSubscribeStatus: UserSubscribeStatus = UserSubscribeStatus(),
    val userSubscribe: UserSubscribe? = null,
)

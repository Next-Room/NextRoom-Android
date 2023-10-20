package com.nextroom.nextroom.presentation.ui.mypage

import com.nextroom.nextroom.domain.model.UserSubscribe

data class MypageState(
    val shopName: String = "",
    val userSubscribe: UserSubscribe? = null,
)

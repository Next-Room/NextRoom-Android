package com.nextroom.nextroom.presentation.ui.mypage

sealed interface MypageEvent {
    data object Logout : MypageEvent
}

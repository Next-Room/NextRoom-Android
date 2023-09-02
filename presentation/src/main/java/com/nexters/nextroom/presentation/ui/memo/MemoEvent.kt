package com.nexters.nextroom.presentation.ui.memo

sealed interface MemoEvent {
    object EraseAll : MemoEvent
}

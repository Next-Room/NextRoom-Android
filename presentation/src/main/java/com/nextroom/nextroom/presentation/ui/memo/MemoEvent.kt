package com.nextroom.nextroom.presentation.ui.memo

sealed interface MemoEvent {
    object EraseAll : MemoEvent
}

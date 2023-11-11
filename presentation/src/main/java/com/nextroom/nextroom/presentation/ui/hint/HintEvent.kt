package com.nextroom.nextroom.presentation.ui.hint

sealed interface HintEvent {
    data object OpenAnswer : HintEvent
}

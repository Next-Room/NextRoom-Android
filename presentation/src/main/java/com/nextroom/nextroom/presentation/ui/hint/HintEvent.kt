package com.nextroom.nextroom.presentation.ui.hint

import com.nextroom.nextroom.domain.model.Hint

sealed interface HintEvent {
    data class ShowToast(val message: String) : HintEvent
    data class HintUsed(val usedHint: Hint) : HintEvent // New event for shared state update
    // ... other Hint events
}

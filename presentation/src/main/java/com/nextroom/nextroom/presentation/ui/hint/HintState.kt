package com.nextroom.nextroom.presentation.ui.hint

import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.model.Hint

data class HintState(
    val loading: Boolean = false,
    val hint: Hint = Hint(),
    val userSubscribeStatus: SubscribeStatus = SubscribeStatus.Default,
    val networkDisconnectedCount: Int = 0,
    val isHintOpened: Boolean = false
)

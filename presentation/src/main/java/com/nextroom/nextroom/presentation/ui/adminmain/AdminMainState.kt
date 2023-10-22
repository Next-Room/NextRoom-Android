package com.nextroom.nextroom.presentation.ui.adminmain

import com.nextroom.nextroom.domain.model.UserSubscribeStatus
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation

data class AdminMainState(
    val userSubscribeStatus: UserSubscribeStatus = UserSubscribeStatus(),
    val showName: String = "",
    val themes: List<ThemeInfoPresentation> = emptyList(),
)

package com.nextroom.nextroom.presentation.ui.adminmain

import com.nextroom.nextroom.domain.model.Banner
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation

data class AdminMainState(
    val loading: Boolean = false,
    val opaqueLoading: Boolean = false,
    val subscribeStatus: SubscribeStatus = SubscribeStatus.Default,
    val shopName: String = "",
    val themes: List<ThemeInfoPresentation> = emptyList(),
    val banner: Banner? = null
)

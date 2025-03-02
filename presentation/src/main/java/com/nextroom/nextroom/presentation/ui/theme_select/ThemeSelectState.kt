package com.nextroom.nextroom.presentation.ui.theme_select

import com.nextroom.nextroom.domain.model.Banner
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation

data class ThemeSelectState(
    val loading: Boolean = false,
    val opaqueLoading: Boolean = false,
    val subscribeStatus: SubscribeStatus = SubscribeStatus.Default,
    val shopName: String = "",
    val themes: List<ThemeInfoPresentation> = emptyList(),
    val banners: List<Banner> = emptyList(),
    val currentBannerPosition: Int = 0,
)

package com.nextroom.nextroom.presentation.ui.theme_select

import com.nextroom.nextroom.domain.model.Banner
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation

data class ThemeSelectUiState(
    val loading: Boolean = false,
    val opaqueLoading: Boolean = true,
    val subscribeStatus: SubscribeStatus = SubscribeStatus.Default,
    val shopName: String = "",
    val themes: List<ThemeInfoPresentation> = emptyList(),
    val banners: List<Banner> = emptyList(),
    val recentUpdatedDate: Long? = null,
)

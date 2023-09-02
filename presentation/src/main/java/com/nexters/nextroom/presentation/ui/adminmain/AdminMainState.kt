package com.nexters.nextroom.presentation.ui.adminmain

import com.nexters.nextroom.presentation.model.ThemeInfoPresentation

data class AdminMainState(
    val showName: String = "",
    val themes: List<ThemeInfoPresentation> = emptyList(),
)

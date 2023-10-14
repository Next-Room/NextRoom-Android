package com.nextroom.nextroom.presentation.ui.adminmain

import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation

data class AdminMainState(
    val showName: String = "",
    val themes: List<ThemeInfoPresentation> = emptyList(),
)

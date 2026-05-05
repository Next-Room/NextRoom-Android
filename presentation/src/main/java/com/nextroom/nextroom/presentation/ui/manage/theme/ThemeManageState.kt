package com.nextroom.nextroom.presentation.ui.manage.theme

import com.nextroom.nextroom.domain.model.ThemeInfo

data class ThemeEditingState(
    val themeId: Int? = null,
    val title: String = "",
    val timeLimit: Int? = null,
    val hintLimit: Int? = null,
)

sealed interface ThemeManageUiState {
    data object Loading : ThemeManageUiState
    data class Loaded(
        val themes: List<ThemeInfo>,
        val isLoading: Boolean,
        val sheetType: ThemeSheetType = ThemeSheetType.None,
        val editingState: ThemeEditingState = ThemeEditingState(),
    ) : ThemeManageUiState
}

enum class ThemeSheetType { None, Add, Edit }

sealed interface ThemeManageEvent {
    data object ThemeSaved : ThemeManageEvent
    data object ThemeDeleted : ThemeManageEvent
}

package com.nextroom.nextroom.presentation.ui.manage.theme

import com.nextroom.nextroom.domain.model.ThemeInfo
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.ThemeRepository
import com.nextroom.nextroom.domain.request.AddThemeRequest
import com.nextroom.nextroom.domain.request.EditThemeRequest
import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeManageViewModel @Inject constructor(
    private val themeRepository: ThemeRepository,
) : NewBaseViewModel() {

    private val _themes = MutableStateFlow<List<ThemeInfo>?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _sheetType = MutableStateFlow(ThemeSheetType.None)
    private val _editingState = MutableStateFlow(ThemeEditingState())

    val uiState = combine(
        _themes,
        _isLoading,
        _sheetType,
        _editingState,
    ) { themes, isLoading, sheetType, editingState ->
        if (themes == null) {
            ThemeManageUiState.Loading
        } else {
            ThemeManageUiState.Loaded(
                themes = themes,
                isLoading = isLoading,
                sheetType = sheetType,
                editingState = editingState,
            )
        }
    }.stateIn(baseViewModelScope, SharingStarted.Lazily, ThemeManageUiState.Loading)

    private val _uiEvent = MutableSharedFlow<ThemeManageEvent>(extraBufferCapacity = 1)
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadThemes()
    }

    fun loadThemes() {
        baseViewModelScope.launch {
            try {
                _isLoading.emit(true)
                themeRepository.getThemes().getOrThrow.also { themes ->
                    _themes.emit(themes)
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    fun showAddSheet() {
        _editingState.value = ThemeEditingState()
        _sheetType.value = ThemeSheetType.Add
    }

    fun showEditSheet(theme: ThemeInfo) {
        _editingState.value = ThemeEditingState(
            themeId = theme.id,
            title = theme.title,
            timeLimit = theme.timeLimitInMinute,
            hintLimit = if (theme.hintLimit == -1) null else theme.hintLimit,
        )
        _sheetType.value = ThemeSheetType.Edit
    }

    fun hideSheet() {
        _sheetType.value = ThemeSheetType.None
    }

    fun updateTitle(title: String) {
        _editingState.update { it.copy(title = title) }
    }

    fun updateTimeLimit(timeLimit: String?) {
        _editingState.update { it.copy(timeLimit = timeLimit?.toIntOrNull()) }
    }

    fun updateHintLimit(hintLimit: String?) {
        _editingState.update { it.copy(hintLimit = hintLimit?.toIntOrNull()) }
    }

    fun saveTheme() {
        _sheetType.value = ThemeSheetType.None
        val editing = _editingState.value
        baseViewModelScope.launch {
            try {
                _isLoading.emit(true)
                val result = if (editing.themeId == null) {
                    themeRepository.addTheme(
                        AddThemeRequest(
                            title = editing.title,
                            timeLimit = requireNotNull(editing.timeLimit),
                            hintLimit = requireNotNull(editing.hintLimit),
                        )
                    )
                } else {
                    themeRepository.editTheme(
                        EditThemeRequest(
                            id = editing.themeId,
                            title = editing.title,
                            timeLimit = requireNotNull(editing.timeLimit),
                            hintLimit = requireNotNull(editing.hintLimit),
                        )
                    )
                }
                result
                    .onSuccess {
                        loadThemes()
                        _uiEvent.emit(ThemeManageEvent.ThemeSaved)
                    }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    fun confirmDelete(themeId: Int) {
        baseViewModelScope.launch {
            try {
                _isLoading.emit(true)
                themeRepository.deleteTheme(themeId).onSuccess {
                    _themes.update { current -> current?.filter { it.id != themeId } }
                    _uiEvent.emit(ThemeManageEvent.ThemeDeleted)
                }
            } catch (e: Exception) {
                handleError(e)
            } finally {
                _isLoading.emit(false)
            }
        }
    }
}

package com.nextroom.nextroom.presentation.ui.login

import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(

) : NewBaseViewModel() {
    private val _selectedSignupSource = MutableStateFlow<UIState.Loaded.SelectedItem?>(null)
    private val _selectedSignupReason = MutableStateFlow<UIState.Loaded.SelectedItem?>(null)

    val uiState = combine(
        _selectedSignupSource,
        _selectedSignupReason
    ) { selectedSignupSource, selectedSignupReason ->
        UIState.Loaded(selectedSignupSource, selectedSignupReason)
    }.stateIn(baseViewModelScope, SharingStarted.Lazily, UIState.Loading)

    fun setSelectedSignupSource(selectedItem: UIState.Loaded.SelectedItem) {
        _selectedSignupSource.update { selectedItem }
    }

    sealed interface UIState {
        data object Loading : UIState
        data class Loaded(
            val selectedSignupSource: SelectedItem?,
            val selectedSignupReason: SelectedItem?,
        ) : UIState {
            data class SelectedItem(
                val id: String,
                val text: String,
            )
        }
    }
}
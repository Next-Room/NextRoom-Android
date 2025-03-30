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
    private val _serviceTermAgree = MutableStateFlow(false)
    private val _marketingTermAgree = MutableStateFlow(false)

    val uiState = combine(
        _selectedSignupSource,
        _selectedSignupReason,
        _serviceTermAgree,
        _marketingTermAgree,
    ) { selectedSignupSource, selectedSignupReason, serviceTermAgree, marketingTermAgree ->
        UIState.Loaded(
            selectedSignupSource = selectedSignupSource,
            selectedSignupReason = selectedSignupReason,
            serviceTermAgreed = serviceTermAgree,
            marketingTermAgreed = marketingTermAgree,
            allTermsAgreed = serviceTermAgree && marketingTermAgree
        )
    }.stateIn(baseViewModelScope, SharingStarted.Lazily, UIState.Loading)

    fun setSelectedSignupSource(selectedItem: UIState.Loaded.SelectedItem) {
        _selectedSignupSource.update { selectedItem }
    }

    fun setSelectedSignupReason(selectedItem: UIState.Loaded.SelectedItem) {
        _selectedSignupReason.update { selectedItem }
    }

    fun onAllTermsAgreeClicked(agree: Boolean) {
        _serviceTermAgree.update { agree }
        _marketingTermAgree.update { agree }
    }

    fun setServiceTermAgree(agree: Boolean) {
        _serviceTermAgree.update { agree }
    }

    fun setMarketingTermAgree(agree: Boolean) {
        _marketingTermAgree.update { agree }
    }

    sealed interface UIState {
        data object Loading : UIState
        data class Loaded(
            val selectedSignupSource: SelectedItem?,
            val selectedSignupReason: SelectedItem?,
            val serviceTermAgreed: Boolean,
            val marketingTermAgreed: Boolean,
            val allTermsAgreed: Boolean,
        ) : UIState {
            data class SelectedItem(
                val id: String,
                val text: String,
            )
        }
    }
}
package com.nextroom.nextroom.presentation.ui.login

import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(

) : NewBaseViewModel() {
    private val _shopName = MutableStateFlow<String?>(null)
    private val _selectedSignupSource = MutableStateFlow<UIState.Loaded.SelectedItem?>(null)
    private val _selectedSignupReason = MutableStateFlow<UIState.Loaded.SelectedItem?>(null)
    private val _serviceTermAgree = MutableStateFlow(false)
    private val _marketingTermAgree = MutableStateFlow(false)

    val uiState = combine(
        _shopName,
        _selectedSignupSource,
        _selectedSignupReason,
        _serviceTermAgree,
        _marketingTermAgree,
    ) { shopName, selectedSignupSource, selectedSignupReason, serviceTermAgree, marketingTermAgree ->
        UIState.Loaded(
            shopName = shopName,
            selectedSignupSource = selectedSignupSource,
            selectedSignupReason = selectedSignupReason,
            serviceTermAgreed = serviceTermAgree,
            marketingTermAgreed = marketingTermAgree,
            allTermsAgreed = serviceTermAgree && marketingTermAgree,
            allRequiredFieldFilled = !shopName.isNullOrEmpty() && selectedSignupSource != null && serviceTermAgree
        )
    }.stateIn(baseViewModelScope, SharingStarted.Lazily, UIState.Loading)

    fun onShopNameChanged(shopName: String?) {
        baseViewModelScope.launch {
            shopName
                .takeIf { !it.isNullOrEmpty() }
                .also { _shopName.emit(it) }
        }
    }

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
            val shopName: String?,
            val selectedSignupSource: SelectedItem?,
            val selectedSignupReason: SelectedItem?,
            val serviceTermAgreed: Boolean,
            val marketingTermAgreed: Boolean,
            val allTermsAgreed: Boolean,
            val allRequiredFieldFilled: Boolean,
        ) : UIState {
            data class SelectedItem(
                val id: String,
                val text: String,
            )
        }
    }
}
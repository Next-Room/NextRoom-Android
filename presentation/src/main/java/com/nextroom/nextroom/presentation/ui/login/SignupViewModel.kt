package com.nextroom.nextroom.presentation.ui.login

import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.AdminRepository
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
class SignupViewModel @Inject constructor(
    val adminRepository: AdminRepository,
) : NewBaseViewModel() {
    private val _shopName = MutableStateFlow<String?>(null)
    private val _selectedSignupSource = MutableStateFlow<UIState.Loaded.SelectedItem?>(null)
    private val _selectedSignupReason = MutableStateFlow<UIState.Loaded.SelectedItem?>(null)
    private val _customSignupSource = MutableStateFlow<String?>(null)
    private val _customSignupReason = MutableStateFlow<String?>(null)
    private val _serviceTermAgree = MutableStateFlow(false)
    private val _marketingTermAgree = MutableStateFlow(false)
    private val _apiLoading = MutableStateFlow(false)

    private val inputs = combine(
        _shopName,
        _selectedSignupSource,
        _selectedSignupReason,
        _customSignupSource,
        _customSignupReason,
    ) { shopName, selectedSignupSource, selectedSignupReason, customSignupSource, customSignupReason ->
        Inputs(
            shopName = shopName,
            selectedSignupSource = selectedSignupSource,
            selectedSignupReason = selectedSignupReason,
            customSignupSource = customSignupSource,
            customSignupReason = customSignupReason,
        )
    }

    private val terms = combine(_serviceTermAgree, _marketingTermAgree) { service, marketing ->
        service to marketing
    }

    val uiState = combine(inputs, terms) { input, (serviceTermAgree, marketingTermAgree) ->
        UIState.Loaded(
            shopName = input.shopName,
            selectedSignupSource = input.selectedSignupSource,
            selectedSignupReason = input.selectedSignupReason,
            customSignupSource = input.customSignupSource,
            customSignupReason = input.customSignupReason,
            serviceTermAgreed = serviceTermAgree,
            marketingTermAgreed = marketingTermAgree,
            allTermsAgreed = serviceTermAgree && marketingTermAgree,
            allRequiredFieldFilled = !input.shopName.isNullOrEmpty() && input.selectedSignupSource != null && serviceTermAgree
        )
    }.combine(_apiLoading) { loaded, loading ->
        if (loading) UIState.Loading else loaded
    }.stateIn(baseViewModelScope, SharingStarted.Lazily, UIState.Loading)

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

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

    fun setCustomSignupSource(text: String?) {
        _customSignupSource.update { text }
    }

    fun setCustomSignupReason(text: String?) {
        _customSignupReason.update { text }
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

    fun signup() {
        baseViewModelScope.launch {
            val shopName = requireNotNull(_shopName.value.toString())
            val signupSource =
                _customSignupSource.value.takeIf { !it.isNullOrEmpty() } ?: _selectedSignupSource.value?.text ?: ""
            val signupReason =
                _customSignupReason.value.takeIf { !it.isNullOrEmpty() } ?: _selectedSignupReason.value?.text ?: ""

            _apiLoading.emit(true)
            adminRepository.putAdditionalUserInfo(
                shopName = shopName,
                signupSource = signupSource,
                signupReason = signupReason,
                marketingTermAgreed = _marketingTermAgree.value
            ).onSuccess {
                _uiEvent.emit(UIEvent.SignupSuccess)
            }.onFailure {
                _uiEvent.emit(UIEvent.SignupFailure)
            }
            _apiLoading.emit(false)
        }
    }

    private data class Inputs(
        val shopName: String?,
        val selectedSignupSource: UIState.Loaded.SelectedItem?,
        val selectedSignupReason: UIState.Loaded.SelectedItem?,
        val customSignupSource: String?,
        val customSignupReason: String?,
    )

    sealed interface UIState {
        data object Loading : UIState
        data class Loaded(
            val shopName: String?,
            val selectedSignupSource: SelectedItem?,
            val selectedSignupReason: SelectedItem?,
            val customSignupSource: String?,
            val customSignupReason: String?,
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

    sealed interface UIEvent {
        data object SignupSuccess : UIEvent
        data object SignupFailure : UIEvent
    }
}
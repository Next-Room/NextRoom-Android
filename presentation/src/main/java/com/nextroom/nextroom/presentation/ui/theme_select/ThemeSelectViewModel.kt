package com.nextroom.nextroom.presentation.ui.theme_select

import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.model.suspendOnSuccess
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.domain.repository.BannerRepository
import com.nextroom.nextroom.domain.repository.DataStoreRepository
import com.nextroom.nextroom.domain.repository.HintRepository
import com.nextroom.nextroom.domain.repository.ThemeRepository
import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation
import com.nextroom.nextroom.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeSelectViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val themeRepository: ThemeRepository,
    private val hintRepository: HintRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val bannerRepository: BannerRepository
) : NewBaseViewModel() {

    private val _uiState = MutableStateFlow(
        ThemeSelectUiState(
            opaqueLoading = true,
            loading = true,
            recentUpdatedDate = null
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ThemeSelectEvent>(extraBufferCapacity = 1)
    val uiEvent = _uiEvent.asSharedFlow()

    private var shownBackgroundCustomDialog = false

    init {
        showInAppReview()

        baseViewModelScope.launch {
            adminRepository.shopName.collect { shopName ->
                _uiState.update { it.copy(shopName = shopName) }
            }
        }
        baseViewModelScope.launch {
            if (dataStoreRepository.getHasSeenGuidePopup().not()) {
                _uiEvent.emit(ThemeSelectEvent.GuidePopupNotSeen)
                dataStoreRepository.setHasSeenGuidePopup()
            }
        }
    }

    fun onResume() {
        loadData()
    }

    fun incrementNetworkDisconnectedCount() {
        baseViewModelScope.launch {
            val count = dataStoreRepository.getNetworkDisconnectedCount()
            dataStoreRepository.setNetworkDisconnectedCount(count + 1)
        }
    }

    private fun showInAppReview() {
        baseViewModelScope.launch {
            delay(200)
            _uiEvent.emit(ThemeSelectEvent.InAppReview)
        }
    }

    fun loadData() {
        baseViewModelScope.launch {
            suspend fun inactiveAllThemeBG(themes: List<ThemeInfoPresentation>) {
                themeRepository.activateThemeBackgroundImage(
                    activeThemeIdList = emptyList(),
                    deActiveThemeIdList = themes.map { it.id }
                )
            }

            suspend fun handleThemeActivationBySubscription(
                subscribeStatus: SubscribeStatus,
                themes: List<ThemeInfoPresentation>,
            ) {
                when (subscribeStatus) {
                    SubscribeStatus.Subscribed -> Unit
                    SubscribeStatus.Default,
                    SubscribeStatus.SUBSCRIPTION_EXPIRATION -> {
                        val activeThemeImageCount = themes.count { it.useTimerUrl }
                        if (activeThemeImageCount > LIMITED_CUSTOM_BG_COUNT_FOR_FREE) {
                            inactiveAllThemeBG(themes)
                        }
                    }
                }
            }

            _uiState.update { it.copy(loading = true) }
            adminRepository.getUserSubscribe().suspendOnSuccess { myPage ->
                _uiState.update { it.copy(subscribeStatus = myPage.status) }

                getThemes()

                val currentState = _uiState.value
                handleThemeActivationBySubscription(
                    currentState.subscribeStatus,
                    currentState.themes
                )

                bannerRepository
                    .getBanners()
                    .onSuccess { banners ->
                        _uiState.update { it.copy(banners = banners) }
                    }

                if (!shouldHideRecommendBackgroundCustomDialogUntil()
                    && !shownBackgroundCustomDialog
                ) {
                    shownBackgroundCustomDialog = true
                    _uiEvent.emit(ThemeSelectEvent.RecommendBackgroundCustom)
                }
            }.onFailure(::handleResultError)
            _uiState.update { it.copy(opaqueLoading = false, loading = false) }
        }
    }

    private suspend fun getThemes() {
        themeRepository.getThemes().onSuccess { themes ->
            themes
                .map { it.toPresentation() }
                .also { presentations ->
                    _uiState.update {
                        it.copy(
                            themes = presentations,
                            recentUpdatedDate = System.currentTimeMillis(),
                        )
                    }
                }

            themes.forEach { themeInfo ->
                hintRepository.saveHints(themeInfo.id).onFailure(::handleResultError)
            }
            dataStoreRepository.setNetworkDisconnectedCount(0)
        }.onFailure(::handleResultError)
    }

    private fun shouldHideRecommendBackgroundCustomDialogUntil(): Boolean {
        val hideUntil = dataStoreRepository.getRecommendBackgroundCustomDialogHiddenUntil()
        return System.currentTimeMillis() < hideUntil
    }

    fun tryGameStart(themeId: Int) {
        baseViewModelScope.launch {
            _uiState.update { it.copy(opaqueLoading = true) }
            themeRepository.updateLatestTheme(themeId)
            adminRepository.getUserSubscribe().suspendOnSuccess { myPage ->
                _uiEvent.emit(ThemeSelectEvent.ReadyToGameStart(myPage.status))
            }.onFailure(::handleResultError)
            _uiState.update { it.copy(opaqueLoading = false) }
        }
    }

    private fun checkNeedToSetPassword() {
        baseViewModelScope.launch {
            if (adminRepository.getAppPassword().isEmpty()) {
                _uiEvent.emit(ThemeSelectEvent.NeedToSetPassword)
            }
        }
    }

    fun onThemeClicked(themeId: String) {
        baseViewModelScope.launch {
            checkNeedToSetPassword()
            if (adminRepository.getAppPassword().isEmpty()) {
                ThemeSelectEvent.NeedToSetPassword
            } else {
                ThemeSelectEvent.NeedToCheckPasswordForStartGame(themeId)
            }.also {
                _uiEvent.emit(it)
            }
        }
    }

    fun onManageThemesClicked() {
        baseViewModelScope.launch {
            checkNeedToSetPassword()
            if (adminRepository.getAppPassword().isEmpty()) {
                ThemeSelectEvent.NeedToSetPassword
            } else {
                ThemeSelectEvent.NeedToCheckPasswordForManageThemes
            }.also {
                _uiEvent.emit(it)
            }
        }
    }

    fun onThemeRefreshClicked() {
        baseViewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            getThemes()
            _uiState.update { it.copy(loading = false) }
        }
    }

    private fun handleResultError(error: Result.Failure) {
        baseViewModelScope.launch {
            when (error) {
                is Result.Failure.NetworkError -> _uiEvent.emit(ThemeSelectEvent.NetworkError)
                is Result.Failure.HttpError -> _uiEvent.emit(ThemeSelectEvent.ClientError(error.message))
                else -> _uiEvent.emit(ThemeSelectEvent.UnknownError)
            }
        }
    }

    companion object {
        const val LIMITED_CUSTOM_BG_COUNT_FOR_FREE = 1
    }
}

package com.nextroom.nextroom.presentation.ui.theme_select

import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.model.suspendOnSuccess
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.domain.repository.BannerRepository
import com.nextroom.nextroom.domain.repository.DataStoreRepository
import com.nextroom.nextroom.domain.repository.FirebaseRemoteConfigRepository
import com.nextroom.nextroom.domain.repository.FirebaseRemoteConfigRepository.Companion.REMOTE_KEY_SUBSCRIPTION_REQUIRED_DATE
import com.nextroom.nextroom.domain.repository.HintRepository
import com.nextroom.nextroom.domain.repository.ThemeRepository
import com.nextroom.nextroom.presentation.base.NewBaseViewModel
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation
import com.nextroom.nextroom.presentation.model.toPresentation
import com.nextroom.nextroom.presentation.ui.theme_select.ThemeSelectViewModel.Companion.DATE_PATTERN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class ThemeSelectViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val themeRepository: ThemeRepository,
    private val hintRepository: HintRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val bannerRepository: BannerRepository,
    private val firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository,
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
        }.onFailure(::handleResultError)
    }

    fun tryGameStart(themeId: Int) {
        baseViewModelScope.launch {
            _uiState.update { it.copy(opaqueLoading = true) }
            themeRepository.updateLatestTheme(themeId)
            adminRepository.getUserSubscribe().suspendOnSuccess { myPage ->
                if (canStartGame(myPage.status)) {
                    _uiEvent.emit(ThemeSelectEvent.ReadyToGameStart(myPage.status))
                } else {
                    _uiEvent.emit(ThemeSelectEvent.NeedSubscriptionForGameStart)
                }
            }.onFailure(::handleResultError)
            _uiState.update { it.copy(opaqueLoading = false) }
        }
    }

    /**
     * 구독 필수 시점(KST 자정) 이전에는 구독 상태와 무관하게 시작할 수 있고,
     * 그 시점부터는 구독 중인 경우에만 시작할 수 있다.
     */
    private suspend fun canStartGame(subscribeStatus: SubscribeStatus): Boolean {
        if (subscribeStatus == SubscribeStatus.Subscribed) {
            return true
        }

        // Remote Config 값을 받아오지 못했거나 형식이 올바르지 않으면 기본값 사용
        val subscriptionRequiredAt = parseStartOfDay(getSubscriptionRequiredDate())
            ?: parseStartOfDay(DEFAULT_SUBSCRIPTION_REQUIRED_DATE)
            ?: return true

        return System.currentTimeMillis() < subscriptionRequiredAt
    }

    /** Remote Config 조회 실패 시 빈 문자열을 반환해 기본 날짜를 사용하게 한다 */
    private suspend fun getSubscriptionRequiredDate(): String {
        return runCatching {
            firebaseRemoteConfigRepository
                .getFirebaseRemoteConfigValue(REMOTE_KEY_SUBSCRIPTION_REQUIRED_DATE)
                .first()
        }.getOrDefault("")
    }

    /** [date]가 [DATE_PATTERN] 형식이면 해당 날짜 KST 자정의 epoch millis, 아니면 null */
    private fun parseStartOfDay(date: String): Long? {
        return try {
            SimpleDateFormat(DATE_PATTERN, Locale.KOREA)
                .apply {
                    timeZone = TimeZone.getTimeZone(TIME_ZONE_KST)
                    isLenient = false
                }.parse(date)
                ?.time
        } catch (_: Exception) {
            null
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
        private const val DEFAULT_SUBSCRIPTION_REQUIRED_DATE = "2026-10-01"
        private const val DATE_PATTERN = "yyyy-MM-dd"
        private const val TIME_ZONE_KST = "Asia/Seoul"
    }
}

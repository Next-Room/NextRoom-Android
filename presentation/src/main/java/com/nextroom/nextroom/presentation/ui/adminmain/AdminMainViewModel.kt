package com.nextroom.nextroom.presentation.ui.adminmain

import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.model.suspendOnSuccess
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.domain.repository.BannerRepository
import com.nextroom.nextroom.domain.repository.DataStoreRepository
import com.nextroom.nextroom.domain.repository.HintRepository
import com.nextroom.nextroom.domain.repository.ThemeRepository
import com.nextroom.nextroom.presentation.base.BaseViewModel
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation
import com.nextroom.nextroom.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class AdminMainViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val themeRepository: ThemeRepository,
    private val hintRepository: HintRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val bannerRepository: BannerRepository
) : BaseViewModel<AdminMainState, AdminMainEvent>() {

    override val container: Container<AdminMainState, AdminMainEvent> = container(AdminMainState(loading = true))

    init {
        showInAppReview()
        updateBackgroundSettingsNoticeShown()

        viewModelScope.launch {
            adminRepository.shopName.collect {
                updateShopInfo(it)
            }
        }
    }

    private fun updateBackgroundSettingsNoticeShown() = intent {
        reduce {
            state.copy(backgroundSettingsNoticeShown = dataStoreRepository.getBackgroundSettingsNoticeShown())
        }
    }

    fun onResume() {
        loadData()
        checkNeedToSetPassword()
    }

    fun incrementNetworkDisconnectedCount() {
        viewModelScope.launch {
            val count = dataStoreRepository.getNetworkDisconnectedCount()
            updateNetworkDisconnectedCount(count + 1)
        }
    }

    private fun updateNetworkDisconnectedCount(count: Int) {
        dataStoreRepository.setNetworkDisconnectedCount(count)
    }

    private fun showInAppReview() = intent {
        viewModelScope.launch {
            delay(200)
            postSideEffect(AdminMainEvent.InAppReview)
        }
    }

    fun updateTheme(themeId: Int) = intent {
        themeRepository
            .getThemes()
            .onSuccess { themes ->
                themes
                    .find { it.id == themeId }
                    ?.let { themeRepository.upsertTheme(it) }
            }.onFailure {
                handleError(it)
                return@intent
            }

        hintRepository.saveHints(themeId).onSuccess { updatedAt ->
            reduce {
                state.copy(
                    themes = state.themes.toMutableList()
                        .map { if (it.id == themeId) it.copy(recentUpdated = updatedAt) else it },
                )
            }
        }.onFailure(::handleError)
    }

    fun loadData() = intent {
        reduce { state.copy(loading = true) }
        adminRepository.getUserSubscribe().suspendOnSuccess { myPage ->
            reduce { state.copy(subscribeStatus = myPage.status) }
            themeRepository.getThemes().onSuccess {
                updateThemes(
                    it.map { themeInfo ->
                        val updatedAt = themeRepository.getUpdatedInfo(themeInfo.id)
                        themeInfo.toPresentation(updatedAt)
                    },
                )
                updateNetworkDisconnectedCount(0)
            }.onFailure(::handleError)

            bannerRepository
                .getBanners()
                .onSuccess {
                    reduce { state.copy(banners = it) }
                }

            if (!shouldHideRecommendBackgroundCustomDialog()) {
                postSideEffect(AdminMainEvent.RecommendBackgroundCustom)
            }
        }
        reduce { state.copy(loading = false) }
    }

    fun onBackgroundSettingsNoticeClicked() {
        dataStoreRepository.updateBackgroundSettingsShown()
    }

    private fun shouldHideRecommendBackgroundCustomDialog(): Boolean {
        val hideUntil = dataStoreRepository.getRecommendBackgroundCustomDialogHidden()
        return System.currentTimeMillis() < hideUntil
    }

    private fun updateShopInfo(shopName: String) = intent {
        reduce { state.copy(shopName = shopName) }
    }

    private fun updateThemes(themes: List<ThemeInfoPresentation>) = intent {
        reduce { state.copy(themes = themes) }
    }

    fun tryGameStart(themeId: Int) = intent {
        reduce { state.copy(opaqueLoading = true) }
        themeRepository.updateLatestTheme(themeId)
        adminRepository.getUserSubscribe().suspendOnSuccess { myPage ->
            postSideEffect(AdminMainEvent.ReadyToGameStart(myPage.status))
        }.onFailure(::handleError)
        reduce { state.copy(opaqueLoading = false) }
    }

    private fun checkNeedToSetPassword() {
        viewModelScope.launch {
            if (adminRepository.getAppPassword().isEmpty()) {
                intent {
                    postSideEffect(AdminMainEvent.NeedToSetPassword)
                }
            }
        }
    }

    fun onThemeClicked(themeId: String) {
        viewModelScope.launch {
            intent {
                if (adminRepository.getAppPassword().isEmpty()) {
                    AdminMainEvent.NeedToSetPassword
                } else {
                    AdminMainEvent.NeedToCheckPasswordForStartGame(themeId)
                }.also { postSideEffect(it) }
            }
        }
    }

    private fun handleError(error: Result.Failure) = intent {
        when (error) {
            is Result.Failure.NetworkError -> postSideEffect(AdminMainEvent.NetworkError)
            is Result.Failure.HttpError -> postSideEffect(AdminMainEvent.ClientError(error.message))
            else -> postSideEffect(AdminMainEvent.UnknownError)
        }
    }
}

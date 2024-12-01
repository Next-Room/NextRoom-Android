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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ThemeListViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val themeRepository: ThemeRepository,
    private val hintRepository: HintRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val bannerRepository: BannerRepository
) : BaseViewModel<ThemeListState, ThemeListEvent>() {

    override val container: Container<ThemeListState, ThemeListEvent> = container(ThemeListState(loading = true))

    init {
        showInAppReview()

        viewModelScope.launch {
            adminRepository.shopName.collect {
                updateShopInfo(it)
            }
        }
    }

    fun onResume() {
        loadData()
    }

    private fun showInAppReview() = intent {
        viewModelScope.launch {
            delay(200)
            postSideEffect(ThemeListEvent.InAppReview)
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

    fun start(themeId: Int, readyToStart: () -> Unit) = intent {
        themeRepository.updateLatestTheme(themeId)
        withContext(Dispatchers.Main) {
            readyToStart()
        }
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
            }.onFailure(::handleError)

            bannerRepository
                .getBanners()
                .onSuccess {
                    reduce { state.copy(banner = it.firstOrNull()) }
                }
        }
        reduce { state.copy(loading = false) }
    }

    private fun updateShopInfo(shopName: String) = intent {
        reduce { state.copy(shopName = shopName) }
    }

    private fun updateThemes(themes: List<ThemeInfoPresentation>) = intent {
        reduce { state.copy(themes = themes) }
    }

    private fun handleError(error: Result.Failure) = intent {
        when (error) {
            is Result.Failure.NetworkError -> postSideEffect(ThemeListEvent.NetworkError)
            is Result.Failure.HttpError -> postSideEffect(ThemeListEvent.ClientError(error.message))
            else -> postSideEffect(ThemeListEvent.UnknownError)
        }
    }
}

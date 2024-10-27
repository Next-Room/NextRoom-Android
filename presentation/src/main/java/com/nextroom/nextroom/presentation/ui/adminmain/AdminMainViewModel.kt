package com.nextroom.nextroom.presentation.ui.adminmain

import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.model.Mypage
import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onSuccess
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
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
        fetchData()
        fetchBanners()

        viewModelScope.launch {
            adminRepository.shopName.collect {
                updateShopInfo(it)
            }
        }
    }

    private fun fetchBanners() = intent {
        bannerRepository
            .getBanners()
            .onSuccess {
                reduce { state.copy(banner = it.firstOrNull()) }
            }
            .onFailure {
                handleError(it)
            }
    }

    fun start(themeId: Int, readyToStart: () -> Unit) = intent {
        themeRepository.updateLatestTheme(themeId)
        withContext(Dispatchers.Main) {
            readyToStart()
        }
    }

    fun fetchData() = intent {
        reduce { state.copy(loading = true) }

        viewModelScope.launch {
            supervisorScope {
                try {
                    val userSubscribeDeferred = async(Dispatchers.IO) { fetchUserSubscribe() }
                    val themesDeferred = async(Dispatchers.IO) { getThemes() }

                    userSubscribeDeferred.await()
                        .getOrNull
                        ?.let {
                            updateSubscribeStatus(it)
                        }

                    val themes = themesDeferred.await()
                    val updateAtHashMap = mutableMapOf<Int, Long>()
                    saveHints(themes)
                        .associate { it.getOrThrow }
                        .also { updateAtHashMap.putAll(it) }
                    if (state.themes.isEmpty()) {
                        updateThemes(themes)
                    } else {
                        updateThemes(
                            state.themes
                                .toMutableList()
                                .map { if (it.id in updateAtHashMap) it.copy(recentUpdated = updateAtHashMap.getValue(it.id)) else it }
                        )
                    }
                } catch (ex: Exception) {
                    //TODO : 리팩토링 필요. ex로 부터 어떤 에러인지 추출할수가 없음. 중간에 에러 정보를 잃음.
                    handleError(Result.Failure.OperationError(ex))
                }
            }
        }

        reduce { state.copy(loading = false) }
    }

    private fun updateSubscribeStatus(myPage: Mypage) = intent {
        reduce { state.copy(subscribeStatus = myPage.status) }
    }

    private suspend fun fetchUserSubscribe() = adminRepository.getUserSubscribe()

    private suspend fun getThemes(): List<ThemeInfoPresentation> {
        return themeRepository.getThemes()
            .getOrThrow
            .map { themeInfo ->
                themeRepository
                    .getUpdatedInfo(themeInfo.id)
                    .let { updatedAt ->
                        themeInfo.toPresentation(updatedAt)
                    }
            }
    }

    private suspend fun saveHints(themes: List<ThemeInfoPresentation>) = coroutineScope {
        return@coroutineScope themes
            .map { theme -> async(Dispatchers.IO) { hintRepository.saveHints(theme.id) } }
            .awaitAll()
    }

    private fun updateShopInfo(shopName: String) = intent {
        reduce { state.copy(shopName = shopName) }
    }

    private fun updateThemes(themes: List<ThemeInfoPresentation>) = intent {
        reduce { state.copy(themes = themes) }
    }

    private fun handleError(error: Result.Failure) = intent {
        when (error) {
            is Result.Failure.NetworkError -> postSideEffect(AdminMainEvent.NetworkError)
            is Result.Failure.HttpError -> postSideEffect(AdminMainEvent.ClientError(error.message))
            else -> postSideEffect(AdminMainEvent.UnknownError)
        }
    }
}

package com.nextroom.nextroom.presentation.ui.adminmain

import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.domain.repository.DataStoreRepository
import com.nextroom.nextroom.domain.repository.HintRepository
import com.nextroom.nextroom.domain.repository.ThemeRepository
import com.nextroom.nextroom.presentation.base.BaseViewModel
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation
import com.nextroom.nextroom.presentation.model.toPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class AdminMainViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
    private val themeRepository: ThemeRepository,
    private val hintRepository: HintRepository,
    private val dataStoreRepository: DataStoreRepository,
) : BaseViewModel<AdminMainState, Nothing>() {

    override val container: Container<AdminMainState, Nothing> = container(AdminMainState(loading = true))

    val isFirstLaunchOfDay: Boolean
        get() = dataStoreRepository.isFirstInitOfDay

    init {
        loadData()

        viewModelScope.launch {
            adminRepository.shopName.collect {
                updateShopInfo(it)
            }
        }
    }

    fun updateTheme(themeId: Int) = intent {
        /*themeRepository
            .getThemes()
            .onSuccess { themes ->
                themes
                    .find { it.id == themeId }
                    ?.let { themeRepository.upsertTheme(it) }
            }*/

        hintRepository.saveHints(themeId).onSuccess { updatedAt ->
            reduce {
                state.copy(
                    themes = state.themes.toMutableList()
                        .map { if (it.id == themeId) it.copy(recentUpdated = updatedAt) else it },
                )
            }
        }
    }

    fun start(themeId: Int, readyToStart: () -> Unit) = intent {
        themeRepository.updateLatestTheme(themeId)
        withContext(Dispatchers.Main) {
            readyToStart()
        }
    }

    fun logout() {
        viewModelScope.launch { adminRepository.logout() }
    }

    private fun loadData() = intent {
        reduce { state.copy(loading = true) }
        /*adminRepository.getUserSubscribeStatus().suspendOnSuccess {
            reduce { state.copy(userSubscribeStatus = it) }
            themeRepository.getThemes().onSuccess {
                updateThemes(
                    it.map { themeInfo ->
                        val updatedAt = themeRepository.getUpdatedInfo(themeInfo.id)
                        themeInfo.toPresentation(updatedAt)
                    },
                )
            }
        }*/
        themeRepository.getThemes().onSuccess {
            updateThemes(
                it.map { themeInfo ->
                    val updatedAt = themeRepository.getUpdatedInfo(themeInfo.id)
                    themeInfo.toPresentation(updatedAt)
                },
            )
        }
        reduce { state.copy(loading = false) }
    }

    private fun updateShopInfo(shopName: String) = intent {
        reduce { state.copy(showName = shopName) }
    }

    private fun updateThemes(themes: List<ThemeInfoPresentation>) = intent {
        reduce { state.copy(themes = themes) }
    }
}

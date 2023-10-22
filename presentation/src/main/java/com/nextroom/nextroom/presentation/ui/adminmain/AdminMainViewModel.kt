package com.nextroom.nextroom.presentation.ui.adminmain

import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.model.UserSubscribeStatus
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.AdminRepository
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
) : BaseViewModel<AdminMainState, Nothing>() {

    override val container: Container<AdminMainState, Nothing> = container(
        AdminMainState(
            userSubscribeStatus = UserSubscribeStatus(SubscribeStatus.구독중),
        ),
    )

    init {
        loadData()
    }

    fun updateHints(themeId: Int) = intent {
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
        viewModelScope.launch {
            launch {
                adminRepository.shopName.collect {
                    updateShopInfo(it)
                }
            }
            launch {
                themeRepository.getThemes()
                themeRepository.getLocalThemes().collect { themes ->
                    updateThemes(
                        themes.map { themeInfo ->
                            val updatedAt = themeRepository.getUpdatedInfo(themeInfo.id)
                            themeInfo.toPresentation(updatedAt)
                        },
                    )
                }
            }
        }
    }

    private fun updateShopInfo(shopName: String) = intent {
        reduce { state.copy(showName = shopName) }
    }

    private fun updateThemes(themes: List<ThemeInfoPresentation>) = intent {
        reduce { state.copy(themes = themes) }
    }
}

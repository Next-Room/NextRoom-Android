package com.nextroom.nextroom.presentation.ui.mypage

import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.model.SubscribeItem
import com.nextroom.nextroom.domain.model.UserSubscribe
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MypageViewModel @Inject constructor(
    private val adminRepository: AdminRepository,
) : BaseViewModel<MypageState, MypageEvent>() {
    override val container: Container<MypageState, MypageEvent> = container(MypageState())

    init {
        viewModelScope.launch {
            adminRepository.shopName.collect(::updateShopName)
        }
        fetchSubsInfo()
    }

    fun logout() = intent {
        adminRepository.logout()
    }

    private fun updateShopName(shopName: String) = intent {
        reduce {
            state.copy(shopName = shopName)
        }
    }

    private fun fetchSubsInfo() = intent {
        reduce {
            state.copy(
                userSubscribe = UserSubscribe(
                    id = 0,
                    type = SubscribeItem(id = 0, name = "스타터"),
                    period = "2023.10.5 ~ 2023.11.5",
                ),
            )
        }
    }
}

package com.nextroom.nextroom.presentation.ui.mypage

import com.nextroom.nextroom.domain.model.SubscribeItem
import com.nextroom.nextroom.domain.model.UserSubscribe
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
        fetchShopName()
        fetchSubsInfo()
    }

    fun logout() = intent {
        adminRepository.logout()
    }

    private fun fetchShopName() = intent {
        adminRepository.shopName.collect {
            reduce { state.copy(shopName = it) }
        }
    }

    private fun fetchSubsInfo() = intent {
        adminRepository.getUserSubscribeStatus().onSuccess {
            reduce {
                state.copy(
                    userSubscribeStatus = it,
                    userSubscribe = UserSubscribe(
                        id = 0,
                        type = SubscribeItem(id = 0, name = "스타터"),
                        period = "2023.10.5 ~ 2023.11.5",
                    ),
                )
            }
        }
    }
}

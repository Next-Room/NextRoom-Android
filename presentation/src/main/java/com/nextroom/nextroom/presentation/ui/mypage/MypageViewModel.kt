package com.nextroom.nextroom.presentation.ui.mypage

import com.nextroom.nextroom.domain.model.suspendConcatMap
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
) : BaseViewModel<MypageState, Nothing>() {

    override val container: Container<MypageState, Nothing> = container(MypageState())

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
        /*adminRepository.getUserSubscribeStatus().onSuccess {
            reduce {
                state.copy(
                    userSubscribeStatus = it,
                    userSubscribe = UserSubscribe(
                        type = SubscribeItem(id = 1, name = "미니"),
                    ),
                )
            }
        }*/
        adminRepository.getUserSubscribeStatus().suspendConcatMap(
            other = adminRepository.getUserSubscribe(),
        ) { subsStatus, mypageInfo ->
            reduce {
                state.copy(
                    userSubscribeStatus = subsStatus,
                    userSubscription = mypageInfo,
                )
            }
        }
    }
}

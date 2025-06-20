package com.nextroom.nextroom.presentation.ui

import com.nextroom.nextroom.domain.model.Result
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onFinally
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SubscriptionPromotionViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : BaseViewModel<SubscriptionPromotionState, SubscriptionPromotionEvent>() {


    override val container: Container<SubscriptionPromotionState, SubscriptionPromotionEvent> =
        container(SubscriptionPromotionState())

    init {
        getSubscriptionPlan()
    }

    private fun getSubscriptionPlan() = intent {
        baseViewModelScope.launch {
            reduce { state.copy(loading = true) }
            adminRepository.getSubscriptionPlan()
                .onSuccess {
                    reduce { SubscriptionPromotionState(plan = it) }
                }.onFailure {
                    handleError(it)
                }.onFinally {
                    reduce { state.copy(loading = false) }
                }
        }
    }

    private fun handleError(error: Result.Failure) = intent {
        when (error) {
            is Result.Failure.NetworkError -> postSideEffect(SubscriptionPromotionEvent.NetworkError)
            is Result.Failure.HttpError -> postSideEffect(SubscriptionPromotionEvent.ClientError(error.message))
            else -> postSideEffect(SubscriptionPromotionEvent.UnknownError)
        }
    }
}

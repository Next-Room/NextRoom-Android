package com.nextroom.nextroom.presentation.ui.hint

import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.repository.DataStoreRepository
import com.nextroom.nextroom.domain.repository.TimerRepository
import com.nextroom.nextroom.presentation.base.BaseViewModel
import com.nextroom.nextroom.presentation.model.Hint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HintViewModel @Inject constructor(
    private val timerRepository: TimerRepository,
    private val dataStoreRepository: DataStoreRepository,
) : BaseViewModel<HintState, HintEvent>() {

    override val container: Container<HintState, HintEvent> =
        container(HintState())

    val lastSeconds = timerRepository.lastSeconds

    init {
        baseViewModelScope.launch {
            dataStoreRepository
                .getNetworkDisconnectedCount()
                .let {
                    updateNetworkDisconnectedCount(it)
                }
        }
    }

    private fun updateNetworkDisconnectedCount(count: Int) = intent {
        reduce { state.copy(networkDisconnectedCount = count) }
    }

    fun setHint(hint: Hint) = intent {
        reduce {
            state.copy(hint = hint.copy(answerOpened = state.hint.answerOpened))
        }
    }

    fun setSubscribeStatus(subscribeStatus: SubscribeStatus) = intent {
        reduce { state.copy(userSubscribeStatus = subscribeStatus) }
    }

    fun openAnswer() = intent {
        reduce { state.copy(hint = state.hint.copy(answerOpened = true)) }
    }
}

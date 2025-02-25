package com.nextroom.nextroom.presentation.ui.hint

import androidx.lifecycle.SavedStateHandle
import com.mangbaam.commonutil.DateTimeUtil
import com.nextroom.nextroom.domain.repository.AdminRepository
import com.nextroom.nextroom.domain.repository.DataStoreRepository
import com.nextroom.nextroom.domain.repository.StatisticsRepository
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
    savedStateHandle: SavedStateHandle,
    private val timerRepository: TimerRepository,
    private val statsRepository: StatisticsRepository,
    private val adminRepository: AdminRepository,
    private val dataStoreRepository: DataStoreRepository,
) : BaseViewModel<HintState, HintEvent>() {

    override val container: Container<HintState, HintEvent> =
        container(
            HintState(
                hint = savedStateHandle.get<Hint>("hint") ?: Hint(),
                userSubscribeStatus = HintFragmentArgs.fromSavedStateHandle(savedStateHandle).subscribeStatus,
            )
        )

    private val state: HintState
        get() = container.stateFlow.value

    private val dateTimeUtil: DateTimeUtil by lazy { DateTimeUtil() }

    init {
        baseViewModelScope.launch {
            timerRepository.lastSeconds.collect(::tick)
        }

        baseViewModelScope.launch {
            dataStoreRepository
                .getNetworkDisconnectedCount()
                .let {
                    updateNetworkDisconnectedCount(it)
                }
        }
        /*viewModelScope.launch {
            // 힌트 오픈 시간 통계 집계
            statsRepository.recordHintStats(HintStats(state.hint.id, DateTimeUtil().currentTime() ?: "", ""))
        }*/
    }

    private fun updateNetworkDisconnectedCount(count: Int) = intent {
        reduce { state.copy(networkDisconnectedCount = count) }
    }

    fun openAnswer() = intent {
        reduce { state.copy(hint = state.hint.copy(answerOpened = true)) }
        // 정답 오픈 시간 통계 집계
//        statsRepository.recordAnswerOpenTime(state.hint.id, dateTimeUtil.currentTime() ?: "")
    }

    private fun tick(lastSeconds: Int) = intent {
        reduce { state.copy(lastSeconds = lastSeconds) }
    }
}

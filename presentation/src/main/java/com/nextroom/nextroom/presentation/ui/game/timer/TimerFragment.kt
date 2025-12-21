package com.nextroom.nextroom.presentation.ui.game.timer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.databinding.FragmentTimerBinding
import com.nextroom.nextroom.presentation.ui.game.shared.GameSharedViewModel
import com.nextroom.nextroom.presentation.util.observe
import com.nextroom.nextroom.presentation.util.safeNavigate
import com.nextroom.nextroom.presentation.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimerFragment : Fragment(R.layout.fragment_timer) {

    private val binding by viewBinding(FragmentTimerBinding::bind)

    // GameSharedViewModel을 navGraphViewModels를 사용하여 공유합니다.
    private val sharedViewModel: GameSharedViewModel by navGraphViewModels(R.id.game_navigation)
    private val viewModel: TimerViewModel by navGraphViewModels(R.id.game_navigation) // TimerViewModel도 공유

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TimerState 관찰
        viewLifecycleOwner.observe(viewModel.container.stateFlow) {
            binding.tvElapsedTime.text = "Elapsed Time: ${it.elapsedTime}"
            binding.btnStartPause.text = if (it.isRunning) "Pause" else "Start"
        }

        // GameSharedState 관찰 (예시: 구독 상태 변경에 따라 UI 업데이트)
        viewLifecycleOwner.observe(sharedViewModel.container.stateFlow) {
            // update UI based on sharedState.subscribeStatus if needed
            // For example, enabling/disabling certain UI elements
        }

        binding.btnStartPause.setOnClickListener {
            if (viewModel.container.stateFlow.value.isRunning) {
                viewModel.pauseTimer()
            } else {
                viewModel.startTimer()
            }
        }

        binding.btnReset.setOnClickListener {
            viewModel.resetTimer()
        }

        // HintFragment로 이동
        binding.btnHint.setOnClickListener {
            // TimerViewModel에서 GameSharedViewModel의 힌트 정보를 업데이트하거나, GameSharedViewModel을 직접 사용하여 힌트 정보 전달
            // sharedViewModel.updateHintInfo("User is navigating to hint.") // 예시
            findNavController().safeNavigate(R.id.action_timerFragment_to_hintFragment)
        }
    }
}

package com.nextroom.nextroom.presentation.ui.game.hint

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.databinding.FragmentHintBinding
import com.nextroom.nextroom.presentation.ui.game.shared.GameSharedViewModel
import com.nextroom.nextroom.presentation.util.observe
import com.nextroom.nextroom.presentation.util.safeNavigate
import com.nextroom.nextroom.presentation.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HintFragment : Fragment(R.layout.fragment_hint) {

    private val binding by viewBinding(FragmentHintBinding::bind)

    // GameSharedViewModel을 navGraphViewModels를 사용하여 공유합니다.
    private val sharedViewModel: GameSharedViewModel by navGraphViewModels(R.id.game_navigation)
    private val viewModel: HintViewModel by navGraphViewModels(R.id.game_navigation) // HintViewModel도 공유

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // HintState 관찰
        viewLifecycleOwner.observe(viewModel.container.stateFlow) {
            binding.tvHintText.text = it.hintText
            binding.cbSubscribe.isChecked = it.subscribeStatus
            binding.btnShowHint.isEnabled = it.canShowHint
        }

        // GameSharedState 변화를 직접 관찰하여 UI 업데이트
        // viewModel의 init 블록에서 이미 GameSharedViewModel을 구독하고 있으므로, HintState의 변화를 관찰하는 것으로 충분할 수 있습니다.
        // 만약 GameSharedViewModel의 특정 값에만 직접적으로 반응해야 한다면 아래 코드를 활성화합니다.
        /*
        viewLifecycleOwner.observe(sharedViewModel.container.stateFlow) {
            binding.tvHintText.text = it.hintInfo
            binding.cbSubscribe.isChecked = it.subscribeStatus
            // binding.btnShowHint.isEnabled = ... // Hint 관련 로직에 따라 UI 업데이트
        }
        */

        binding.btnShowHint.setOnClickListener {
            viewModel.fetchHint()
        }

        binding.cbSubscribe.setOnClickListener {
            viewModel.toggleSubscriptionStatus()
        }

        binding.btnBackToTimer.setOnClickListener {
            findNavController().safeNavigate(R.id.action_hintFragment_to_timerFragment)
        }
    }
}

package com.nextroom.nextroom.presentation.ui.hint

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentHintBinding
import com.nextroom.nextroom.presentation.extension.enableFullScreen
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.toTimerFormat
import com.nextroom.nextroom.presentation.extension.updateSystemPadding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.viewmodel.observe
import timber.log.Timber

@AndroidEntryPoint
class HintFragment : BaseFragment<FragmentHintBinding>(FragmentHintBinding::inflate) {

    private val viewModel: HintViewModel by viewModels()
    private val state: HintState
        get() = viewModel.container.stateFlow.value

    private var scrolled: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)
    }

    private fun initViews() = with(binding) {
        enableFullScreen()
        updateSystemPadding(false)

        tbHint.apply {
            tvButton.text = getString(R.string.memo_button)
            tvButton.setOnClickListener {
                val action = HintFragmentDirections.actionGlobalMemoFragment(true)
                findNavController().safeNavigate(action)
            }
            ivBack.setOnClickListener { gotoHome() }
        }

        btnAction.setOnClickListener {
            if (state.hint.answerOpened) {
                gotoHome()
            } else {
                // 정답 보기
                viewModel.openAnswer()
            }
        }
    }

    private fun render(state: HintState) = with(binding) {
        tbHint.tvTitle.text = state.lastSeconds.toTimerFormat()
        groupAnswer.isVisible = state.hint.answerOpened
        btnAction.text = if (!state.hint.answerOpened) {
            getString(R.string.game_hint_button_show_answer)
        } else {
            getString(R.string.game_hint_button_goto_home)
        }

        tvProgress.text = String.format("%d%%", state.hint.progress)
        tvHint.text = state.hint.hint
        if (state.hint.answerOpened) {
            tvAnswer.text = state.hint.answer
            if (!scrolled) {
                scrolled = true

                viewLifecycleOwner.repeatOnStarted {
                    delay(500)
                    svContents.smoothScrollTo(0, tvAnswerLabel.top)
                }
            }
        }
    }

    private fun handleEvent(event: HintEvent) {
        when (event) {
            HintEvent.OpenAnswer -> viewModel.openAnswer()
        }
    }

    private fun gotoHome() {
        Timber.d("gotoHome")
        findNavController().popBackStack(R.id.mainFragment, false)
    }
}

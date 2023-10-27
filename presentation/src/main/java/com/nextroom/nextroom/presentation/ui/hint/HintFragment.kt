package com.nextroom.nextroom.presentation.ui.hint

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentHintBinding
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.toTimerFormat
import com.nextroom.nextroom.presentation.ui.main.GameScreenState
import com.nextroom.nextroom.presentation.ui.main.GameViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.viewmodel.observe
import timber.log.Timber

@AndroidEntryPoint
class HintFragment : BaseFragment<FragmentHintBinding>(FragmentHintBinding::inflate) {

    private val gameViewModel: GameViewModel by activityViewModels()
    private var scrolled: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        gameViewModel.observe(viewLifecycleOwner, state = ::render)
    }

    private fun initViews() = with(binding) {
        tbHint.apply {
            tvButton.text = getString(R.string.memo_button)
            tvButton.setOnClickListener {
                val action = HintFragmentDirections.actionGlobalMemoFragment(true)
                findNavController().safeNavigate(action)
            }
            ivBack.setOnClickListener { gotoHome() }
        }

        btnAction.setOnClickListener {
            val state = gameViewModel.container.stateFlow.value
            if (state.answerOpened) {
                gotoHome()
            } else {
                // 정답 보기
                state.currentHint?.hintId?.let { id -> gameViewModel.openAnswer(id) }
            }
        }
    }

    private fun render(gameScreenState: GameScreenState) = with(binding) {
        // 타이머 렌더링
        tbHint.tvTitle.text = gameScreenState.lastSeconds.toTimerFormat()

        val state = gameScreenState.currentHint ?: return@with
        groupAnswer.isVisible = gameScreenState.answerOpened
        btnAction.text = if (!gameScreenState.answerOpened) {
            getString(R.string.game_hint_button_show_answer)
        } else {
            getString(R.string.game_hint_button_goto_home)
        }

        tvProgress.text = String.format("%d%%", state.progress)
        tvHint.text = state.hint
        if (gameScreenState.answerOpened) {
            tvAnswer.text = state.answer
            if (!scrolled) {
                scrolled = true

                viewLifecycleOwner.repeatOnStarted {
                    delay(500)
                    svContents.smoothScrollTo(0, tvAnswerLabel.top)
                }
            }
        }
    }

    private fun gotoHome() {
        Timber.d("gotoHome")
        val action = HintFragmentDirections.actionHintFragmentToMainFragment()
        findNavController().safeNavigate(action)
    }
}

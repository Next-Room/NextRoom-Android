package com.nextroom.nextroom.presentation.ui.main

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.common.NRDialog
import com.nextroom.nextroom.presentation.databinding.FragmentMainBinding
import com.nextroom.nextroom.presentation.extension.enableFullScreen
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.setOnLongClickListener
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.toTimeUnit
import com.nextroom.nextroom.presentation.extension.vibrator
import com.nextroom.nextroom.presentation.model.InputState
import com.nextroom.nextroom.presentation.ui.memo.PainterViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class GameFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    private lateinit var backCallback: OnBackPressedCallback

    private val viewModel: GameViewModel by viewModels()
    private val painterViewModel: PainterViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
        enableFullScreen()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)
    }

    private fun initViews() = with(binding) {
        tbGame.apply {
            root.setBackgroundColor(resources.getColor(android.R.color.transparent, null))
            tvButton.text = getString(R.string.memo_button)
            tvButton.setOnClickListener {
                val action = GameFragmentDirections.actionGlobalMemoFragment()
                findNavController().safeNavigate(action)
            }
            ivBack.alpha = 0.2F
            ivBack.setOnLongClickListener(1000) {
                showExitDialog()
            }
        }
        initKeypad()
    }

    private fun render(state: GameScreenState) = with(binding) {
        // 타이머 렌더링
        val (hours, minutes, seconds) = state.lastSeconds.toTimeUnit()
        tvHours.text = hours.toString()
        tvMinutes.text = String.format("%02d", minutes)
        tvSeconds.text = String.format("%02d", seconds)
        customTimer.timeLimit = state.totalSeconds
        customTimer.lastSeconds = state.lastSeconds

        // 힌트 렌더링
        tvHintCount.text = String.format(
            "%d/%s",
            state.usedHintsCount,
            if (state.totalHintCount == -1) "∞" else state.totalHintCount.toString(),
        )

        customCodeInput.setCode(state.currentInput)

        when (state.inputState) {
            is InputState.Error -> {
                customCodeInput.setError()
                vibrate()
            }

            else -> {}
        }
    }

    private fun handleEvent(event: GameEvent) {
        when (event) {
            is GameEvent.OnOpenHint -> {
                val action = GameFragmentDirections.actionMainFragmentToHintFragment(event.hint)
                findNavController().safeNavigate(action)
                clearHintCode()
            }

            GameEvent.GameFinish -> snackbar("게임이 종료되었습니다")
        }
    }

    private fun showExitDialog() {
        NRDialog.Builder(requireContext())
            .setTitle(R.string.game_main_exit_dialog)
            .setMessage(R.string.game_main_exit_dialog_message)
            .setPositiveButton(R.string.dialog_yes) { _, _ ->
                painterViewModel.clear()
                viewModel.finishGame { findNavController().popBackStack() }
            }
            .setNegativeButton(R.string.dialog_no) { dialog, _ ->
                dialog.dismiss()
            }.show(childFragmentManager, "ExitGameDialog")
    }

    private fun initKeypad() = with(binding) {
        tvKey1.setOnClickListener { viewModel.inputHintCode(1) }
        tvKey2.setOnClickListener { viewModel.inputHintCode(2) }
        tvKey3.setOnClickListener { viewModel.inputHintCode(3) }
        tvKey4.setOnClickListener { viewModel.inputHintCode(4) }
        tvKey5.setOnClickListener { viewModel.inputHintCode(5) }
        tvKey6.setOnClickListener { viewModel.inputHintCode(6) }
        tvKey7.setOnClickListener { viewModel.inputHintCode(7) }
        tvKey8.setOnClickListener { viewModel.inputHintCode(8) }
        tvKey9.setOnClickListener { viewModel.inputHintCode(9) }
        tvKey0.setOnClickListener { viewModel.inputHintCode(0) }
        keyBackspace.setOnClickListener { viewModel.backspaceHintCode() }
    }

    private fun clearHintCode() {
        viewModel.clearHintCode()
        binding.customCodeInput.setCode("")
    }

    private fun vibrate() {
        requireContext().vibrator.vibrate(longArrayOf(100, 100, 100, 100), -1)
    }

    override fun onDetach() {
        super.onDetach()
        backCallback.remove()
    }
}

package com.nextroom.nextroom.presentation.ui.main

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.common.NRDialog
import com.nextroom.nextroom.presentation.common.NRTwoButtonDialog
import com.nextroom.nextroom.presentation.databinding.FragmentMainBinding
import com.nextroom.nextroom.presentation.extension.disableFullScreen
import com.nextroom.nextroom.presentation.extension.enableFullScreen
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.setOnLongClickListener
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.toTimerFormat
import com.nextroom.nextroom.presentation.extension.updateSystemPadding
import com.nextroom.nextroom.presentation.extension.vibrator
import com.nextroom.nextroom.presentation.model.InputState
import com.nextroom.nextroom.presentation.ui.memo.PainterViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe
import java.util.Locale

@AndroidEntryPoint
class GameFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    private lateinit var backCallback: OnBackPressedCallback

    private val viewModel: GameViewModel by viewModels()
    private val painterViewModel: PainterViewModel by activityViewModels()

    private val gameStartConfirmDialog by lazy {
        NRDialog.Builder(requireContext())
            .setTitle(R.string.dialog_title_game_start_confirm)
            .setMessage(R.string.dialog_message_game_start_confirm)
            .setCancelable(false)
            .setPositiveButton(R.string.start) { _, _ ->
                viewModel.onGameStartClicked()
                dismissStartConfirmDialog()
            }.create()
    }

    private fun dismissStartConfirmDialog() {
        gameStartConfirmDialog.dismiss()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showGameStartConfirmDialog()
        initViews()
        setFragmentResultListeners()
        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)
    }

    private fun showGameStartConfirmDialog() {
        gameStartConfirmDialog.show(parentFragmentManager, "GameStartConfirmDialog")
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(REQUEST_KEY_FINISH_GAME) { _, _ ->
            painterViewModel.clear()
            viewModel.finishGame { findNavController().popBackStack() }
        }
    }

    private fun initViews() = with(binding) {
        enableFullScreen()
        updateSystemPadding(false)

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
        tvTimer.text = state.lastSeconds.toTimerFormat()
        customTimer.timeLimit = state.totalSeconds
        customTimer.lastSeconds = state.lastSeconds

        // 힌트 렌더링
        tvHintCount.text = String.format(
            Locale.getDefault(),
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
            is GameEvent.ClearHintCode -> binding.customCodeInput.setCode("")
            is GameEvent.OnOpenHint -> {
                val action = GameFragmentDirections.actionMainFragmentToHintFragment(event.hint)
                findNavController().safeNavigate(action)
                viewModel.clearHintCode()
            }

            is GameEvent.GameFinish -> snackbar(R.string.game_finished)

            GameEvent.ShowAvailableHintExceedError -> snackbar(message = getString(R.string.game_hint_limit_exceed))
        }
    }

    private fun showExitDialog() {
        GameFragmentDirections
            .actionGlobalNrTwoButtonDialog(
                NRTwoButtonDialog.NRTwoButtonArgument(
                    title = getString(R.string.game_main_exit_dialog),
                    message = getString(R.string.game_main_exit_dialog_message),
                    posBtnText = getString(R.string.dialog_yes),
                    negBtnText = getString(R.string.dialog_no),
                    dialogKey = REQUEST_KEY_FINISH_GAME,
                ),
            )
            .also { findNavController().safeNavigate(it) }
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

    private fun vibrate() {
        requireContext().vibrator.vibrate(longArrayOf(100, 100, 100, 100), -1)
    }

    override fun onDetach() {
        disableFullScreen()
        backCallback.remove()
        super.onDetach()
    }

    override fun onDestroyView() {
        gameStartConfirmDialog.dismiss()
        super.onDestroyView()
    }

    companion object {
        const val REQUEST_KEY_FINISH_GAME = "REQUEST_KEY_FINISH_GAME"
    }
}

package com.nextroom.nextroom.presentation.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.nextroom.nextroom.domain.model.ThemeImageCustomInfo
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
import com.nextroom.nextroom.presentation.util.Logger
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
        initViews()
        initListener()
        setFragmentResultListeners()
        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        binding.pvCustomImage.setOnTouchListener { _, _ -> true }
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(REQUEST_KEY_FINISH_GAME) { _, _ ->
            painterViewModel.clear()
            viewModel.finishGame { findNavController().popBackStack() }
        }
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

    override fun onResume() {
        super.onResume()
        enableFullScreen()
        updateSystemPadding(false)
    }

    private fun setBackground(url: String?, themeImageCustomInfo: ThemeImageCustomInfo?) {
        Glide.with(requireContext())
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    e?.message?.let { Logger.e(e) }

                    try {
                        if (isAdded) {
                            binding.pvCustomImage.isVisible = false
                            binding.ivDefaultImage.isVisible = true
                        }
                    } catch (e: Exception) {
                        Logger.e(e)
                    }

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (!isAdded) return false

                    try {
                        val left = themeImageCustomInfo?.left
                        val top = themeImageCustomInfo?.top
                        val right = themeImageCustomInfo?.right
                        val bottom = themeImageCustomInfo?.bottom

                        if (left != null && top != null && right != null && bottom != null && resource != null) {
                            val matrix = Matrix()
                                .apply {
                                    val fromRectF = RectF(
                                        0f,
                                        0f,
                                        binding.pvCustomImage.width.toFloat(),
                                        binding.pvCustomImage.height.toFloat()
                                    )
                                    val toRectF = RectF(
                                        left,
                                        top,
                                        right,
                                        bottom
                                    )
                                    setRectToRect(
                                        fromRectF,
                                        toRectF,
                                        Matrix.ScaleToFit.FILL
                                    )
                                }

                            // 내부적으로 setImageMatrix를 여러번 호출하면서 최신 matrix정보를 덮어 씀으로 타이밍을 늦춰 마지막에 setImageMatrix를 호출할 수 있도록 post 추가
                            binding.pvCustomImage.post {
                                binding.pvCustomImage.setImageDrawable(resource)
                                binding.pvCustomImage.setSuppMatrix(matrix)
                            }
                        }

                        binding.ivDefaultImage.isVisible = false
                    } catch (e: Exception) {
                        Logger.e(e)
                    }

                    return false
                }

            }).into(binding.pvCustomImage)
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

        if (binding.pvCustomImage.drawable == null && state.themeImageEnabled && !state.themeImageUrl.isNullOrEmpty()) {
            binding.pvCustomImage.isVisible = true
            setBackground(state.themeImageUrl, state.themeImageCustomInfo)
            state.themeImageCustomInfo
                ?.opacity
                ?.let {
                    binding.pvCustomImage.alpha = (it.toFloat() / 100)
                }
        }
    }

    private fun handleEvent(event: GameEvent) {
        when (event) {
            is GameEvent.ClearHintCode -> binding.customCodeInput.setCode("")
            is GameEvent.OnOpenHint -> {
                val action = GameFragmentDirections.actionMainFragmentToHintFragment(event.hint, event.subscribeStatus)
                findNavController().safeNavigate(action)
                viewModel.clearHintCode()
            }

            is GameEvent.GameFinish -> snackbar(R.string.game_finished)

            GameEvent.ShowAvailableHintExceedError -> snackbar(message = getString(R.string.game_hint_limit_exceed))
            GameEvent.NewGame -> {
                gameStartConfirmDialog.show(parentFragmentManager, "GameStartConfirmDialog")
            }
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

    companion object {
        const val REQUEST_KEY_FINISH_GAME = "REQUEST_KEY_FINISH_GAME"
    }
}

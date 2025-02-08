package com.nextroom.nextroom.presentation.ui.background_custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.common.NRTwoButtonDialog
import com.nextroom.nextroom.presentation.databinding.FragmentBackgroundImageCustomDetailBinding
import com.nextroom.nextroom.presentation.extension.dpToPx
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class BackgroundImageCustomDetailFragment : BaseFragment<FragmentBackgroundImageCustomDetailBinding>(FragmentBackgroundImageCustomDetailBinding::inflate) {

    private val viewModel by viewModels<BackgroundImageCustomDetailViewModel>()

    private var rendered = false

    private val dialogKeyEditingExit = DIALOG_KEY_EDITING_EXIT

    private lateinit var backCallback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                checkImageEditing()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }

    override fun onDetach() {
        super.onDetach()
        backCallback.remove()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)
    }

    private fun render(state: BackgroundImageCustomDetailState) {
        binding.layoutToolbar.tvTitle.text = state.themeInfoPresentation.title
        if (!rendered) {
            rendered = true
            Glide.with(requireContext())
                .load(state.themeInfoPresentation.themeImageUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        val left = state.themeInfoPresentation.themeImageCustomInfo?.left
                        val top = state.themeInfoPresentation.themeImageCustomInfo?.top
                        val right = state.themeInfoPresentation.themeImageCustomInfo?.right
                        val bottom = state.themeInfoPresentation.themeImageCustomInfo?.bottom

                        if (left != null && top != null && right != null && bottom != null && resource != null) {
                            val matrix = Matrix()
                                .apply {
                                    val fromRectF = RectF(0f, 0f, binding.imgTheme.width.toFloat(), binding.imgTheme.height.toFloat())
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
                            binding.imgTheme.post {
                                binding.imgTheme.setImageDrawable(resource)
                                binding.imgTheme.setSuppMatrix(matrix)
                            }
                        }

                        return false
                    }

                })
                .into(binding.imgTheme)
        }

        state.themeInfoPresentation.themeImageCustomInfo
            ?.opacity
            ?.let {
                binding.sbOpacity.progress = it
                binding.imgTheme.alpha = (it.toFloat() / 100)
                binding.tvOpacityPercent.text = "$it%"
            }
    }

    private fun handleEvent(event: BackgroundImageCustomDetailEvent) {
        when (event) {
            BackgroundImageCustomDetailEvent.SaveSuccess -> {
                toast(getString(R.string.save_success))
                findNavController().popBackStack()
            }

            BackgroundImageCustomDetailEvent.UnknownError -> snackbar(R.string.error_something)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        binding.layoutToolbar.ivBack.setOnClickListener {
            checkImageEditing()
        }
        binding.llExpandOrCollapse.setOnClickListener {
            hideUsageIntroduceLayout()
        }
        //PhotoView를 추가하면 bottomsheet이 드래그가 작동하지 않음.
        //아래 코드를 넣으면 bottomsheet가 이벤트를 직접 소비함으로써 드래그가 다시 가능
        binding.bottomSheet.setOnTouchListener { _, _ ->
            hideUsageIntroduceLayout()
            true
        }
        binding.imgTheme.setOnPhotoTapListener { _, _, _ ->
            hideUsageIntroduceLayout()
        }
        binding.imgTheme.setOnMatrixChangeListener {
            if (it.left != 0f && it.top != 0f) {
                viewModel.onMatrixChanged(it.left, it.top, it.right, it.bottom)
            }
        }
        binding.imgTheme.setOnViewDragListener { _, _ -> hideUsageIntroduceLayout() }
        binding.layoutToolbar.tvButton.setOnClickListener {
            viewModel.onSaveClicked()
        }
        binding.sbOpacity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.onOpacityChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.scApply.setOnCheckedChangeListener { _, isChecked ->
            binding.layoutGame.root.isVisible = isChecked
        }

        setFragmentResultListener(dialogKeyEditingExit) { _, _ ->
            findNavController().popBackStack()
        }
    }

    private fun checkImageEditing() {
        if (viewModel.isImageEditing()) {
            NavGraphDirections
                .actionGlobalNrTwoButtonDialog(
                    NRTwoButtonDialog.NRTwoButtonArgument(
                        title = getString(R.string.text_image_editing_exit_title),
                        message = getString(R.string.text_image_editing_exit_desc),
                        posBtnText = getString(R.string.text_image_editing_exit_confirm),
                        negBtnText = getString(R.string.text_cancel),
                        dialogKey = dialogKeyEditingExit,
                    ),
                ).also {
                    findNavController().safeNavigate(direction = it)
                }
        } else {
            findNavController().popBackStack()
        }
    }

    private fun initView() {
        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.peekHeight = 52.dpToPx

        binding.layoutToolbar.toolbar.setBackgroundColor(Color.TRANSPARENT)
        binding.layoutToolbar.tvButton.isVisible = true
        binding.layoutToolbar.tvButton.text = getString(R.string.save)
    }

    private fun hideUsageIntroduceLayout() {
        if (binding.llExpandOrCollapse.isVisible) {
            binding.llExpandOrCollapse.visibility = View.INVISIBLE
        }
    }

    companion object {
        private const val DIALOG_KEY_EDITING_EXIT = "DIALOG_KEY_EDITING_EXIT"
    }
}
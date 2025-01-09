package com.nextroom.nextroom.presentation.ui.hint

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.common.ImageAdapter
import com.nextroom.nextroom.presentation.databinding.FragmentHintBinding
import com.nextroom.nextroom.presentation.extension.enableFullScreen
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.snackbar
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

    //timer가 있어서 계속해서 render를 호출함. 이에 있어서 반드시 한번만 불려야 하는 ui를 위해 이 flag가 필요
    private var hintPagerInitialised = false
    private var hintAnswerPagerInitialised = false
    private var hintImageAdapter: ImageAdapter? = null
    private var answerImageAdapter: ImageAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseAnalytics.getInstance(requireContext()).logEvent("screen_view", bundleOf("screen_name" to "hint"))
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
        pbLoading.isVisible = state.loading
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

            if (!hintAnswerPagerInitialised) {
                hintAnswerPagerInitialised = true
                setUpHintAnswerImage(binding, state)
            }
        }

        if (!hintPagerInitialised) {
            hintPagerInitialised = true
            setUpHintImage(binding, state)
        }
    }

    private fun setUpHintImage(binding: FragmentHintBinding, hintState: HintState) = with(binding) {
        when (hintState.userSubscribeStatus) {
            SubscribeStatus.Default -> {
                vpHintImage.isVisible = false
                indicator.isVisible = false
            }

            SubscribeStatus.SUBSCRIPTION_EXPIRATION,
            SubscribeStatus.Subscribed -> {
                vpHintImage.isVisible = hintState.hint.hintImageUrlList.isNotEmpty()
                indicator.isVisible = hintState.hint.hintImageUrlList.isNotEmpty()
            }
        }

        hintImageAdapter = null
        hintImageAdapter = ImageAdapter(
            onImageClicked = {
                if (hintState.userSubscribeStatus == SubscribeStatus.Subscribed) {
                    NavGraphDirections.actionGlobalImageViewerFragment(
                        imageUrlList = hintState.hint.hintImageUrlList.toTypedArray(),
                        position = vpHintImage.currentItem
                    ).also {
                        findNavController().safeNavigate(it)
                    }

                    FirebaseAnalytics.getInstance(requireContext())
                        .logEvent("btn_click", bundleOf("btn_name" to "hint_image"))
                }
            }
        )
        binding.vpHintImage.adapter = hintImageAdapter
        hintState.hint.hintImageUrlList.map { imageUrl ->
            when (hintState.userSubscribeStatus) {
                SubscribeStatus.Default -> ImageAdapter.Image.None
                SubscribeStatus.Subscribed -> {
                    if (hintState.networkDisconnectedCount > NETWORK_DISCONNECT_LIMIT) {
                        ImageAdapter.Image.Drawable(R.drawable.img_placeholder)
                    } else {
                        ImageAdapter.Image.Url(imageUrl)
                    }
                }

                SubscribeStatus.SUBSCRIPTION_EXPIRATION -> ImageAdapter.Image.Drawable(R.drawable.img_placeholder)
            }
        }.also { hintImageAdapter?.setList(it) }

        vpHintImage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                indicator.select(position)
            }
        })
        indicator.withViewPager(vpHintImage)
    }

    private fun setUpHintAnswerImage(binding: FragmentHintBinding, hintState: HintState) = with(binding) {
        when (hintState.userSubscribeStatus) {
            SubscribeStatus.Default -> {
                vpHintAnswerImage.isVisible = false
                indicatorAnswer.isVisible = false
            }

            SubscribeStatus.SUBSCRIPTION_EXPIRATION,
            SubscribeStatus.Subscribed -> {
                vpHintAnswerImage.isVisible = hintState.hint.answerImageUrlList.isNotEmpty()
                indicatorAnswer.isVisible = hintState.hint.answerImageUrlList.isNotEmpty()
            }
        }

        answerImageAdapter = null
        answerImageAdapter = ImageAdapter(
            onImageClicked = {
                if (hintState.userSubscribeStatus == SubscribeStatus.Subscribed) {
                    NavGraphDirections.actionGlobalImageViewerFragment(
                        imageUrlList = hintState.hint.answerImageUrlList.toTypedArray(),
                        position = vpHintAnswerImage.currentItem
                    ).also {
                        findNavController().safeNavigate(it)
                    }

                    FirebaseAnalytics.getInstance(requireContext())
                        .logEvent("btn_click", bundleOf("btn_name" to "answer_image"))
                }
            }
        )
        binding.vpHintAnswerImage.adapter = answerImageAdapter
        hintState.hint.answerImageUrlList.map { imageUrl ->
            when (hintState.userSubscribeStatus) {
                SubscribeStatus.Default -> ImageAdapter.Image.None
                SubscribeStatus.Subscribed -> {
                    if (hintState.networkDisconnectedCount > NETWORK_DISCONNECT_LIMIT) {
                        ImageAdapter.Image.Drawable(R.drawable.img_placeholder)
                    } else {
                        ImageAdapter.Image.Url(imageUrl)
                    }
                }

                SubscribeStatus.SUBSCRIPTION_EXPIRATION -> ImageAdapter.Image.Drawable(R.drawable.img_placeholder)
            }
        }.also { answerImageAdapter?.setList(it) }

        vpHintAnswerImage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                indicatorAnswer.select(position)
            }
        })
        indicatorAnswer.withViewPager(vpHintAnswerImage)
    }

    private fun handleEvent(event: HintEvent) {
        when (event) {
            HintEvent.OpenAnswer -> viewModel.openAnswer()
            is HintEvent.NetworkError -> snackbar(R.string.error_network)
            is HintEvent.UnknownError -> snackbar(R.string.error_something)
            is HintEvent.ClientError -> snackbar(event.message)
        }
    }

    private fun gotoHome() {
        Timber.d("gotoHome")
        findNavController().popBackStack(R.id.gameFragment, false)
    }

    override fun onDestroyView() {
        hintPagerInitialised = false
        hintAnswerPagerInitialised = false
        hintImageAdapter = null
        answerImageAdapter = null
        super.onDestroyView()
    }

    companion object {
        private const val NETWORK_DISCONNECT_LIMIT = 3
    }
}

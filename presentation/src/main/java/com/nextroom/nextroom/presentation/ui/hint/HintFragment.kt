package com.nextroom.nextroom.presentation.ui.hint

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.common.ImageFragment
import com.nextroom.nextroom.presentation.databinding.FragmentHintBinding
import com.nextroom.nextroom.presentation.extension.enableFullScreen
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.toTimerFormat
import com.nextroom.nextroom.presentation.extension.updateSystemPadding
import com.nextroom.nextroom.presentation.model.Hint
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
                setUpHintAnswerImage(binding, state.hint)
            }
        }

        if (!hintPagerInitialised) {
            hintPagerInitialised = true
            setUpHintImage(binding, state.hint)
        }
    }

    private fun setUpHintImage(binding: FragmentHintBinding, hint: Hint) = with(binding) {
        vpHintImage.isVisible = hint.hintImageUrlList.isNotEmpty()
        indicator.isVisible = hint.hintImageUrlList.isNotEmpty()
        vpHintImage.adapter = object : FragmentStateAdapter(requireActivity()) {
            override fun getItemCount(): Int {
                return hint.hintImageUrlList.size
            }

            override fun createFragment(position: Int): Fragment {
                return ImageFragment(
                    imageUrl = hint.hintImageUrlList[position],
                    onImageClicked = {
                        NavGraphDirections.actionGlobalImageViewerFragment(
                            imageUrlList = hint.hintImageUrlList.toTypedArray(),
                            position = position
                        ).also {
                            findNavController().safeNavigate(it)
                        }

                        FirebaseAnalytics.getInstance(requireContext()).logEvent("btn_click", bundleOf("btn_name" to "hint_image"))
                    }
                )
            }
        }
        vpHintImage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                indicator.select(position)
            }
        })
        indicator.withViewPager(vpHintImage)
    }

    private fun setUpHintAnswerImage(binding: FragmentHintBinding, hint: Hint) = with(binding) {
        vpHintAnswerImage.isVisible = hint.answerImageUrlList.isNotEmpty()
        indicatorAnswer.isVisible = hint.answerImageUrlList.isNotEmpty()
        vpHintAnswerImage.adapter = object : FragmentStateAdapter(requireActivity()) {
            override fun getItemCount(): Int {
                return hint.answerImageUrlList.size
            }

            override fun createFragment(position: Int): Fragment {
                return ImageFragment(
                    imageUrl = hint.answerImageUrlList[position],
                    onImageClicked = {
                        NavGraphDirections.actionGlobalImageViewerFragment(
                            imageUrlList = hint.answerImageUrlList.toTypedArray(),
                            position = position
                        ).also {
                            findNavController().safeNavigate(it)
                        }

                        FirebaseAnalytics.getInstance(requireContext()).logEvent("btn_click", bundleOf("btn_name" to "answer_image"))
                    }
                )
            }
        }
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
        }
    }

    private fun gotoHome() {
        Timber.d("gotoHome")
        findNavController().popBackStack(R.id.mainFragment, false)
    }

    override fun onDestroyView() {
        hintPagerInitialised = false
        hintAnswerPagerInitialised = false
        super.onDestroyView()
    }
}

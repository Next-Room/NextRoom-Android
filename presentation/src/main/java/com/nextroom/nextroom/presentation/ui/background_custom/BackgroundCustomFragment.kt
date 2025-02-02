package com.nextroom.nextroom.presentation.ui.background_custom

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentBackgroundCustomBinding
import com.nextroom.nextroom.presentation.databinding.ItemBackgroundCustomInfoBinding
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.toast
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class BackgroundCustomFragment : BaseFragment<FragmentBackgroundCustomBinding>(FragmentBackgroundCustomBinding::inflate) {

    private val viewModel by viewModels<BackgroundCustomViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)
    }

    private fun render(state: BackgroundCustomState) {
        (binding.rvTheme.adapter as? ThemeBackgroundToggleAdapter)?.submitList(state.themes)

        binding.llInfo.removeAllViews()
        mutableListOf(
            getString(R.string.text_background_custom_info_1),
            getString(R.string.text_background_custom_info_2),
        ).apply {
            if (state.userSubscribeStatus == SubscribeStatus.Default
                || state.userSubscribeStatus == SubscribeStatus.SUBSCRIPTION_EXPIRATION
            ) {
                add(getString(R.string.text_background_custom_info_3))
            }
        }.map {
            ItemBackgroundCustomInfoBinding
                .inflate(layoutInflater)
                .apply {
                    tvInfo.text = it
                }
        }.forEach {
            binding.llInfo.addView(it.root)
        }

    }

    private fun handleEvent(event: BackgroundCustomEvent) {
        when (event) {
            is BackgroundCustomEvent.ToggleImageError -> toast(getString(R.string.text_background_setting_error))
            is BackgroundCustomEvent.NetworkError -> snackbar(R.string.error_network)
            is BackgroundCustomEvent.ClientError -> snackbar(event.message)
            BackgroundCustomEvent.UnknownError -> snackbar(R.string.error_something)
            BackgroundCustomEvent.ToggleNotAllowed -> findNavController().safeNavigate(BackgroundCustomFragmentDirections.moveToSubscriptionPayment())
            is BackgroundCustomEvent.ThemeImageClicked -> navToImageCustom(event.theme)
        }
    }

    private fun initViews() {
        binding.toolbar.tvTitle.text = getString(R.string.text_timer_background_custom)
        binding.toolbar.tvButton.isVisible = false
        binding.toolbar.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.rvTheme.adapter = ThemeBackgroundToggleAdapter(
            onToggleClicked = { viewModel.toggleImage(it) },
            onImageClicked = { viewModel.onThemeImageClicked(it) }
        )
    }

    private fun navToImageCustom(theme: ThemeInfoPresentation) {
        //TODO : BackgroundCustomFragment 네이밍을 수정하자 -> BackgroundImageCustomListFragment
        BackgroundCustomFragmentDirections
            .navToBackgroundImageCustomDetailFragment(theme)
            .also { findNavController().safeNavigate(it) }
    }
}
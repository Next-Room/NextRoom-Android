package com.nextroom.nextroom.presentation.ui.mypage

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentMypageBinding
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MypageFragment : BaseFragment<FragmentMypageBinding>(FragmentMypageBinding::inflate) {

    private val viewModel: MypageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initListeners()
        initObserve()
    }

    private fun initViews() = with(binding) {
        tbMypage.apply {
            tvButton.isVisible = false
            tvTitle.text = getString(R.string.mypage_title)
        }
    }

    private fun initListeners() = with(binding) {
        tbMypage.ivBack.setOnClickListener { findNavController().popBackStack() }
        tvLogoutButton.setOnClickListener { viewModel.logout() }
        clSubscribe.setOnClickListener {
            (viewModel.uiState.value as? MypageViewModel.UiState.Loaded)?.let { loaded ->
                when (loaded.status) {
                    SubscribeStatus.Default -> goToPurchase()
                    SubscribeStatus.Subscribed -> goToSubscriptionInfo()
                }
            }
        }
    }

    private fun initObserve() {
        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.uiState.collect { state ->
                    when (state) {
                        MypageViewModel.UiState.Failure -> Unit // TODO JH: 처리 어떻게 할지 결정
                        is MypageViewModel.UiState.Loaded -> {
                            binding.tvShopName.text = state.shopName
                            binding.pbLoading.isVisible = false
                        }

                        MypageViewModel.UiState.Loading -> binding.pbLoading.isVisible = true
                    }
                }
            }
        }
    }

    private fun goToPurchase() {
        val action = MypageFragmentDirections.actionMypageFragmentToPurchaseFragment()
        findNavController().safeNavigate(action)
    }

    private fun goToSubscriptionInfo() {
        val action = MypageFragmentDirections.actionMypageFragmentToSubscriptionFragment()
        findNavController().safeNavigate(action)
    }
}

package com.nextroom.nextroom.presentation.ui.mypage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.common.NRTwoButtonDialog
import com.nextroom.nextroom.presentation.databinding.FragmentMypageBinding
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.toast
import com.nextroom.nextroom.presentation.ui.Constants
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
        setFragmentResultListeners()
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
        tvResignButton.setOnClickListener { showConfirmResignDialog() }
        clSubscribe.setOnClickListener {
            (viewModel.uiState.value as? MypageViewModel.UiState.Loaded)?.let { loaded ->
                when (loaded.status) {
                    SubscribeStatus.SUBSCRIPTION_EXPIRATION,
                    SubscribeStatus.Default -> goToPurchase()

                    SubscribeStatus.Subscribed -> goToSubscriptionInfo()
                }
            }
        }
        clChangeAppPassword.setOnClickListener {
            moveToSetPassword()
        }
        clCustomerService.setOnClickListener {
            try {
                Constants.KAKAO_BUSINESS_CHANNEL_URL
                    .let { url ->
                        Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
                    }.also { startActivity(it) }
            } catch (e: Exception) {
                toast(getString(R.string.error_something))
            }
        }
    }

    private fun initObserve() {
        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.uiState.collect { state ->
                    when (state) {
                        MypageViewModel.UiState.Failure -> snackbar(R.string.error_something)
                        is MypageViewModel.UiState.Loaded -> {
                            binding.tvShopName.text = state.shopName
                            binding.pbLoading.isVisible = false
                            binding.tvAppVersion.text = state.appVersion
                        }

                        MypageViewModel.UiState.Loading -> binding.pbLoading.isVisible = true
                    }
                }
            }
            launch {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        MypageViewModel.UiEvent.ResignFail -> snackbar(R.string.error_something)
                        MypageViewModel.UiEvent.ResignSuccess -> toast(R.string.resign_success_message)
                    }
                }
            }
        }
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(REQUEST_KEY_RESIGN) { _, _ ->
            viewModel.resign()
        }
    }

    private fun goToPurchase() {
        val action = MypageFragmentDirections.moveToPurchaseFragment()
        findNavController().safeNavigate(action)
    }

    private fun goToSubscriptionInfo() {
        val action = MypageFragmentDirections.moveToSubscriptionInfoFragment()
        findNavController().safeNavigate(action)
    }

    private fun showConfirmResignDialog() {
        MypageFragmentDirections
            .moveToNrTwoButtonDialog(
                NRTwoButtonDialog.NRTwoButtonArgument(
                    title = getString(R.string.resign_dialog_title),
                    message = getString(R.string.resign_dialog_message),
                    posBtnText = getString(R.string.resign),
                    negBtnText = getString(R.string.dialog_no),
                    dialogKey = REQUEST_KEY_RESIGN,
                ),
            ).also { findNavController().safeNavigate(it) }
    }

    private fun moveToSetPassword() {
        NavGraphDirections
            .moveToSetPassword()
            .also { findNavController().safeNavigate(it) }
    }

    companion object {
        const val REQUEST_KEY_RESIGN = "REQUEST_KEY_RESIGN"
    }
}

package com.nextroom.nextroom.presentation.ui.mypage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.common.NRTwoButtonDialog
import com.nextroom.nextroom.presentation.common.compose.NRLoading
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MypageFragment : ComposeBaseViewModelFragment<MypageViewModel>() {

    override val screenName: String = "mypage"

    override val viewModel: MypageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsState()
                when (val state = uiState) {
                    is MypageViewModel.UiState.Loaded -> {
                        MypageScreen(
                            state = state,
                            onBackClick = { findNavController().popBackStack() },
                            onSubscribeClick = ::onSubscribeClick,
                            onChangeAppPasswordClick = ::moveToSetPassword,
                            onCustomerServiceClick = ::openCustomerService,
                            onLogoutClick = viewModel::logout,
                            onResignClick = ::showConfirmResignDialog,
                        )
                    }

                    MypageViewModel.UiState.Failure,
                    MypageViewModel.UiState.Loading -> NRLoading(true)
                }
            }
        }
    }

    override fun initObserve() {
        super.initObserve()

        viewLifecycleOwner.repeatOnStarted {
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

    override fun setFragmentResultListeners() {
        setFragmentResultListener(REQUEST_KEY_RESIGN) { _, _ ->
            viewModel.resign()
        }
    }

    private fun onSubscribeClick() {
        val loaded = viewModel.uiState.value as? MypageViewModel.UiState.Loaded ?: return
        when (loaded.status) {
            SubscribeStatus.SUBSCRIPTION_EXPIRATION,
            SubscribeStatus.Default -> goToPurchase()

            SubscribeStatus.Subscribed -> goToSubscriptionInfo()
        }
    }

    private fun openCustomerService() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.link_official_instagram))
            }
            startActivity(intent)
        } catch (e: Exception) {
            toast(getString(R.string.error_something))
        }
    }

    private fun goToPurchase() {
        val action = NavGraphDirections.moveToPurchaseFragment()
        findNavController().safeNavigate(action)
    }

    private fun goToSubscriptionInfo() {
        val action = MypageFragmentDirections.moveToSubscriptionInfoFragment()
        findNavController().safeNavigate(action)
    }

    private fun showConfirmResignDialog() {
        NavGraphDirections
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

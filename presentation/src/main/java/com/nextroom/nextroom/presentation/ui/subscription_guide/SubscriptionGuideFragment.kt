package com.nextroom.nextroom.presentation.ui.subscription_guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.common.NROneButtonDialog
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.ui.billing.BillingEvent
import com.nextroom.nextroom.presentation.ui.billing.BillingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * 미구독 사용자가 게임을 시작하려 할 때 무료 체험 구독을 안내하는 화면
 */
@AndroidEntryPoint
class SubscriptionGuideFragment : ComposeBaseViewModelFragment<SubscriptionGuideViewModel>() {

    override val screenName: String = "subscription_guide"
    override val viewModel: SubscriptionGuideViewModel by viewModels()

    private val billingViewModel: BillingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by viewModel.uiState.collectAsState()
                SubscriptionGuideScreen(
                    state = state,
                    onCloseClick = { findNavController().popBackStack() },
                    onStartFreeTrialClick = ::startFreeTrial,
                )
            }
        }
    }

    override fun initSubscribe() {
        super.initSubscribe()

        viewLifecycleOwner.repeatOnStarted {
            launch {
                billingViewModel.uiEvent.collect { event ->
                    handleBillingEvent(event)
                }
            }
        }
    }

    private fun startFreeTrial() {
        viewModel.onPurchaseStarted()
        // 실패는 BillingEvent.PurchaseFailed로 전달된다.
        billingViewModel.buyPlans(
            productId = viewModel.subscriptionProductId,
            upDowngrade = false,
        )
    }

    private fun handleBillingEvent(event: BillingEvent) {
        when (event) {
            BillingEvent.PurchaseAcknowledged -> moveToPurchaseSuccess()
            BillingEvent.PurchaseCanceled -> viewModel.onPurchaseFinished()
            is BillingEvent.PurchaseFailed -> {
                viewModel.onPurchaseFinished()
                showErrorDialog(
                    event.errorMessage + "\n" +
                        event.purchaseState?.let { getString(R.string.text_error_code, it) }.orEmpty()
                )
            }
        }
    }

    private fun moveToPurchaseSuccess() {
        NavGraphDirections
            .moveToPurchaseSuccess()
            .also {
                findNavController().safeNavigate(
                    direction = it,
                    navOptions = NavOptions.Builder()
                        .setPopUpTo(
                            destinationId = R.id.subscription_guide_fragment,
                            inclusive = true,
                        ).build(),
                )
            }
    }

    private fun showErrorDialog(errorText: String) {
        NavGraphDirections
            .moveToNrOneButtonDialog(
                NROneButtonDialog.NROneButtonArgument(
                    title = getString(R.string.dialog_noti),
                    message = getString(R.string.error_something),
                    btnText = getString(R.string.text_confirm),
                    errorText = errorText,
                )
            ).also { findNavController().safeNavigate(it) }
    }
}

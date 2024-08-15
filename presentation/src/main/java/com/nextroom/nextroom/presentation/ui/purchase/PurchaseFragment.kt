package com.nextroom.nextroom.presentation.ui.purchase

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentPurchaseBinding
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.toast
import com.nextroom.nextroom.presentation.ui.billing.BillingEvent
import com.nextroom.nextroom.presentation.ui.billing.BillingViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class PurchaseFragment : BaseFragment<FragmentPurchaseBinding>(FragmentPurchaseBinding::inflate) {

    private val viewModel: PurchaseViewModel by viewModels()
    private val billingViewModel: BillingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)
        initObserve()
    }

    private fun initViews() = with(binding) {
        tbPurchase.apply {
            tvTitle.text = getString(R.string.purchase_ticket)
            tvButton.isVisible = false
            ivBack.setOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun render(state: PurchaseState) = with(binding) {
        tvMainLabel.text = getString(R.string.purchase_title)
        tvSubLabel.text = getString(R.string.purchase_description)
    }

    private fun handleEvent(event: PurchaseEvent) {
        when (event) {
            is PurchaseEvent.StartPurchase -> {
                billingViewModel.buyPlans(
                    productId = event.productId,
                    tag = event.tag,
                    upDowngrade = event.upDowngrade,
                )
            }
        }
    }

    private fun initObserve() {
        viewLifecycleOwner.repeatOnStarted {
            billingViewModel.uiEvent.collect { event ->
                when (event) {
                    BillingEvent.PurchaseAcknowledged -> {
                        PurchaseFragmentDirections
                            .actionPurchaseFragmentToPurchaseSuccessFragment()
                            .also {
                                findNavController().safeNavigate(it)
                            }
                    }

                    is BillingEvent.PurchaseFailed -> toast(
                        getString(
                            R.string.purchase_error_message,
                            event.purchaseState,
                        ),
                    )
                }
            }
        }
    }
}

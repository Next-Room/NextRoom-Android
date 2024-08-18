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
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.strikeThrow
import com.nextroom.nextroom.presentation.extension.toast
import com.nextroom.nextroom.presentation.ui.billing.BillingEvent
import com.nextroom.nextroom.presentation.ui.billing.BillingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PurchaseFragment : BaseFragment<FragmentPurchaseBinding>(FragmentPurchaseBinding::inflate) {

    private val viewModel: PurchaseViewModel by viewModels()
    private val billingViewModel: BillingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initListeners()
        initObserve()
    }

    private fun initViews() = with(binding) {
        tbPurchase.apply {
            tvTitle.text = getString(R.string.purchase_ticket)
            tvButton.isVisible = false
        }
    }

    private fun initListeners() {
        binding.tbPurchase.ivBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnSubscribe.setOnClickListener {
            (viewModel.uiState.value as? PurchaseViewModel.UiState.Loaded)?.let { loaded ->
                billingViewModel.buyPlans(
                    productId = loaded.id,
                    tag = "",
                    upDowngrade = false,
                )
            }
        }
    }

    private fun initObserve() {
        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.uiState.collect { state ->
                    when (state) {
                        PurchaseViewModel.UiState.Failure -> snackbar(R.string.error_something)
                        is PurchaseViewModel.UiState.Loaded -> {
                            binding.pbLoading.isVisible = false
                            updateUi(state)
                        }

                        PurchaseViewModel.UiState.Loading -> binding.pbLoading.isVisible = true
                    }
                }
            }
            launch {
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

    private fun updateUi(loaded: PurchaseViewModel.UiState.Loaded) {
        with(binding) {
            with(loaded) {
                tvMainLabel.text = description
                tvSubLabel.text = subDescription
                tvName.text = productName
                tvDiscountRate.text = getString(R.string.discount_rate, discountRate)
                tvOriginPrice.text = originPrice
                tvOriginPrice.strikeThrow()
                tvSellPrice.text = sellPrice
            }
        }
    }
}

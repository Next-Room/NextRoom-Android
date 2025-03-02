package com.nextroom.nextroom.presentation.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.databinding.BottomSheetSubscriptionPromotionBinding
import com.nextroom.nextroom.presentation.databinding.ItemBenefitBinding
import com.nextroom.nextroom.presentation.extension.dpToPx
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.toast
import com.nextroom.nextroom.presentation.ui.billing.BillingEvent
import com.nextroom.nextroom.presentation.ui.billing.BillingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.viewmodel.observe
import java.text.NumberFormat


@AndroidEntryPoint
class SubscriptionPromotionBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetSubscriptionPromotionBinding? = null
    private val binding get() = checkNotNull(_binding)

    private val viewModel by viewModels<SubscriptionPromotionViewModel>()
    private val billingViewModel: BillingViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = BottomSheetSubscriptionPromotionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)
        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet =
                (dialog as BottomSheetDialog).findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.from(it).skipCollapsed = true
                BottomSheetBehavior.from(it).isHideable = false
                BottomSheetBehavior.from(it).isDraggable = false
            }
        }

        return bottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listOf(
            Benefit(
                title = getString(R.string.subscribe_beneft_first_title),
                desc = getString(R.string.subscribe_benefit_first_desc),
                subDesc = getString(R.string.subscribe_benefit_first_sub_desc),
                image = R.drawable.benefit
            ),
            Benefit(
                title = getString(R.string.subscribe_beneft_second_title),
                desc = getString(R.string.subscribe_benefit_second_desc),
                subDesc = getString(R.string.subscribe_benefit_second_sub_desc),
                image = R.drawable.benefit1
            ),
        ).map {
            ItemBenefitBinding.inflate(layoutInflater).apply {
                tvTitle.text = it.title
                tvDesc.text = it.desc
                tvDesc1.text = it.subDesc
                ivBenefit.setImageResource(it.image)
            }
        }.forEach {
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply {
                    setMargins(0, 9.dpToPx, 0, 9.dpToPx)
                    height = 108.dpToPx
                }
            binding.llBenefit.addView(it.root, layoutParams)
        }
        binding.acbSubscribe.setOnClickListener {
            viewModel.container.stateFlow.value.plan.plans.firstOrNull()?.let {
                binding.pbLoading.isVisible = true
                billingViewModel.buyPlans(
                    productId = it.subscriptionProductId,
                    tag = "",
                    upDowngrade = false,
                )
            }
        }
        binding.ivClose.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.tvLater.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)

        initObserve()
    }

    private fun initObserve() {
        viewLifecycleOwner.repeatOnStarted {
            launch {
                billingViewModel.uiEvent.collect { event ->
                    when (event) {
                        BillingEvent.PurchaseAcknowledged -> {
                            NavGraphDirections
                                .moveToPurchaseSuccess()
                                .also {
                                    findNavController().navigate(
                                        directions = it,
                                        navOptions = NavOptions.Builder().setPopUpTo(
                                            destinationId = R.id.subscription_payment_fragment,
                                            inclusive = true,
                                        ).build()
                                    )
                                }
                        }

                        is BillingEvent.PurchaseFailed -> {
                            toast(getString(R.string.purchase_error_message, event.purchaseState))
                            binding.pbLoading.isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun render(state: SubscriptionPromotionState) = with(binding) {
        pbLoading.isVisible = state.loading

        state.plan.plans.firstOrNull()
            ?.let {
                acbSubscribe.text = try {
                    getString(
                        R.string.text_subscribe_monthly_payment,
                        NumberFormat.getNumberInstance().format(it.sellPrice)
                    )
                } catch (_: Exception) {
                    getString(R.string.text_subscribe_monthly_payment, it.sellPrice)
                }
            }
    }

    private fun handleEvent(event: SubscriptionPromotionEvent) {
        when (event) {
            is SubscriptionPromotionEvent.NetworkError -> snackbar(R.string.error_network)
            is SubscriptionPromotionEvent.UnknownError -> snackbar(R.string.error_something)
            is SubscriptionPromotionEvent.ClientError -> snackbar(event.message)
        }
    }

    data class Benefit(
        val title: String,
        val desc: String,
        val subDesc: String,
        val image: Int
    )
}
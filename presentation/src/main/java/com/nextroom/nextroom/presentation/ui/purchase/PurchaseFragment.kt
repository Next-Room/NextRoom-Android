package com.nextroom.nextroom.presentation.ui.purchase

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentPurchaseBinding
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class PurchaseFragment : BaseFragment<FragmentPurchaseBinding, PurchaseState, PurchaseEvent>(
    inflate = { inflater, parent -> FragmentPurchaseBinding.inflate(inflater, parent, false) },
) {
    private val viewModel: PurchaseViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)
    }

    private fun initViews() = with(binding) {
        tbPurchase.apply {
            root.isVisible = false
            tvButton.isVisible = false
            ivBack.setOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun render(state: PurchaseState) = with(binding) {
        tbPurchase.root.isVisible = state.subscribeStatus !in listOf(SubscribeStatus.무료체험끝, SubscribeStatus.구독만료)
        tbPurchase.tvTitle.text = when (state.subscribeStatus) {
            SubscribeStatus.무료체험중 -> getString(R.string.purchase_ticket)
            SubscribeStatus.구독중 -> getString(R.string.purchase_change_ticket)
            else -> ""
        }
        val text = when (state.subscribeStatus) {
            SubscribeStatus.None -> "None"
            SubscribeStatus.무료체험중 -> "무료 체험 중"
            SubscribeStatus.무료체험끝 -> "무료 체험 끝"
            SubscribeStatus.유예기간만료 -> "유예기간 만료"
            SubscribeStatus.구독중 -> "구독 중"
            SubscribeStatus.구독만료 -> "구독 만료"
        }
        tvText.text = text
    }

    private fun handleEvent(event: PurchaseEvent) {}
}

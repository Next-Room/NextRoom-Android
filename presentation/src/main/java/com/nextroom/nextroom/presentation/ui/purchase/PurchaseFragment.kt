package com.nextroom.nextroom.presentation.ui.purchase

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.nextroom.nextroom.domain.model.SubscribeStatus
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

        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)
    }

    private fun initViews() = with(binding) {}
    private fun render(state: PurchaseState) = with(binding) {
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

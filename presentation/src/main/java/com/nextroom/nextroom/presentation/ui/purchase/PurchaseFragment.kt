package com.nextroom.nextroom.presentation.ui.purchase

import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.common.LinearSpaceDecoration
import com.nextroom.nextroom.presentation.databinding.FragmentPurchaseBinding
import com.nextroom.nextroom.presentation.extension.dp
import com.nextroom.nextroom.presentation.extension.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class PurchaseFragment : BaseFragment<FragmentPurchaseBinding>(FragmentPurchaseBinding::inflate) {

    private val viewModel: PurchaseViewModel by viewModels()
    private val adapter: TicketAdapter by lazy { TicketAdapter(viewModel::startPurchase) }
    private val spacer: LinearSpaceDecoration = LinearSpaceDecoration(spaceBetween = 12.dp)

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
        rvSubscribes.apply {
            adapter = this@PurchaseFragment.adapter
            addItemDecoration(spacer)
        }
    }

    private fun render(state: PurchaseState) = with(binding) {
        tbPurchase.root.isInvisible = state.subscribeStatus in listOf(SubscribeStatus.무료체험끝, SubscribeStatus.구독만료)
        tbPurchase.tvTitle.text = when (state.subscribeStatus) {
            SubscribeStatus.무료체험중 -> getString(R.string.purchase_ticket)
            SubscribeStatus.구독중 -> getString(R.string.purchase_change_ticket)
            else -> ""
        }
        tvMainLabel.text = when (state.subscribeStatus) {
            SubscribeStatus.무료체험중 -> getString(R.string.purchase_main_label_normal)
            SubscribeStatus.무료체험끝 -> getString(R.string.purchase_main_label_free_end)
            SubscribeStatus.구독중 -> getString(R.string.purchase_main_label_normal)
            SubscribeStatus.구독만료 -> getString(R.string.purchase_main_label_subscribe_end)
            else -> ""
        }
        tvSubLabel.text = when (state.subscribeStatus) {
            SubscribeStatus.무료체험중 -> getString(R.string.purchase_sub_label_normal)
            SubscribeStatus.무료체험끝 -> getString(R.string.purchase_sub_label_free_end)
            SubscribeStatus.구독중 -> getString(R.string.purchase_sub_label_normal)
            SubscribeStatus.구독만료 -> getString(R.string.purchase_sub_label_subscribe_end)
            else -> ""
        }
        adapter.submitList(state.ticketsForUi)
    }

    private fun handleEvent(event: PurchaseEvent) {
        when (event) {
            is PurchaseEvent.StartPurchase -> {
                val action = PurchaseFragmentDirections.actionPurchaseFragmentToPurchaseSuccessFragment()
                findNavController().safeNavigate(action)
            }
        }
    }

    override fun onDestroyView() {
        binding.rvSubscribes.removeItemDecoration(spacer)
        binding.rvSubscribes.adapter = null
        super.onDestroyView()
    }
}

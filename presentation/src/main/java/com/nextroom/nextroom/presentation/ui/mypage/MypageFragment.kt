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
import com.nextroom.nextroom.presentation.extension.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class MypageFragment : BaseFragment<FragmentMypageBinding>(FragmentMypageBinding::inflate) {

    private val viewModel: MypageViewModel by viewModels()
    private val state: MypageState
        get() = viewModel.container.stateFlow.value

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        viewModel.observe(viewLifecycleOwner, state = ::render)
    }

    private fun initViews() = with(binding) {
        tbMypage.apply {
            tvButton.isVisible = false
            tvTitle.text = getString(R.string.mypage_title)
            ivBack.setOnClickListener { findNavController().popBackStack() }
        }
        tvPurchaseTicketButton.setOnClickListener { goToPurchase(state.userSubscribeStatus.subscribeStatus) }
        tvLogoutButton.setOnClickListener { viewModel.logout() }
    }

    private fun render(state: MypageState) = with(binding) {
        pbLoading.isVisible = state.loading
        groupRemoteData.isVisible = !state.loading

        tvShopName.text = state.shopName
        /*tvPurchaseTicketButton.text = if (state.userSubscribeStatus.subscribeStatus == SubscribeStatus.Subscription) {
            getString(R.string.purchase_change_ticket)
        } else {
            getString(R.string.purchase_ticket)
        }*/
        tvSubsName.text = state.userSubscription?.type?.name ?: ""
        tvSubsPeriod.text = state.period
    }

    private fun goToPurchase(subscribeStatus: SubscribeStatus) {
        val action = MypageFragmentDirections.actionMypageFragmentToPurchaseFragment(subscribeStatus)
        findNavController().safeNavigate(action)
    }
}

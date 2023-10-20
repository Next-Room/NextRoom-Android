package com.nextroom.nextroom.presentation.ui.mypage

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentMypageBinding
import com.nextroom.nextroom.presentation.extension.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class MypageFragment : BaseFragment<FragmentMypageBinding, MypageState, MypageEvent>(
    inflate = { inflater, parent -> FragmentMypageBinding.inflate(inflater, parent, false) },
) {
    private val viewModel: MypageViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)
    }

    private fun initViews() = with(binding) {
        tbMypage.apply {
            tvButton.isVisible = false
            tvTitle.text = getString(R.string.mypage_title)
            ivBack.setOnClickListener { findNavController().popBackStack() }
        }
        tvLogoutButton.setOnClickListener { viewModel.logout() }
    }

    private fun render(state: MypageState) = with(binding) {
        tvShopName.text = state.shopName
        state.userSubscribe?.let { subs ->
            tvSubsName.text = subs.type.name
            tvSubsPeriod.text = subs.period
        }
    }

    private fun handleEvent(event: MypageEvent) {
        when (event) {
            MypageEvent.Logout -> findNavController().safeNavigate(MypageFragmentDirections.actionGlobalAdminCodeFragment())
        }
    }
}

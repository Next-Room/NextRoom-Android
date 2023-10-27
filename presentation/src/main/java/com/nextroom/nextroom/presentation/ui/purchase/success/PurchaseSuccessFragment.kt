package com.nextroom.nextroom.presentation.ui.purchase.success

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentPurchaseSuccessBinding
import com.nextroom.nextroom.presentation.extension.safeNavigate

class PurchaseSuccessFragment : BaseFragment<FragmentPurchaseSuccessBinding, Nothing, Nothing>({ inflater, parent ->
    FragmentPurchaseSuccessBinding.inflate(inflater, parent, false)
}) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() = with(binding) {
        btnHome.setOnClickListener { goToHome() }
    }

    private fun goToHome() {
        val action = PurchaseSuccessFragmentDirections.actionToHome()
        findNavController().safeNavigate(action)
    }
}

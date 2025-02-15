package com.nextroom.nextroom.presentation.ui.purchase.success

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentPurchaseSuccessBinding

class PurchaseSuccessFragment : BaseFragment<FragmentPurchaseSuccessBinding>(FragmentPurchaseSuccessBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnHome.setOnClickListener { findNavController().navigateUp() }
    }
}

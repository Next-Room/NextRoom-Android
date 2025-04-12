package com.nextroom.nextroom.presentation.base

import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.NROneButtonDialog
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import kotlinx.coroutines.launch

abstract class BaseViewModelFragment<VB : ViewBinding, VM : NewBaseViewModel>(private val inflate: Inflate<VB>) :
    NewBaseFragment<VB>(inflate) {
    abstract val viewModel: VM

    override fun initObserve() {
        super.initObserve()

        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.errorFlow.collect {
                    NavGraphDirections.moveToNrOneButtonDialog(
                        NROneButtonDialog.NROneButtonArgument(
                            title = getString(R.string.dialog_noti),
                            message = getString(R.string.error_something),
                            btnText = getString(R.string.text_confirm),
                            errorText = it.message,
                        )
                    ).also { findNavController().safeNavigate(it) }
                }
            }
        }
    }
}
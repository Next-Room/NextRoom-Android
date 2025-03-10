package com.nextroom.nextroom.presentation.base

import androidx.viewbinding.ViewBinding
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.toast
import kotlinx.coroutines.launch

abstract class BaseViewModelFragment<VB : ViewBinding, VM : NewBaseViewModel>(private val inflate: Inflate<VB>) :
    NewBaseFragment<VB>(inflate) {
    abstract val viewModel: VM

    override fun initObserve() {
        super.initObserve()

        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.errorFlow.collect {
                    // TODO: OneButtonDialog 제작 후 수정
                    toast(R.string.error_something)
                }
            }
        }
    }
}
package com.nextroom.nextroom.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.nextroom.nextroom.presentation.extension.statusBarHeight

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) :
    Fragment() {

    private var _binding: VB? = null
    val binding: VB
        get() = checkNotNull(_binding) { "binding is null" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    fun setMarginTopStatusBarHeight(view: View) {
        (view.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
            setMargins(
                leftMargin,
                topMargin + view.context.statusBarHeight,
                rightMargin,
                bottomMargin,
            )
        }
        view.requestLayout()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

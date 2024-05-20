package com.nextroom.nextroom.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.nextroom.nextroom.presentation.extension.updateSystemPadding
import com.nextroom.nextroom.presentation.util.FragmentLifecycleLogger
import com.nextroom.nextroom.presentation.util.FragmentLifecycleLoggerImpl

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) :
    Fragment(),
    FragmentLifecycleLogger by FragmentLifecycleLoggerImpl() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerViewLifecycleOwner(this)
        updateSystemPadding()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

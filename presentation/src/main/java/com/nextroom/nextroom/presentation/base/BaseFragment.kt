package com.nextroom.nextroom.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.nextroom.nextroom.presentation.extension.statusBarHeight
import com.nextroom.nextroom.presentation.extension.vibrator

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

    /**
     * [onAttach]에서 호출
     * */
    fun setFullscreen() {
        var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN

        flags = flags or (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )

        requireActivity().window.decorView.systemUiVisibility = flags
    }

    /**
     * [onDetach]에서 호출
     * */
    fun exitFullscreen() {
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
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

    fun vibrate(pattern: LongArray = longArrayOf(100, 100, 100, 100)) {
        requireContext().vibrator.vibrate(pattern, -1)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

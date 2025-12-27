package com.nextroom.nextroom.presentation.base

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.nextroom.nextroom.presentation.extension.updateSystemPadding

abstract class ComposeBaseFragment : Fragment() {
    abstract val screenName: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateSystemPadding()

        initSubscribe()
        initObserve()
        setFragmentResultListeners()
        initViews()
    }

    open fun initSubscribe() {}
    open fun initObserve() {}
    open fun setFragmentResultListeners() {}
    open fun initViews() {}

    override fun onResume() {
        super.onResume()

        FirebaseAnalytics.getInstance(requireContext())
            .logEvent("screen_view", bundleOf("screen_name" to screenName))
    }
}
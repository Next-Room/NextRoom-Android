package com.nextroom.nextroom.presentation.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber

interface FragmentLifecycleLogger : LifecycleEventObserver {
    fun registerViewLifecycleOwner(fragment: Fragment)
    fun registerLifecycleOwner(fragment: Fragment)
}

class FragmentLifecycleLoggerImpl : FragmentLifecycleLogger {
    private var fragment: Fragment? = null
    private var target = Pair(false, false) // Pair(Fragment, View)

    override fun registerViewLifecycleOwner(fragment: Fragment) {
        fragment.viewLifecycleOwner.lifecycle.addObserver(this)
        this.fragment = fragment
        target = target.copy(second = true)
    }

    override fun registerLifecycleOwner(fragment: Fragment) {
        fragment.lifecycle.addObserver(this)
        this.fragment = fragment
        target = target.copy(first = true)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        val message = when (event) {
            Lifecycle.Event.ON_CREATE -> "onCreate"
            Lifecycle.Event.ON_START -> "onStart"
            Lifecycle.Event.ON_RESUME -> "onResume"
            Lifecycle.Event.ON_PAUSE -> "onPause"
            Lifecycle.Event.ON_STOP -> "onStop"
            Lifecycle.Event.ON_DESTROY -> "onDestroy"
            Lifecycle.Event.ON_ANY -> "onAny"
        }
        val targetLog = StringBuilder().apply {
            append("[")
            if (target.first) append("Fragment")
            if (target.first && target.second) append(", ")
            if (target.second) append("View")
            append("]")
        }
        Timber.tag("FragmentLifecycleLogger").d("$targetLog ${fragment?.javaClass?.simpleName}: $message")
    }
}

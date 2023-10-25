package com.nextroom.nextroom.presentation.extension

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.nextroom.nextroom.presentation.common.NRSnackbar

fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), message, duration).show()
}

fun Fragment.toast(@StringRes stringId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), stringId, duration).show()
}

fun Fragment.snackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    NRSnackbar.make(requireView(), message, duration).show()
}

fun Fragment.snackbar(@StringRes messageId: Int, duration: Int = Snackbar.LENGTH_SHORT) {
    NRSnackbar.make(requireView(), messageId, duration).show()
}

/**
 * 현재 프래그먼트를 전체 화면으로 설정한다. [Fragment.onAttach] 에서 호출.
 *
 */
fun Fragment.enableFullScreen() {
    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            viewLifecycleOwnerLiveData.observe(this@enableFullScreen) { viewLifecycleOwner ->
                viewLifecycleOwner?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
                    override fun onStart(owner: LifecycleOwner) {
                        requireActivity().setFullScreen()
                    }

                    override fun onStop(owner: LifecycleOwner) {
                        requireActivity().exitFullScreen()
                    }

                    override fun onDestroy(owner: LifecycleOwner) {
                        viewLifecycleOwner.lifecycle.removeObserver(this)
                    }
                })
            }
        }

        override fun onDestroy(owner: LifecycleOwner) {
            lifecycle.removeObserver(this)
        }
    })
}

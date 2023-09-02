package com.nexters.nextroom.presentation.extension

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.nexters.nextroom.presentation.common.NRSnackbar

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

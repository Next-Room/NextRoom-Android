package com.nextroom.nextroom.presentation.extension

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.google.android.material.snackbar.Snackbar
import com.nextroom.nextroom.presentation.common.NRSnackbar
import com.nextroom.nextroom.presentation.util.WindowInsetsManager

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
 * ## 시스템 영역 확보
 *
 * 상태바와 하단 내비게이션바가 노출되는 경우 [systemBar]를 `true`로 설정하여 컨텐츠와 겹치지 않도록 시스템 영역을 확보한다.
 *
 * 반면 상태바와 하단 내비게이션바가 노출되지 않는 경우 [systemBar]를 `false`로 설정하여 컨텐츠를 표시할 공간을 확보한다.
 *
 * _[systemBar]가 `true`인 경우 `WindowCompat.setDecorFitsSystemWindows(window, true)`와 비슷한 동작을 한다._
 * @param systemBar 상태바와 하단 내비게이션바 영역 확보 여부
 */
fun Fragment.updateSystemPadding(
    systemBar: Boolean = true,
) {
    updateSystemPadding(systemBar, systemBar)
}

/**
 * ## 시스템 영역 확보
 *
 * 상태바나 하단 내비게이션바가 노출되는 경우 [statusBar]나 [navigationBar]를 `true`로 설정하여 컨텐츠와 겹치지 않도록 시스템 영역을 확보한다.
 *
 * 반면 상태바나 하단 내비게이션바가 노출되지 않는 경우 [statusBar]나 [navigationBar]를 `false`로 설정하여 컨텐츠를 표시할 공간을 확보한다.
 *
 * @param statusBar 상태바 영역 확보 여부
 * @param navigationBar 하단 내비게이션바 영역 확보 여부
 */
fun Fragment.updateSystemPadding(
    statusBar: Boolean = true,
    navigationBar: Boolean = true,
) {
    ViewCompat.setOnApplyWindowInsetsListener(requireView()) { view, windowInsets ->
        val typeMask = when {
            statusBar && navigationBar -> WindowInsetsCompat.Type.systemBars()
            statusBar -> WindowInsetsCompat.Type.statusBars()
            navigationBar -> WindowInsetsCompat.Type.navigationBars()
            else -> return@setOnApplyWindowInsetsListener windowInsets
        }
        val insets = windowInsets.getInsets(typeMask)
        view.updatePadding(
            left = insets.left,
            top = insets.top,
            right = insets.right,
            bottom = insets.bottom,
        )
        WindowInsetsCompat.CONSUMED
    }
}

/**
 * 현재 프래그먼트를 전체 화면으로 설정한다.
 *
 */
fun Fragment.enableFullScreen(
    hideStatusBar: Boolean = true,
    hideNavigationBar: Boolean = true,
) {
    viewLifecycleOwner.repeatOn(state = Lifecycle.State.RESUMED) {
        (requireActivity() as? WindowInsetsManager)?.enableFullScreen(
            hideStatusBar = hideStatusBar,
            hideNavigationBar = hideNavigationBar,
        )
    }
}

fun Fragment.disableFullScreen() {
    (requireActivity() as? WindowInsetsManager)?.disableFullScreen()
}

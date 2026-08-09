package com.nextroom.nextroom.presentation.extension

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.nextroom.nextroom.presentation.common.NRSnackbar
import com.nextroom.nextroom.presentation.util.Logger
import com.nextroom.nextroom.presentation.util.WindowInsetsManager

fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    try {
        Toast.makeText(requireContext(), message, duration).show()
    } catch (e: Exception) {
        Logger.e(e)
    }
}

fun Fragment.toast(@StringRes stringId: Int, duration: Int = Toast.LENGTH_SHORT) {
    try {
        Toast.makeText(requireContext(), stringId, duration).show()
    } catch (e: Exception) {
        Logger.e(e)
    }
}

fun Fragment.snackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    try {
        NRSnackbar.make(requireView(), message, duration).show()
    } catch (e: Exception) {
        Logger.e(e)
    }
}

fun Fragment.snackbar(@StringRes messageId: Int, duration: Int = Snackbar.LENGTH_SHORT) {
    try {
        NRSnackbar.make(requireView(), messageId, duration).show()
    } catch (e: Exception) {
        Logger.e(e)
    }
}

/**
 * ## 시스템 영역 확보
 *
 * 상태바와 하단 내비게이션바, 키보드가 노출되는 경우 [systemBar]를 `true`로 설정하여 컨텐츠와 겹치지 않도록 시스템 영역을 확보한다.
 *
 * 반면 상태바와 하단 내비게이션바가 노출되지 않는 경우 [systemBar]를 `false`로 설정하여 컨텐츠를 표시할 공간을 확보한다.
 *
 * _[systemBar]가 `true`인 경우 `WindowCompat.setDecorFitsSystemWindows(window, true)`와 비슷한 동작을 한다._
 * @param systemBar 상태바와 하단 내비게이션바, 키보드 영역 확보 여부
 */
fun Fragment.updateSystemPadding(
    systemBar: Boolean = true,
) {
    updateSystemPadding(statusBar = systemBar, navigationBar = systemBar, ime = systemBar)
}

/**
 * ## 시스템 영역 확보
 *
 * 상태바나 하단 내비게이션바, 키보드가 노출되는 경우 [statusBar]나 [navigationBar], [ime]를 `true`로 설정하여
 * 컨텐츠와 겹치지 않도록 시스템 영역을 확보한다.
 *
 * 반면 노출되지 않는 경우 `false`로 설정하여 컨텐츠를 표시할 공간을 확보한다.
 *
 * edge-to-edge 환경에서는 `android:windowSoftInputMode="adjustResize"`가 더 이상 윈도우 크기를 줄이지 않으므로,
 * 키보드에 컨텐츠가 가려지지 않으려면 [ime] 인셋을 직접 반영해야 한다.
 *
 * @param statusBar 상태바(디스플레이 컷아웃 포함) 영역 확보 여부
 * @param navigationBar 하단 내비게이션바 영역 확보 여부
 * @param ime 키보드 영역 확보 여부
 */
fun Fragment.updateSystemPadding(
    statusBar: Boolean = true,
    navigationBar: Boolean = true,
    ime: Boolean = true,
) {
    ViewCompat.setOnApplyWindowInsetsListener(requireView()) { view, windowInsets ->
        var typeMask = 0
        if (statusBar) {
            typeMask = typeMask or WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.displayCutout()
        }
        if (navigationBar) typeMask = typeMask or WindowInsetsCompat.Type.navigationBars()
        if (ime) typeMask = typeMask or WindowInsetsCompat.Type.ime()

        if (typeMask == 0) return@setOnApplyWindowInsetsListener windowInsets

        // 여러 타입을 함께 조회하면 각 방향별로 가장 큰 인셋이 반환된다.
        // 즉 키보드가 올라온 경우 하단 패딩은 max(내비게이션바, 키보드)가 된다.
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

/**
 * AssistedInject를 사용하는 ViewModel을 쉽게 생성하기 위한 확장 함수
 *
 * ## 사용 예시
 * ```kotlin
 * @Inject
 * lateinit var viewModelFactory: HintViewModel.Factory
 *
 * private val gameSharedViewModel: GameSharedViewModel by hiltNavGraphViewModels(R.id.game_navigation)
 *
 * override val viewModel: HintViewModel by assistedViewModel {
 *     viewModelFactory.create(gameSharedViewModel)
 * }
 * ```
 *
 * @param factory ViewModel을 생성하는 람다 함수
 * @return ViewModel의 Lazy 인스턴스
 */
inline fun <reified VM : ViewModel> Fragment.assistedViewModel(
    crossinline factory: () -> VM
): Lazy<VM> {
    return viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return factory() as T
            }
        }
    }
}

package com.nextroom.nextroom.presentation.ui.theme_select

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.nextroom.nextroom.domain.repository.StatisticsRepository
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.common.NRTwoButtonDialog
import com.nextroom.nextroom.presentation.extension.getResultData
import com.nextroom.nextroom.presentation.extension.hasResultData
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.toast
import com.nextroom.nextroom.presentation.util.Logger
import com.nextroom.nextroom.presentation.util.isOnline
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ThemeSelectFragment : ComposeBaseViewModelFragment<ThemeSelectViewModel>() {

    override val screenName: String = "theme_select"
    override val viewModel: ThemeSelectViewModel by viewModels()

    private lateinit var backCallback: OnBackPressedCallback

    @Inject
    lateinit var statisticsRepository: StatisticsRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by viewModel.uiState.collectAsState()
                ThemeSelectScreen(
                    state = state,
                    onRefresh = viewModel::loadData,
                    onBannerClick = { banner ->
                        navByDeepLink(banner.linkUrl)
                        FirebaseAnalytics.getInstance(requireContext())
                            .logEvent("btn_click", bundleOf("btn_name" to "banner"))
                    },
                    onPurchaseClick = ::goToPurchase,
                    onMyPageClick = ::goToMyPage,
                    onManageThemesClick = viewModel::onManageThemesClicked,
                    onBackgroundSettingClick = ::goToBackgroundSetting,
                    onThemeRefreshClick = viewModel::onThemeRefreshClicked,
                    onThemeClick = { themeId ->
                        viewModel.onThemeClicked(themeId.toString())
                    },
                )
            }
        }
    }

    override fun initSubscribe() {
        super.initSubscribe()

        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.uiEvent.collect { event ->
                    handleEvent(event)
                }
            }
        }
    }

    override fun setFragmentResultListeners() {
        setFragmentResultListener(requestKeyCheckPassword, ::handleFragmentResults)
        setFragmentResultListener(requestKeyCheckPasswordForManageThemes, ::handleFragmentResults)
        setFragmentResultListener(dialogKeyNeedToSetPassword, ::handleFragmentResults)
        setFragmentResultListener(dialogKeyNeedSubscriptionForGameStart, ::handleFragmentResults)
        setFragmentResultListener(SHOW_USAGE_GUIDE_DIALOG_KEY, ::handleFragmentResults)
    }

    private fun handleFragmentResults(requestKey: String, bundle: Bundle) {
        when (requestKey) {
            requestKeyCheckPassword -> {
                try {
                    if (bundle.hasResultData()) {
                        bundle.getResultData()?.let { themeId ->
                            viewModel.tryGameStart(themeId.toInt())
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                    snackbar(R.string.error_something)
                }
            }

            requestKeyCheckPasswordForManageThemes -> {
                if (bundle.hasResultData()) {
                    findNavController().safeNavigate(
                        ThemeSelectFragmentDirections.moveToThemeManageFragment()
                    )
                }
            }

            dialogKeyNeedToSetPassword -> moveToSetPassword()
            dialogKeyNeedSubscriptionForGameStart -> goToPurchase()
            SHOW_USAGE_GUIDE_DIALOG_KEY -> {
                try {
                    getString(R.string.link_usage_guide).let { url ->
                        Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) }
                    }.also {
                        startActivity(it)
                    }
                } catch (e: Exception) {
                    Logger.e(e)
                    toast(getString(R.string.error_something))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
        if (isOnline(context ?: return).not()) {
            viewModel.incrementNetworkDisconnectedCount()
        }
    }

    private fun handleEvent(event: ThemeSelectEvent) {
        when (event) {
            is ThemeSelectEvent.NetworkError -> snackbar(R.string.error_network)
            is ThemeSelectEvent.UnknownError -> snackbar(R.string.error_something)
            is ThemeSelectEvent.ClientError -> snackbar(event.message)
            ThemeSelectEvent.InAppReview -> showInAppReview()
            is ThemeSelectEvent.ReadyToGameStart -> moveToGameStart(event.subscribeStatus)
            ThemeSelectEvent.NeedSubscriptionForGameStart -> showNeedSubscriptionDialog()
            ThemeSelectEvent.NeedToSetPassword -> showNeedToSetPasswordDialog()
            is ThemeSelectEvent.NeedToCheckPasswordForStartGame -> moveToCheckPasswordForGameStart(event.themeId)
            ThemeSelectEvent.NeedToCheckPasswordForManageThemes -> moveToCheckPasswordForManageThemes()
            ThemeSelectEvent.RecommendBackgroundCustom -> showRecommendBackgroundCustomBottomSheet()
            ThemeSelectEvent.GuidePopupNotSeen -> showSuggestGuidePopup()
        }
    }

    private fun showRecommendBackgroundCustomBottomSheet() {
        findNavController().safeNavigate(ThemeSelectFragmentDirections.moveToRecommendBackgroundCustom())
    }

    private fun showInAppReview() {
        val manager = ReviewManagerFactory.create(context ?: return)
        manager
            .requestReviewFlow()
            .addOnCompleteListener { request ->
                try {
                    if (request.isSuccessful) {
                        manager.launchReviewFlow(
                            activity ?: return@addOnCompleteListener,
                            request.result
                        )
                    }
                } catch (ex: Exception) {
                    Timber.e(ex)
                }
            }
    }

    private fun goToPurchase() {
        val action = NavGraphDirections.moveToPurchaseFragment()
        findNavController().safeNavigate(action)
    }

    private fun goToMyPage() {
        val action = ThemeSelectFragmentDirections.moveToMypage()
        findNavController().safeNavigate(action)
    }

    private fun goToBackgroundSetting() {
        val state = viewModel.uiState.value
        findNavController().safeNavigate(
            ThemeSelectFragmentDirections.moveToBackgroundCustomFragment(
                state.subscribeStatus,
                state.themes.toTypedArray()
            )
        )
    }

    private fun moveToGameStart(subscribeStatus: com.nextroom.nextroom.domain.model.SubscribeStatus) {
        NavGraphDirections
            .moveToTimerFragment(subscribeStatus)
            .also { findNavController().safeNavigate(it) }
    }

    private fun showNeedToSetPasswordDialog() {
        NavGraphDirections
            .moveToNrTwoButtonDialog(
                NRTwoButtonDialog.NRTwoButtonArgument(
                    title = getString(R.string.text_need_to_set_password_title),
                    message = getString(R.string.text_need_to_set_password_message),
                    posBtnText = getString(R.string.text_move_to_setting),
                    negBtnText = getString(R.string.dialog_close),
                    dialogKey = dialogKeyNeedToSetPassword,
                ),
            ).also {
                findNavController().safeNavigate(
                    direction = it,
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            }
    }

    private fun showNeedSubscriptionDialog() {
        NavGraphDirections
            .moveToNrTwoButtonDialog(
                NRTwoButtonDialog.NRTwoButtonArgument(
                    title = getString(R.string.text_need_subscription_for_game_start_title),
                    message = getString(R.string.text_need_subscription_for_game_start_message),
                    posBtnText = getString(R.string.dialog_subscribe_button),
                    negBtnText = getString(R.string.dialog_close),
                    dialogKey = dialogKeyNeedSubscriptionForGameStart,
                ),
            ).also {
                findNavController().safeNavigate(
                    direction = it,
                    navOptions = NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build()
                )
            }
    }

    private fun moveToSetPassword() {
        NavGraphDirections
            .moveToSetPassword()
            .also { findNavController().safeNavigate(it) }
    }

    private fun moveToCheckPasswordForGameStart(themeId: String) {
        NavGraphDirections
            .moveToCheckPassword(requestKey = requestKeyCheckPassword, resultData = themeId)
            .also { findNavController().safeNavigate(it) }
    }

    private fun moveToCheckPasswordForManageThemes() {
        NavGraphDirections
            .moveToCheckPassword(
                requestKey = requestKeyCheckPasswordForManageThemes,
                resultData = ""
            )
            .also { findNavController().safeNavigate(it) }
    }

    private fun showSuggestGuidePopup() {
        NavGraphDirections.moveToNrTwoButtonDialog(
            NRTwoButtonDialog.NRTwoButtonArgument(
                title = getString(R.string.text_suggest_guide_popup_title),
                message = getString(R.string.text_suggest_guide_popup_message),
                negBtnText = getString(R.string.text_cancel),
                posBtnText = getString(R.string.text_see_guide),
                dialogKey = SHOW_USAGE_GUIDE_DIALOG_KEY,
            )
        ).also { findNavController().safeNavigate(it) }
    }

    private fun navByDeepLink(deeplinkUrl: String) {
        try {
            Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(deeplinkUrl))
                .let {
                    startActivity(it)
                }
        } catch (ex: Exception) {
            toast(R.string.text_no_available_browser_guide)
            Timber.e(ex)
        }
    }

    override fun onDetach() {
        super.onDetach()
        backCallback.remove()
    }

    companion object {
        private const val requestKeyCheckPassword = "requestKeyCheckPassword"
        private const val requestKeyCheckPasswordForManageThemes =
            "requestKeyCheckPasswordForManageThemes"
        private const val dialogKeyNeedToSetPassword = "dialogKeyNeedToSetPassword"
        private const val dialogKeyNeedSubscriptionForGameStart =
            "dialogKeyNeedSubscriptionForGameStart"
        private const val SHOW_USAGE_GUIDE_DIALOG_KEY = "SHOW_USAGE_GUIDE_DIALOG_KEY"
    }
}

package com.nextroom.nextroom.presentation.ui.theme_select

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.mangbaam.commonutil.DateTimeUtil
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.repository.StatisticsRepository
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.common.NRLoading
import com.nextroom.nextroom.presentation.common.NRTwoButtonDialog
import com.nextroom.nextroom.presentation.databinding.FragmentThemeSelectBinding
import com.nextroom.nextroom.presentation.extension.addMargin
import com.nextroom.nextroom.presentation.extension.getResultData
import com.nextroom.nextroom.presentation.extension.hasResultData
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.setCurrentItemWithDuration
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.statusBarHeight
import com.nextroom.nextroom.presentation.extension.toast
import com.nextroom.nextroom.presentation.extension.updateSystemPadding
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation
import com.nextroom.nextroom.presentation.util.isOnline
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.viewmodel.observe
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ThemeSelectFragment :
    BaseFragment<FragmentThemeSelectBinding>(FragmentThemeSelectBinding::inflate) {

    private lateinit var backCallback: OnBackPressedCallback
    private var job: Job? = null
    private var pageChangeCallback: ViewPager2.OnPageChangeCallback? = null

    @Inject
    lateinit var statisticsRepository: StatisticsRepository

    private val viewModel: ThemeSelectViewModel by viewModels()
    private val adapter: ThemesAdapter by lazy {
        ThemesAdapter(
            onThemeClicked = { themeId -> viewModel.onThemeClicked(themeId.toString()) }
        )
    }
    private val state: ThemeSelectState
        get() = viewModel.container.stateFlow.value

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initSubscribe()
        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)
    }

    private fun initSubscribe() {
        setFragmentResultListener(requestKeyCheckPassword, ::handleFragmentResults)
        setFragmentResultListener(dialogKeyNeedToSetPassword, ::handleFragmentResults)
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

            dialogKeyNeedToSetPassword -> moveToSetPassword()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
        if (isOnline(context ?: return).not()) {
            viewModel.incrementNetworkDisconnectedCount()
        }
    }

    /*override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            // 통계 정보 서버에 전송
            statisticsRepository.postGameStats()
        }
    }*/

    private fun initViews() = with(binding) {
        updateSystemPadding(statusBar = false, navigationBar = true)

        ivMyButton.addMargin(top = requireContext().statusBarHeight)

        rvThemes.adapter = adapter

        tvPurchaseButton.setOnClickListener {
            goToPurchase()
        }
        ivMyButton.setOnClickListener {
            goToMyPage()
        }

        srlTheme.setOnRefreshListener {
            viewModel.loadData()
        }

        vpBanner.adapter = BannerAdapter {
            navByDeepLink(it.linkUrl)
            FirebaseAnalytics.getInstance(requireContext()).logEvent("btn_click", bundleOf("btn_name" to "banner"))
        }

        tvBacgroundSetting.setOnClickListener {
            findNavController().safeNavigate(
                ThemeSelectFragmentDirections.moveToBackgroundCustomFragment(
                    state.subscribeStatus,
                    state.themes.toTypedArray()
                )
            )
        }
        ivRefresh.setOnClickListener {
            viewModel.onThemeRefreshClicked()
        }

        pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (state) {
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        try {
                            setCurrentBannerPosition(binding.vpBanner.currentItem)
                            if (job?.isActive == false) startAutoScroll()
                        } catch (e: Exception) {
                            // do nothing
                        }
                    }

                    ViewPager2.SCROLL_STATE_DRAGGING -> job?.cancel()
                    ViewPager2.SCROLL_STATE_SETTLING -> Unit
                }
            }
        }
        pageChangeCallback?.let { binding.vpBanner.registerOnPageChangeCallback(it) }
    }

    private fun startAutoScroll() {
        try {
            job = lifecycleScope.launch {
                while (true) {
                    delay(AUTO_SCROLL_INTERVAL_TIME)
                    setCurrentBannerPosition(viewModel.getCurrentBannerPosition() + 1)
                }
            }
        } catch (e: Exception) {
            job?.cancel()
        }
    }

    private fun setCurrentBannerPosition(currentPosition: Int) {
        (binding.vpBanner.adapter as? BannerAdapter)
            ?.itemCount
            ?.let { currentPosition % it }
            ?.let { viewModel.setCurrentBannerPosition(it) }
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

    private fun render(state: ThemeSelectState) = with(binding) {
        if (state.banners.isEmpty()) {
            vpBanner.isVisible = false
        } else {
            vpBanner.isVisible = true
            (vpBanner.adapter as? BannerAdapter)?.submitList(state.banners)
        }

        tvPurchaseButton.isVisible = state.subscribeStatus != SubscribeStatus.Subscribed
        srlTheme.isRefreshing = false
        tvShopName.text = state.shopName
        llEmptyThemeGuide.isVisible = state.themes.isEmpty()
        adapter.submitList(state.themes.map { it.toAdapterUI() })
        tvThemeCount.text = state.themes.size.toString()
        tvLastUpdate.text = getString(
            R.string.text_last_hint_update,
            DateTimeUtil().longToDateString(System.currentTimeMillis(), pattern = "yyyy.MM.dd HH:mm")
        )

        if (state.opaqueLoading) {
            NRLoading.BackgroundType.BLACK
        } else {
            NRLoading.BackgroundType.TRANSPARENT
        }.also { binding.nrLoading.setBackgroundType(it) }
        binding.nrLoading.isVisible = state.opaqueLoading || state.loading
        binding.vpBanner.setCurrentItemWithDuration(state.currentBannerPosition, PAGING_ANIMATION_DURATION)

        if (job == null || job?.isActive == false) {
            startAutoScroll()
        }
    }

    private fun handleEvent(event: ThemeSelectEvent) {
        when (event) {
            is ThemeSelectEvent.NetworkError -> snackbar(R.string.error_network)
            is ThemeSelectEvent.UnknownError -> snackbar(R.string.error_something)
            is ThemeSelectEvent.ClientError -> snackbar(event.message)
            ThemeSelectEvent.InAppReview -> showInAppReview()
            is ThemeSelectEvent.ReadyToGameStart -> moveToGameStart(event.subscribeStatus)
            ThemeSelectEvent.NeedToSetPassword -> showNeedToSetPasswordDialog()
            is ThemeSelectEvent.NeedToCheckPasswordForStartGame -> moveToCheckPasswordForGameStart(event.themeId)
            ThemeSelectEvent.RecommendBackgroundCustom -> showRecommendBackgroundCustomBottomSheet()
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

    private fun goToLink(linkUrl: String) {
        findNavController().safeNavigate(ThemeSelectFragmentDirections.moveToWebview(url = linkUrl))
    }

    private fun goToPurchase() {
        val action = ThemeSelectFragmentDirections.moveToPurchaseFragment()
        findNavController().safeNavigate(action)
    }

    private fun goToMyPage() {
        val action = ThemeSelectFragmentDirections.moveToMypageFragment()
        findNavController().safeNavigate(action)
    }

    private fun moveToGameStart(subscribeStatus: SubscribeStatus) {
        NavGraphDirections
            .actionGlobalTimerFragment(subscribeStatus)
            .also { findNavController().safeNavigate(it) }
    }

    private fun showNeedToSetPasswordDialog() {
        NavGraphDirections
            .actionGlobalNrTwoButtonDialog(
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

    private fun ThemeInfoPresentation.toAdapterUI(): ThemesAdapter.ThemeInfo {
        return ThemesAdapter.ThemeInfo(
            id = this.id,
            title = this.title,
            imageUrl = this.themeImageUrl,
        )
    }

    override fun onPause() {
        job?.cancel()
        super.onPause()
    }

    override fun onDestroyView() {
        binding.rvThemes.adapter = null
        binding.vpBanner.adapter = null
        pageChangeCallback?.let { binding.vpBanner.unregisterOnPageChangeCallback(it) }
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        backCallback.remove()
    }

    companion object {
        private const val requestKeyCheckPassword = "requestKeyCheckPassword"
        private const val dialogKeyNeedToSetPassword = "dialogKeyNeedToSetPassword"
        private const val AUTO_SCROLL_INTERVAL_TIME = 3500L
        private const val PAGING_ANIMATION_DURATION = 400L
    }
}

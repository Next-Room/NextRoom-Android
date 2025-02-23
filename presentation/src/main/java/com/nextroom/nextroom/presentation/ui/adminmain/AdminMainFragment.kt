package com.nextroom.nextroom.presentation.ui.adminmain

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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
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
import com.nextroom.nextroom.presentation.databinding.FragmentAdminMainBinding
import com.nextroom.nextroom.presentation.extension.addMargin
import com.nextroom.nextroom.presentation.extension.getResultData
import com.nextroom.nextroom.presentation.extension.hasResultData
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.statusBarHeight
import com.nextroom.nextroom.presentation.extension.updateSystemPadding
import com.nextroom.nextroom.presentation.util.isOnline
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AdminMainFragment :
    BaseFragment<FragmentAdminMainBinding>(FragmentAdminMainBinding::inflate) {

    private lateinit var backCallback: OnBackPressedCallback

    @Inject
    lateinit var statisticsRepository: StatisticsRepository

    private val viewModel: AdminMainViewModel by viewModels()
    private val adapter: ThemesAdapter by lazy {
        ThemesAdapter(
            onThemeClicked = { themeId -> viewModel.onThemeClicked(themeId.toString()) }
        )
    }
    private val state: AdminMainState
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

        rvBanner.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvBanner.adapter = BannerAdapter {
            navByDeepLink(it.linkUrl)
            FirebaseAnalytics.getInstance(requireContext()).logEvent("btn_click", bundleOf("btn_name" to "banner"))
        }
        PagerSnapHelper().attachToRecyclerView(rvBanner)

        tvBacgroundSetting.setOnClickListener {
            findNavController().safeNavigate(
                AdminMainFragmentDirections.moveToBackgroundCustomFragment(
                    state.subscribeStatus,
                    state.themes.toTypedArray()
                )
            )
        }
        ivRefresh.setOnClickListener {
            viewModel.onThemeRefreshClicked()
        }
    }

    private fun navByDeepLink(deeplinkUrl: String) {
        try {
            Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(deeplinkUrl))
                .let {
                    startActivity(it)
                }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
    }

    private fun render(state: AdminMainState) = with(binding) {
        if (state.banners.isEmpty()) {
            rvBanner.isVisible = false
        } else {
            rvBanner.isVisible = true
            (rvBanner.adapter as? BannerAdapter)?.submitList(state.banners)
        }

        tvPurchaseButton.isVisible = state.subscribeStatus != SubscribeStatus.Subscribed
        srlTheme.isRefreshing = false
        tvShopName.text = state.shopName
        llEmptyThemeGuide.isVisible = state.themes.isEmpty()
        adapter.submitList(state.themes)
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
    }

    private fun handleEvent(event: AdminMainEvent) {
        when (event) {
            is AdminMainEvent.NetworkError -> snackbar(R.string.error_network)
            is AdminMainEvent.UnknownError -> snackbar(R.string.error_something)
            is AdminMainEvent.ClientError -> snackbar(event.message)
            AdminMainEvent.InAppReview -> showInAppReview()
            is AdminMainEvent.ReadyToGameStart -> moveToGameStart(event.subscribeStatus)
            AdminMainEvent.NeedToSetPassword -> showNeedToSetPasswordDialog()
            is AdminMainEvent.NeedToCheckPasswordForStartGame -> moveToCheckPasswordForGameStart(event.themeId)
            AdminMainEvent.RecommendBackgroundCustom -> showRecommendBackgroundCustomBottomSheet()
        }
    }

    private fun showRecommendBackgroundCustomBottomSheet() {
        findNavController().safeNavigate(AdminMainFragmentDirections.moveToRecommendBackgroundCustom())
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
        findNavController().safeNavigate(AdminMainFragmentDirections.actionAdminToWebview(url = linkUrl))
    }

    private fun goToPurchase() {
        val action = AdminMainFragmentDirections.actionAdminMainFragmentToPurchaseFragment()
        findNavController().safeNavigate(action)
    }

    private fun goToMyPage() {
        val action = AdminMainFragmentDirections.actionAdminMainFragmentToMypageFragment()
        findNavController().safeNavigate(action)
    }

    private fun moveToGameStart(subscribeStatus: SubscribeStatus) {
        NavGraphDirections
            .actionGlobalGameFragment(subscribeStatus)
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

    override fun onDestroyView() {
        binding.rvThemes.adapter = null
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        backCallback.remove()
    }

    companion object {
        private const val requestKeyCheckPassword = "requestKeyCheckPassword"
        private const val dialogKeyNeedToSetPassword = "dialogKeyNeedToSetPassword"
    }
}

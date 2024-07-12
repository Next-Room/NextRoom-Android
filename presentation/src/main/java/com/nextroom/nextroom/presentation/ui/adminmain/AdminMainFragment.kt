package com.nextroom.nextroom.presentation.ui.adminmain

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.domain.repository.StatisticsRepository
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.common.NRTwoButtonDialog
import com.nextroom.nextroom.presentation.databinding.FragmentAdminMainBinding
import com.nextroom.nextroom.presentation.extension.addMargin
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.statusBarHeight
import com.nextroom.nextroom.presentation.extension.toast
import com.nextroom.nextroom.presentation.extension.updateSystemPadding
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe
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
            onStartGame = ::startGame,
            onClickUpdate = viewModel::updateTheme,
        )
    }

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
        setFragmentResultListeners()
        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)
    }

    /*override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            // 통계 정보 서버에 전송
            statisticsRepository.postGameStats()
        }
    }*/

    private fun startGame(themeId: Int) {
        viewModel.start(themeId) {
            goToMain(themeId)
        }
    }

    private fun initViews() = with(binding) {
        updateSystemPadding(statusBar = false, navigationBar = true)

        rvThemes.adapter = adapter
//        tvPurchaseTicketButton.setOnClickListener {
//            goToPurchase()
//        }
//        ivMyButton.setOnClickListener {
//            goToMyPage()
//        }
//        tvLogoutButton.apply {
//            addMargin(top = requireContext().statusBarHeight)
//            setOnClickListener { logout() }
//        }
        srlTheme.setOnRefreshListener {
            viewModel.loadData()
        }
        tvResignButton.addMargin(top = requireContext().statusBarHeight)
        tvResignButton.setOnClickListener {
            AdminMainFragmentDirections
                .actionGlobalNrTwoButtonDialog(
                    NRTwoButtonDialog.NRTwoButtonArgument(
                        title = getString(R.string.resign_dialog_title),
                        message = getString(R.string.resign_dialog_message),
                        posBtnText = getString(R.string.resign),
                        negBtnText = getString(R.string.dialog_no),
                        dialogKey = REQUEST_KEY_RESIGN,
                    )
                )
                .also { findNavController().safeNavigate(it) }
        }
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(REQUEST_KEY_RESIGN) { _, _ ->
            viewModel.resign()
        }
    }

    private fun render(state: AdminMainState) = with(binding) {
        if (state.loading) return@with

//        tvPurchaseTicketButton.isVisible = state.userSubscribeStatus.subscribeStatus != SubscribeStatus.Subscription
//        when (state.userSubscribeStatus.subscribeStatus) {
//            SubscribeStatus.Expiration -> logout()
//            SubscribeStatus.Hold, SubscribeStatus.SubscriptionExpiration -> goToPurchase(state.userSubscribeStatus.subscribeStatus)
//            SubscribeStatus.Free -> {
//                if (viewModel.isFirstLaunchOfDay) { // 하루 최초 한 번 다이얼로그 표시
//                    state.calculateDday().let { dday ->
//                        if (dday >= 0) showDialog(dday)
//                    }
//                }
//            }
//
//            SubscribeStatus.None, SubscribeStatus.Subscription -> Unit
//        }
        srlTheme.isRefreshing = false
        tvShopName.text = state.showName
        llEmptyThemeGuide.isVisible = state.themes.isEmpty()
        adapter.submitList(state.themes)
    }

    private fun handleEvent(event: AdminMainEvent) {
        when (event) {
            is AdminMainEvent.NetworkError -> snackbar(R.string.error_network)
            is AdminMainEvent.UnknownError -> snackbar(R.string.error_something)
            is AdminMainEvent.ClientError -> snackbar(event.message)
            is AdminMainEvent.OnResign -> toast(R.string.resign_success_message)
        }
    }

    /*private fun goToPurchase(subscribeStatus: SubscribeStatus = state.userSubscribeStatus.subscribeStatus) {
        val action = AdminMainFragmentDirections.actionAdminMainFragmentToPurchaseFragment(subscribeStatus)
        findNavController().safeNavigate(action)
    }*/

    private fun goToMyPage() {
        val action = AdminMainFragmentDirections.actionAdminMainFragmentToMypageFragment()
        findNavController().safeNavigate(action)
    }

    private fun goToMain(themeId: Int) {
        val action =
            AdminMainFragmentDirections.actionAdminMainFragmentToVerifyFragment(themeId = themeId)
        findNavController().safeNavigate(action)
    }

    /*private fun showDialog(dDay: Int) {
        NRImageDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_free_plan_title, dDay))
            .setMessage(getString(R.string.dialog_free_plan_message))
            .setImage(R.drawable.ticket)
            .setNegativeButton(getString(R.string.dialog_close)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.dialog_subscribe_button)) { _, _ ->
                goToPurchase()
            }
            .show(childFragmentManager)
    }*/

    private fun logout() {
        viewModel.logout()
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
        const val REQUEST_KEY_RESIGN = "REQUEST_KEY_RESIGN"
    }
}

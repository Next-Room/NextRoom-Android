package com.nextroom.nextroom.presentation.ui.adminmain

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.repository.StatisticsRepository
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentAdminMainBinding
import com.nextroom.nextroom.presentation.extension.addMargin
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.extension.statusBarHeight
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
        ThemesAdapter(onStartGame = ::startGame)
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

        ivMyButton.addMargin(top = requireContext().statusBarHeight)

        rvThemes.adapter = adapter
        tvPurchaseButton.setOnClickListener {
            goToPurchase()
        }
        ivMyButton.setOnClickListener {
            goToMyPage()
        }

        srlTheme.setOnRefreshListener {
            viewModel.fetchData()
        }

        llBanner.setOnClickListener {
            viewModel.container.stateFlow.value.banner?.linkUrl?.let {
                goToLink(it)
            }
            FirebaseAnalytics.getInstance(requireContext()).logEvent("btn_click", bundleOf("btn_name" to "banner"))
        }
    }

    private fun render(state: AdminMainState) = with(binding) {
        if (state.loading) return@with

        state.banner?.let {
            llBanner.isVisible = true
            tvBanner.text = it.description
        } ?: run {
            llBanner.isVisible = false
        }

        tvPurchaseButton.isVisible = state.subscribeStatus != SubscribeStatus.Subscribed
        srlTheme.isRefreshing = false
        tvShopName.text = state.shopName
        llEmptyThemeGuide.isVisible = state.themes.isEmpty()
        adapter.submitList(state.themes)
    }

    private fun handleEvent(event: AdminMainEvent) {
        when (event) {
            is AdminMainEvent.NetworkError -> snackbar(R.string.error_network)
            is AdminMainEvent.UnknownError -> snackbar(R.string.error_something)
            is AdminMainEvent.ClientError -> snackbar(event.message)
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

    private fun goToMain(themeId: Int) {
        val action =
            AdminMainFragmentDirections.actionAdminMainFragmentToVerifyFragment(themeId = themeId)
        findNavController().safeNavigate(action)
    }

    override fun onDestroyView() {
        binding.rvThemes.adapter = null
        super.onDestroyView()
    }

    override fun onDetach() {
        super.onDetach()
        backCallback.remove()
    }
}

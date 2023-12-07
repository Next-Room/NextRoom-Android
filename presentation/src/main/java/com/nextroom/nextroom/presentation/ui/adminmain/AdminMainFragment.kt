package com.nextroom.nextroom.presentation.ui.adminmain

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.domain.repository.StatisticsRepository
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentAdminMainBinding
import com.nextroom.nextroom.presentation.extension.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.viewmodel.observe
import javax.inject.Inject

@AndroidEntryPoint
class AdminMainFragment : BaseFragment<FragmentAdminMainBinding>(FragmentAdminMainBinding::inflate) {

    private lateinit var backCallback: OnBackPressedCallback

    @Inject
    lateinit var statisticsRepository: StatisticsRepository

    private val viewModel: AdminMainViewModel by viewModels()
    private val adapter: ThemesAdapter by lazy {
        ThemesAdapter(
            onStartGame = ::startGame,
            onClickUpdate = viewModel::updateHints,
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
        viewModel.observe(viewLifecycleOwner, state = ::render)
    }

    override fun onStart() {
        super.onStart()

        lifecycleScope.launch {
            // 통계 정보 서버에 전송
            statisticsRepository.postGameStats()
        }
    }

    private fun startGame(code: Int) {
        viewModel.start(code) {
            goToCounter()
        }
    }

    private fun initViews() = with(binding) {
//        ivMyButton.addMargin(top = requireContext().statusBarHeight)
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
        tvShopName.text = state.showName
        adapter.submitList(state.themes)
    }

    /*private fun goToPurchase(subscribeStatus: SubscribeStatus = state.userSubscribeStatus.subscribeStatus) {
        val action = AdminMainFragmentDirections.actionAdminMainFragmentToPurchaseFragment(subscribeStatus)
        findNavController().safeNavigate(action)
    }*/

    private fun goToMyPage() {
        val action = AdminMainFragmentDirections.actionAdminMainFragmentToMypageFragment()
        findNavController().safeNavigate(action)
    }

    private fun goToCounter() {
        val action = AdminMainFragmentDirections.actionAdminMainFragmentToVerifyFragment()
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
}

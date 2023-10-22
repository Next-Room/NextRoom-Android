package com.nextroom.nextroom.presentation.ui.adminmain

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentAdminMainBinding
import com.nextroom.nextroom.presentation.extension.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class AdminMainFragment : BaseFragment<FragmentAdminMainBinding, AdminMainState, Nothing>({ layoutInflater, viewGroup ->
    FragmentAdminMainBinding.inflate(layoutInflater, viewGroup, false)
}) {

    private lateinit var backCallback: OnBackPressedCallback

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

    private fun startGame(code: Int) {
        viewModel.start(code) {
            goToCounter()
        }
    }

    private fun initViews() = with(binding) {
        setMarginTopStatusBarHeight(ivMyButton)
        rvThemes.adapter = adapter
        tvPurchaseTicketButton.setOnClickListener {
            goToPurchase(state.userSubscribeStatus.subscribeStatus)
        }
        ivMyButton.setOnClickListener {
            goToMyPage()
        }
    }

    private fun render(state: AdminMainState) = with(binding) {
        tvPurchaseTicketButton.isVisible = state.userSubscribeStatus.subscribeStatus != SubscribeStatus.구독중
        when (state.userSubscribeStatus.subscribeStatus) {
            SubscribeStatus.None, SubscribeStatus.유예기간만료 -> logout()
            SubscribeStatus.무료체험끝, SubscribeStatus.구독만료 -> goToPurchase(state.userSubscribeStatus.subscribeStatus)
            SubscribeStatus.무료체험중 -> {
                // TODO 디데이 확인 후 다이얼로그 표시. 하루 한 번
            }

            SubscribeStatus.구독중 -> Unit
        }
        tvShopName.text = state.showName
        adapter.submitList(state.themes)
    }

    private fun goToPurchase(subscribeStatus: SubscribeStatus) {
        val action = AdminMainFragmentDirections.actionAdminMainFragmentToPurchaseFragment(subscribeStatus)
        findNavController().safeNavigate(action)
    }

    private fun goToMyPage() {
        val action = AdminMainFragmentDirections.actionAdminMainFragmentToMypageFragment()
        findNavController().safeNavigate(action)
    }

    private fun goToCounter() {
        val action = AdminMainFragmentDirections.actionAdminMainFragmentToVerifyFragment()
        findNavController().safeNavigate(action)
    }

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

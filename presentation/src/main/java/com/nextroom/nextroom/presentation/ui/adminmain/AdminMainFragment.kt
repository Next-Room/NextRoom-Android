package com.nextroom.nextroom.presentation.ui.adminmain

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentAdminMainBinding
import com.nextroom.nextroom.presentation.extension.safeNavigate
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class AdminMainFragment : BaseFragment<FragmentAdminMainBinding>(FragmentAdminMainBinding::inflate) {

    private lateinit var backCallback: OnBackPressedCallback

    private val viewModel: AdminMainViewModel by viewModels()
    private val adapter: ThemesAdapter by lazy {
        ThemesAdapter(
            onStartGame = ::startGame,
            onClickUpdate = viewModel::updateHints,
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
        viewModel.observe(viewLifecycleOwner, state = ::render)
    }

    private fun startGame(code: Int) {
        viewModel.start(code) {
            val action = AdminMainFragmentDirections.actionAdminMainFragmentToVerifyFragment()
            findNavController().safeNavigate(action)
        }
    }

    private fun initViews() = with(binding) {
        setMarginTopStatusBarHeight(tvLogoutButton)

        rvThemes.adapter = adapter
        tvLogoutButton.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun render(state: AdminMainState) = with(binding) {
        tvShopName.text = state.showName
        adapter.submitList(state.themes)
    }

    override fun onDetach() {
        super.onDetach()
        backCallback.remove()
    }
}

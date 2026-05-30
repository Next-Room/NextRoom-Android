package com.nextroom.nextroom.presentation.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.extension.BUNDLE_KEY_RESULT_DATA
import com.nextroom.nextroom.presentation.extension.hasResultData
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.toast
import com.nextroom.nextroom.presentation.model.SelectItemBottomSheetArg
import com.nextroom.nextroom.presentation.ui.login.compose.SignupScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupFragment : ComposeBaseViewModelFragment<SignupViewModel>() {
    override val screenName = "signup"
    override val viewModel: SignupViewModel by viewModels()
    private val args: SignupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by viewModel.uiState.collectAsState()
                SignupScreen(
                    state = state,
                    onBackClick = { findNavController().navigateUp() },
                    onShopNameChange = viewModel::onShopNameChanged,
                    onSignupSourceClick = ::showSelectSignupSourceBottomSheet,
                    onCustomSignupSourceChange = viewModel::setCustomSignupSource,
                    onSignupReasonClick = ::showSelectSignupReasonBottomSheet,
                    onCustomSignupReasonChange = viewModel::setCustomSignupReason,
                    onAllTermsAgreeClick = viewModel::onAllTermsAgreeClicked,
                    onServiceTermAgreeClick = viewModel::setServiceTermAgree,
                    onMarketingTermAgreeClick = viewModel::setMarketingTermAgree,
                    onServiceTermLinkClick = ::moveToServiceTermWebView,
                    onSignupClick = viewModel::signup,
                )
            }
        }
    }

    override fun setFragmentResultListeners() {
        super.setFragmentResultListeners()

        setFragmentResultListener(SELECT_SIGNUP_SOURCE_REQUEST_KEY, ::handleFragmentResults)
        setFragmentResultListener(SELECT_SIGNUP_REASON_REQUEST_KEY, ::handleFragmentResults)
    }

    private fun handleFragmentResults(requestKey: String, bundle: Bundle) {
        fun Bundle.toSelectedItem(): SignupViewModel.UIState.Loaded.SelectedItem? {
            return BundleCompat.getParcelable(
                this,
                BUNDLE_KEY_RESULT_DATA,
                SelectItemBottomSheetArg.Item::class.java
            )?.let { SignupViewModel.UIState.Loaded.SelectedItem(id = it.id, text = it.text) }
        }

        when (requestKey) {
            SELECT_SIGNUP_SOURCE_REQUEST_KEY -> {
                if (bundle.hasResultData()) {
                    bundle.toSelectedItem()?.let {
                        viewModel.setSelectedSignupSource(it)
                        if (it.text != getString(R.string.text_etc)) {
                            viewModel.setCustomSignupSource(null)
                        }
                    }
                }
            }

            SELECT_SIGNUP_REASON_REQUEST_KEY -> {
                if (bundle.hasResultData()) {
                    bundle.toSelectedItem()?.let {
                        viewModel.setSelectedSignupReason(it)
                        if (it.text != getString(R.string.text_etc)) {
                            viewModel.setCustomSignupReason(null)
                        }
                    }
                }
            }
        }
    }

    override fun initSubscribe() {
        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        SignupViewModel.UIEvent.SignupFailure -> toast(R.string.error_something)
                        SignupViewModel.UIEvent.SignupSuccess -> {
                            setFragmentResult(args.requestKey, bundleOf())
                            findNavController().navigateUp()
                        }
                    }
                }
            }
        }
    }

    private fun showSelectSignupSourceBottomSheet() {
        val loaded = viewModel.uiState.value as? SignupViewModel.UIState.Loaded ?: return
        val selected = loaded.selectedSignupSource
        resources.getStringArray(R.array.signup_source).mapIndexed { index, s ->
            SelectItemBottomSheetArg.Item(
                id = index.toString(),
                text = s,
                isSelected = index == selected?.id?.toIntOrNull()
            )
        }.let {
            SelectItemBottomSheetArg(
                header = getString(R.string.text_select_signup_source),
                items = it,
                requestKey = SELECT_SIGNUP_SOURCE_REQUEST_KEY
            )
        }.also {
            findNavController().navigate(NavGraphDirections.moveToSelectItem(it))
        }
    }

    private fun showSelectSignupReasonBottomSheet() {
        val loaded = viewModel.uiState.value as? SignupViewModel.UIState.Loaded ?: return
        val selected = loaded.selectedSignupReason
        resources.getStringArray(R.array.signup_reason).mapIndexed { index, s ->
            SelectItemBottomSheetArg.Item(
                id = index.toString(),
                text = s,
                isSelected = index == selected?.id?.toIntOrNull()
            )
        }.let {
            SelectItemBottomSheetArg(
                header = getString(R.string.text_select_signup_reason),
                items = it,
                requestKey = SELECT_SIGNUP_REASON_REQUEST_KEY
            )
        }.also {
            findNavController().navigate(NavGraphDirections.moveToSelectItem(it))
        }
    }

    private fun moveToServiceTermWebView() {
        EmailLoginFragmentDirections
            .moveToWebViewFragment(getString(R.string.link_privacy_policy))
            .also { findNavController().safeNavigate(it) }
    }

    companion object {
        const val SELECT_SIGNUP_SOURCE_REQUEST_KEY = "SELECT_SIGNUP_SOURCE_REQUEST_KEY"
        const val SELECT_SIGNUP_REASON_REQUEST_KEY = "SELECT_SIGNUP_REASON_REQUEST_KEY"
    }
}

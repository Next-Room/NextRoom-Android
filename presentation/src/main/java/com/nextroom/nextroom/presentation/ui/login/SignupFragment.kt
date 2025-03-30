package com.nextroom.nextroom.presentation.ui.login

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.BundleCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseViewModelFragment
import com.nextroom.nextroom.presentation.databinding.FragmentSignupBinding
import com.nextroom.nextroom.presentation.extension.BUNDLE_KEY_RESULT_DATA
import com.nextroom.nextroom.presentation.extension.hasResultData
import com.nextroom.nextroom.presentation.extension.inputMethodManager
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.toast
import com.nextroom.nextroom.presentation.model.SelectItemBottomSheetArg
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupFragment : BaseViewModelFragment<FragmentSignupBinding, SignupViewModel>(FragmentSignupBinding::inflate),
    View.OnClickListener {
    override val screenName = "signup"
    override val viewModel: SignupViewModel by viewModels()
    val args: SignupFragmentArgs by navArgs()

    override fun initViews() {
        super.initViews()

        binding.tvSignupComplete.isEnabled = false
    }

    override fun initListeners() {
        super.initListeners()

        binding.llSignupSource.setOnClickListener(this)
        binding.llSignupReason.setOnClickListener(this)
        binding.clAgreeAllTerms.setOnClickListener(this)
        binding.llServiceTermAgree.setOnClickListener(this)
        binding.llMarketingTermAgree.setOnClickListener(this)
        binding.tvSignupComplete.setOnClickListener(this)
        binding.etShopName.addTextChangedListener {
            viewModel.onShopNameChanged(it.toString())
        }
        binding.etShopName.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                requireActivity().inputMethodManager?.hideSoftInputFromWindow(v.windowToken, 0)
                binding.etShopName.clearFocus()
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
                bundle,
                BUNDLE_KEY_RESULT_DATA,
                SelectItemBottomSheetArg.Item::class.java
            )?.let { SignupViewModel.UIState.Loaded.SelectedItem(id = it.id, text = it.text) }
        }

        when (requestKey) {
            SELECT_SIGNUP_SOURCE_REQUEST_KEY -> {
                if (bundle.hasResultData()) {
                    bundle.toSelectedItem()?.let { viewModel.setSelectedSignupSource(it) }
                }
            }

            SELECT_SIGNUP_REASON_REQUEST_KEY -> {
                if (bundle.hasResultData()) {
                    bundle.toSelectedItem()?.let { viewModel.setSelectedSignupReason(it) }
                }
            }
        }
    }

    override fun initObserve() {
        super.initObserve()

        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is SignupViewModel.UIState.Loaded -> updateUI(state)
                        SignupViewModel.UIState.Loading -> Unit
                    }
                    binding.pbLoading.isVisible = state is SignupViewModel.UIState.Loading
                }
            }
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

    private fun updateUI(state: SignupViewModel.UIState.Loaded) {
        fun updateTextView(view: TextView, text: String?) {
            if (text == null) {
                view.setTextColor(resources.getColor(R.color.Gray01, null))
                view.text = getString(R.string.text_please_select)
            } else {
                view.setTextColor(resources.getColor(R.color.white, null))
                view.text = text
            }
        }

        updateTextView(binding.tvSignupSource, state.selectedSignupSource?.text)
        updateTextView(binding.tvSignupReason, state.selectedSignupReason?.text)
        binding.cbAgreeAllTerms.isChecked = state.allTermsAgreed
        binding.cbServiceTermAgree.isChecked = state.serviceTermAgreed
        binding.cbMarketingTermsAgree.isChecked = state.marketingTermAgreed
        binding.tvSignupComplete.isEnabled = state.allRequiredFieldFilled
    }

    private fun showSelectSignupSourceBottomSheet(selectedItem: SignupViewModel.UIState.Loaded.SelectedItem? = null) {
        resources.getStringArray(R.array.signup_source).mapIndexed { index, s ->
            SelectItemBottomSheetArg.Item(
                id = index.toString(),
                text = s,
                isSelected = index == selectedItem?.id?.toIntOrNull()
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

    private fun showSelectSignupReasonBottomSheet(selectedItem: SignupViewModel.UIState.Loaded.SelectedItem? = null) {
        resources.getStringArray(R.array.signup_reason).mapIndexed { index, s ->
            SelectItemBottomSheetArg.Item(
                id = index.toString(),
                text = s,
                isSelected = index == selectedItem?.id?.toIntOrNull()
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

    override fun onClick(v: View) {
        when (v) {
            binding.llSignupSource -> {
                (viewModel.uiState.value as? SignupViewModel.UIState.Loaded)?.let { loaded ->
                    showSelectSignupSourceBottomSheet(loaded.selectedSignupSource)
                }
            }
            binding.llSignupReason -> {
                (viewModel.uiState.value as? SignupViewModel.UIState.Loaded)?.let { loaded ->
                    showSelectSignupReasonBottomSheet(loaded.selectedSignupReason)
                }
            }
            binding.clAgreeAllTerms -> viewModel.onAllTermsAgreeClicked(binding.cbAgreeAllTerms.isChecked.not())
            binding.llServiceTermAgree -> viewModel.setServiceTermAgree(binding.cbServiceTermAgree.isChecked.not())
            binding.llMarketingTermAgree -> viewModel.setMarketingTermAgree(binding.cbMarketingTermsAgree.isChecked.not())
            binding.tvSignupComplete -> viewModel.signup()
        }
    }

    companion object {
        const val SELECT_SIGNUP_SOURCE_REQUEST_KEY = "SELECT_SIGNUP_SOURCE_REQUEST_KEY"
        const val SELECT_SIGNUP_REASON_REQUEST_KEY = "SELECT_SIGNUP_REASON_REQUEST_KEY"
    }
}
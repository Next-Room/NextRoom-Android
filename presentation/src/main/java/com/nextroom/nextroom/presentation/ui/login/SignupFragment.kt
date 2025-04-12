package com.nextroom.nextroom.presentation.ui.login

import android.os.Bundle
import android.view.View
import android.widget.EditText
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
import com.nextroom.nextroom.presentation.extension.safeNavigate
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

        fun setEditTextFocusSettings(editText: EditText) {
            editText.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    requireActivity().inputMethodManager?.hideSoftInputFromWindow(v.windowToken, 0)
                    editText.clearFocus()
                }

                if (hasFocus) {
                    R.drawable.bg_black_border_white50_r8
                } else {
                    R.drawable.bg_black_border_white20_r8
                }.also {
                    editText.setBackgroundResource(it)
                }
            }
            editText.setOnEditorActionListener { _, _, _ ->
                editText.clearFocus()
                false
            }
        }

        binding.ivBack.setOnClickListener(this)
        binding.llSignupSource.setOnClickListener(this)
        binding.llSignupReason.setOnClickListener(this)
        binding.clAgreeAllTerms.setOnClickListener(this)
        binding.tvServiceTermAgree.setOnClickListener(this)
        binding.llServiceTermAgree.setOnClickListener(this)
        binding.llMarketingTermAgree.setOnClickListener(this)
        binding.tvSignupComplete.setOnClickListener(this)
        binding.etShopName.addTextChangedListener {
            viewModel.onShopNameChanged(it.toString())
        }
        binding.etSignupSource.addTextChangedListener {
            viewModel.setCustomSignupSource(it.toString())
        }
        binding.etSignupReason.addTextChangedListener {
            viewModel.setCustomSignupReason(it.toString())
        }
        setEditTextFocusSettings(binding.etShopName)
        setEditTextFocusSettings(binding.etSignupSource)
        setEditTextFocusSettings(binding.etSignupReason)
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
                    bundle.toSelectedItem()?.let {
                        viewModel.setSelectedSignupSource(it)
                        if (it.text != getString(R.string.text_etc)) {
                            viewModel.setCustomSignupSource(null)
                            binding.etSignupSource.text = null
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
                            binding.etSignupReason.text = null
                        }
                    }
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
        binding.etSignupSource.isVisible =
            state.selectedSignupSource?.let { it.text == getString(R.string.text_etc) } ?: false
        binding.etSignupReason.isVisible =
            state.selectedSignupReason?.let { it.text == getString(R.string.text_etc) } ?: false
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

    private fun moveToServiceTermWebView() {
        EmailLoginFragmentDirections
            .moveToWebViewFragment(getString(R.string.link_privacy_policy))
            .also { findNavController().safeNavigate(it) }
    }

    override fun onClick(v: View) {
        when (v) {
            binding.ivBack -> findNavController().navigateUp()
            binding.llSignupSource -> {
                binding.etShopName.clearFocus()
                (viewModel.uiState.value as? SignupViewModel.UIState.Loaded)?.let { loaded ->
                    showSelectSignupSourceBottomSheet(loaded.selectedSignupSource)
                }
            }
            binding.llSignupReason -> {
                binding.etShopName.clearFocus()
                (viewModel.uiState.value as? SignupViewModel.UIState.Loaded)?.let { loaded ->
                    showSelectSignupReasonBottomSheet(loaded.selectedSignupReason)
                }
            }
            binding.clAgreeAllTerms -> viewModel.onAllTermsAgreeClicked(binding.cbAgreeAllTerms.isChecked.not())
            binding.tvServiceTermAgree -> moveToServiceTermWebView()
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
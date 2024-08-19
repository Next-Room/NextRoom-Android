package com.nextroom.nextroom.presentation.ui.mypage

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentSubscriptionInfoBinding
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class SubscriptionInfoFragment :
    BaseFragment<FragmentSubscriptionInfoBinding>(FragmentSubscriptionInfoBinding::inflate) {
    private val viewModel: SubscriptionInfoViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initListeners()
        initObserve()
    }

    private fun initViews() = with(binding) {
        tbSubscriptionInfo.apply {
            tvButton.isVisible = false
            tvTitle.text = getString(R.string.subscription_info)
        }
    }

    private fun initObserve() {
        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.uiState.collect { state ->
                    when (state) {
                        SubscriptionInfoViewModel.UiState.Failure -> snackbar(R.string.error_something)
                        is SubscriptionInfoViewModel.UiState.Loaded -> {
                            binding.tvSubscriptionStatus.text = when (state.subscribeStatus) {
                                SubscribeStatus.Default -> getString(R.string.ticket_not_subscribe)
                                SubscribeStatus.Subscribed -> getString(R.string.ticket_subscribing)
                            }
                            binding.tvSubscriptionPeriod.text = getSubscriptionPeriod(state.startDate, state.endDate)
                            binding.pbLoading.isVisible = false
                        }

                        SubscriptionInfoViewModel.UiState.Loading -> binding.pbLoading.isVisible = true
                    }
                }
            }
        }
    }

    private fun getSubscriptionPeriod(startDate: String?, endDate: String?): String {
        return try {
            requireNotNull(startDate)
            requireNotNull(endDate)

            val inputFormat = SimpleDateFormat(apiDateFormatter, Locale.getDefault())
            val outputFormat = SimpleDateFormat(uiDateFormatter, Locale.getDefault())

            val start = inputFormat.parse(startDate)
            val end = inputFormat.parse(endDate)

            String.format(
                getString(R.string.date_range_format),
                outputFormat.format(start),
                outputFormat.format(end),
            )
        } catch (e: Exception) {
            ""
        }
    }

    private fun initListeners() {
        binding.tbSubscriptionInfo.ivBack.setOnClickListener { findNavController().popBackStack() }
    }

    companion object {
        const val apiDateFormatter = "yyyy-MM-dd"
        const val uiDateFormatter = "yyyy.M.d"
    }
}

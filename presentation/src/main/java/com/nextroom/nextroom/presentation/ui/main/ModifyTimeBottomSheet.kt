package com.nextroom.nextroom.presentation.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nextroom.nextroom.presentation.databinding.BottomSheetModifyTimeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ModifyTimeBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetModifyTimeBinding
    private val args: ModifyTimeBottomSheetArgs by navArgs()
    private val now = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = BottomSheetModifyTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI(args.timeLimitInMinute)
        binding.ivClose.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
        binding.btnConfirm.setOnClickListener {
            setFragmentResult(args.requestKey, bundleOf(BUNDLE_KEY_MODIFIED_TIME to binding.npTime.value))
            dismiss()
        }
        binding.npTime.setOnValueChangedListener { _, _, newVal ->
            binding.tvTimerEndTime.text = addMinuteToNow(newVal)
        }
    }

    private fun updateUI(timeLimitInMinute: Int) {
        binding.npTime.minValue = 1
        binding.npTime.maxValue = MAXIMUM_TIME
        binding.npTime.value = timeLimitInMinute
        binding.tvTimerEndTime.text = addMinuteToNow(timeLimitInMinute)
    }

    private fun addMinuteToNow(minute: Int): String {
        val endTimeInMillis = now + minute * 60 * 1000
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(endTimeInMillis))
    }

    companion object {
        const val BUNDLE_KEY_MODIFIED_TIME = "BUNDLE_KEY_MODIFIED_TIME"
        const val MAXIMUM_TIME = 300
    }
}
package com.nextroom.nextroom.presentation.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.databinding.FragmentSubscriptionPaymentBinding
import com.nextroom.nextroom.presentation.databinding.ItemBenefitBinding
import com.nextroom.nextroom.presentation.extension.dpToPx
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SubscriptionPaymentBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSubscriptionPaymentBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSubscriptionPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState)
        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet =
                (dialog as BottomSheetDialog).findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.from(it).skipCollapsed = true
                BottomSheetBehavior.from(it).isHideable = false
                BottomSheetBehavior.from(it).isDraggable = false
            }
        }

        return bottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listOf(
            Benefit(
                title = getString(R.string.subscribe_beneft_first_title),
                desc = getString(R.string.subscribe_benefit_first_desc),
                subDesc = getString(R.string.subscribe_benefit_first_sub_desc),
                image = R.drawable.benefit
            ),
            Benefit(
                title = getString(R.string.subscribe_beneft_second_title),
                desc = getString(R.string.subscribe_benefit_second_desc),
                subDesc = getString(R.string.subscribe_benefit_second_sub_desc),
                image = R.drawable.benefit1
            ),
        ).map {
            ItemBenefitBinding.inflate(layoutInflater).apply {
                tvTitle.text = it.title
                tvDesc.text = it.desc
                tvDesc1.text = it.subDesc
                ivBenefit.setImageResource(it.image)
            }
        }.forEach {
            val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                .apply {
                    setMargins(0, 9.dpToPx, 0, 9.dpToPx)
                    height = 108.dpToPx
                }
            binding.llBenefit.addView(it.root, layoutParams)
        }

        binding.ivClose.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.tvLater.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    data class Benefit(
        val title: String,
        val desc: String,
        val subDesc: String,
        val image: Int
    )
}
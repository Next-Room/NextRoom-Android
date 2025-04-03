package com.nextroom.nextroom.presentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nextroom.nextroom.presentation.databinding.BottomSheetSelectItemBinding
import com.nextroom.nextroom.presentation.databinding.ItemSelectBinding
import com.nextroom.nextroom.presentation.extension.BUNDLE_KEY_RESULT_DATA
import com.nextroom.nextroom.presentation.model.SelectItemBottomSheetArg

class SelectItemBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetSelectItemBinding
    private val args: SelectItemBottomSheetArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = BottomSheetSelectItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI(args.argument)

        binding.ivClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateUI(args: SelectItemBottomSheetArg) {
        fun generateItem(item: SelectItemBottomSheetArg.Item, requestKey: String): View {
            return ItemSelectBinding.inflate(layoutInflater).apply {
                tvLabel.text = item.text
                ivCheck.isVisible = item.isSelected
                root.setOnClickListener {
                    setFragmentResult(requestKey, bundleOf(BUNDLE_KEY_RESULT_DATA to item))
                    dismiss()
                }
            }.root
        }

        binding.tvTitle.text = args.header
        binding.llContainer.removeAllViews()
        args.items.forEach { item ->
            binding.llContainer.addView(generateItem(item, args.requestKey))
        }
    }
}
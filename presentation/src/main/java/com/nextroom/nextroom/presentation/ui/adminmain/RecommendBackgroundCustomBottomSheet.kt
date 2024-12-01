package com.nextroom.nextroom.presentation.ui.adminmain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nextroom.nextroom.presentation.databinding.FragmentBottomSheetRecommendBackgrounCustomBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendBackgroundCustomBottomSheet : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetRecommendBackgrounCustomBinding? = null
    private val binding: FragmentBottomSheetRecommendBackgrounCustomBinding get() = checkNotNull(_binding)

    private val viewModel by viewModels<RecommendBackgroundCustomViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBottomSheetRecommendBackgrounCustomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.acbConfirm.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.tvCancel.setOnClickListener {
            viewModel.onDismissClicked()
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
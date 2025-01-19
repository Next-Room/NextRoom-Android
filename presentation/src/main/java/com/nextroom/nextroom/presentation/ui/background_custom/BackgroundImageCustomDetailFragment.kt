package com.nextroom.nextroom.presentation.ui.background_custom

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentBackgroundImageCustomDetailBinding
import com.nextroom.nextroom.presentation.extension.dpToPx
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackgroundImageCustomDetailFragment : BaseFragment<FragmentBackgroundImageCustomDetailBinding>(FragmentBackgroundImageCustomDetailBinding::inflate) {

    private val args by navArgs<BackgroundImageCustomDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initListener() {
        binding.layoutToolbar.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.llExpandOrCollapse.setOnClickListener {
            binding.llExpandOrCollapse.visibility = View.INVISIBLE
        }
        //PhotoView를 추가하면 bottomsheet이 드래그가 작동하지 않음.
        //아래 코드를 넣으면 bottomsheet가 이벤트를 직접 소비함으로써 드래그가 다시 가능
        binding.bottomSheet.setOnTouchListener { _, _ -> true }
    }

    private fun initView() {
        val behavior = BottomSheetBehavior.from(binding.bottomSheet)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.peekHeight = 52.dpToPx

        binding.layoutToolbar.toolbar.setBackgroundColor(Color.TRANSPARENT)
        binding.layoutToolbar.tvTitle.text = args.theme.title
        binding.layoutToolbar.tvButton.isVisible = true
        binding.layoutToolbar.tvButton.text = getString(R.string.save)

        Glide.with(requireContext())
            .load(args.theme.themeImageUrl)
            .into(binding.imgTheme)
    }
}
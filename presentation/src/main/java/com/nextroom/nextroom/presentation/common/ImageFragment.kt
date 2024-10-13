package com.nextroom.nextroom.presentation.common

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentHintImageBinding

class ImageFragment(
    private val imageUrl: String,
    private val onImageClicked: (() -> Unit)? = null
) : BaseFragment<FragmentHintImageBinding>(FragmentHintImageBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initListener() {
        binding.imgHint.setOnClickListener {
            onImageClicked?.invoke()
        }
    }

    private fun initView() {
        Glide.with(requireContext())
            .load(imageUrl)
            .into(binding.imgHint)
    }
}
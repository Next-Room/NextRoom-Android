package com.nextroom.nextroom.presentation.common

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentHintImageBinding

class ImageFragment(
    private val image: Image,
    private val onImageClicked: (() -> Unit)? = null
) : BaseFragment<FragmentHintImageBinding>(FragmentHintImageBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initListener() {
        binding.ivHint.setOnClickListener {
            onImageClicked?.invoke()
        }
    }

    private fun initView() {
        when(image) {
            is Image.Drawable -> binding.ivError.setImageResource(image.resourceId)
            is Image.Url -> Glide.with(requireContext())
                .load(image.url)
                .into(binding.ivHint)
            Image.None -> Unit
        }
    }

    sealed interface Image {
        data class Url(val url: String): Image
        data class Drawable(val resourceId: Int): Image
        data object None: Image
    }
}
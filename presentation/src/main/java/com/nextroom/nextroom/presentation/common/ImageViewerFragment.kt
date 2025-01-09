package com.nextroom.nextroom.presentation.common

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentImageViewerBinding


class ImageViewerFragment : BaseFragment<FragmentImageViewerBinding>(FragmentImageViewerBinding::inflate) {

    private val args by navArgs<ImageViewerFragmentArgs>()
    private var imageAdapter: ImageAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseAnalytics.getInstance(requireContext()).logEvent("screen_view", bundleOf("screen_name" to "image_viewer"))
        initViews()
        initListener()
    }

    private fun initListener() {
        binding.toolbar.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.vpImg.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.toolbar.tvTitle.text = String.format(
                    getString(R.string.image_viewer_title_format),
                    (position + 1).toString(),
                    (args.imageUrlList.size).toString()
                )
            }
        })
    }

    private fun initViews() {
        imageAdapter = ImageAdapter()
        binding.vpImg.adapter = imageAdapter
        args.imageUrlList
            .map { ImageAdapter.Image.Url(it) }
            .also { imageAdapter?.setList(it) }

        binding.root.post {
            binding.vpImg.setCurrentItem(args.position, false)
        }

        binding.toolbar.ivBack.setImageResource(R.drawable.ic_exit24)
        binding.toolbar.ivBack.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        binding.toolbar.tvTitle.text = String.format(
            getString(R.string.image_viewer_title_format),
            (args.position + 1).toString(),
            (args.imageUrlList.size).toString()
        )
        binding.toolbar.tvButton.isVisible = false
    }

    override fun onDestroyView() {
        imageAdapter = null
        super.onDestroyView()
    }
}
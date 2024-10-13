package com.nextroom.nextroom.presentation.common

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentImageViewerBinding


class ImageViewerFragment : BaseFragment<FragmentImageViewerBinding>(FragmentImageViewerBinding::inflate) {

    private val args by navArgs<ImageViewerFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    getString(R.string.text_image_size),
                    (position + 1).toString(),
                    (args.imageUrlList.size).toString()
                )
            }
        })
    }

    private fun initViews() {
        binding.vpImg.adapter = object : FragmentStateAdapter(requireActivity()) {
            override fun getItemCount(): Int {
                return args.imageUrlList.size
            }

            override fun createFragment(position: Int): Fragment {
                return ImageFragment(imageUrl = args.imageUrlList[position])
            }
        }
        binding.root.post {
            binding.vpImg.setCurrentItem(args.position, false)
        }

        binding.toolbar.ivBack.setImageResource(R.drawable.ic_exit24)
        binding.toolbar.ivBack.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
        binding.toolbar.tvTitle.text = String.format(
            getString(R.string.text_image_size),
            (args.position + 1).toString(),
            (args.imageUrlList.size).toString()
        )
        binding.toolbar.tvButton.isVisible = false
    }
}
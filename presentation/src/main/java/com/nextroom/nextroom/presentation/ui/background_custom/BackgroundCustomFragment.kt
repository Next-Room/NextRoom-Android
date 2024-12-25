package com.nextroom.nextroom.presentation.ui.background_custom

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentBackgroundCustomBinding
import com.nextroom.nextroom.presentation.databinding.ItemBackgroundCustomInfoBinding
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackgroundCustomFragment : BaseFragment<FragmentBackgroundCustomBinding>(FragmentBackgroundCustomBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.toolbar.tvTitle.text = getString(R.string.text_timer_background_custom)
        binding.toolbar.tvButton.isVisible = false
        binding.toolbar.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.llInfo.removeAllViews()
        listOf(
            getString(R.string.text_background_custom_info_1),
            getString(R.string.text_background_custom_info_2),
            getString(R.string.text_background_custom_info_3)
        ).map {
            ItemBackgroundCustomInfoBinding
                .inflate(layoutInflater)
                .apply {
                    tvInfo.text = it
                }
        }.forEach {
            binding.llInfo.addView(it.root)
        }

        binding.rvTheme.adapter = ThemeBackgroundToggleAdapter()
        (binding.rvTheme.adapter as? ThemeBackgroundToggleAdapter)?.submitList(
            listOf(
                ThemeInfoPresentation(title = "testtttttttttt")
            )
        )
    }
}
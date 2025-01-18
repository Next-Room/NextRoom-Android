package com.nextroom.nextroom.presentation.ui.background_custom

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentBackgroundImageCustomDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BackgroundImageCustomDetailFragment : BaseFragment<FragmentBackgroundImageCustomDetailBinding>(FragmentBackgroundImageCustomDetailBinding::inflate) {

    private val args by navArgs<BackgroundImageCustomDetailFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
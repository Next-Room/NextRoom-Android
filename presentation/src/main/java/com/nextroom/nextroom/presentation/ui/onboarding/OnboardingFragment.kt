package com.nextroom.nextroom.presentation.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.databinding.FragmentOnboardingBinding
import com.nextroom.nextroom.presentation.extension.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding
        get() = _binding ?: error("binding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        binding.tvApplyFreeTrial.setOnClickListener {
            val action = OnboardingFragmentDirections.actionGlobalWebViewFragment("https://m.naver.com/") // TODO: 홈페이지 주소 확정시 변경
            findNavController().safeNavigate(action)
        }
        binding.tvExistingUserGuide.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

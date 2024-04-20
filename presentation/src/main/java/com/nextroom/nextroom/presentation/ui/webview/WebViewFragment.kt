package com.nextroom.nextroom.presentation.ui.webview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nextroom.nextroom.presentation.databinding.FragmentWebviewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewFragment : Fragment() {
    private var _binding: FragmentWebviewBinding? = null
    private val binding
        get() = _binding ?: error("binding is null")

    private val args: WebViewFragmentArgs by navArgs()

    private lateinit var backCallback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (_binding != null && binding.webview.canGoBack()) {
                    binding.webview.goBack()
                } else {
                    findNavController().popBackStack()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentWebviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webview.apply {
            settings.apply {
                builtInZoomControls = false
                domStorageEnabled = true
                javaScriptEnabled = true
                userAgentString = "$userAgentString APP_NEXTROOM_ANDROID"
                setSupportZoom(false)
            }
            loadUrl(args.url)
        }
    }

    override fun onDetach() {
        backCallback.remove()
        super.onDetach()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

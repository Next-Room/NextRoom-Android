package com.nextroom.nextroom.presentation.ui.memo

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentMemoBinding
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.toTimerFormat
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class MemoFragment :
    BaseFragment<FragmentMemoBinding, MemoState, MemoEvent>({ layoutInflater, viewGroup ->
        FragmentMemoBinding.inflate(layoutInflater, viewGroup, false)
    }) {

    private val args: MemoFragmentArgs by navArgs()

    private lateinit var backCallback: OnBackPressedCallback
    private val viewModel: MemoViewModel by viewModels()
    private val painterViewModel: PainterViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {}
        }
        requireActivity().onBackPressedDispatcher.addCallback(backCallback)
        setFullscreen()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observe(viewLifecycleOwner, state = ::render, sideEffect = ::handleEvent)

        initViews()
        initListeners()
    }

    private fun initViews() = with(binding) {
        tbMemo.apply {
            ivBack.setOnClickListener { findNavController().popBackStack() }
            if (args.fromHint) {
                tvButton.isVisible = true
                tvButton.text = getString(R.string.common_hint_eng)
                tvButton.setOnClickListener {
                    val action = MemoFragmentDirections.actionMemoFragmentToHintFragment()
                    findNavController().safeNavigate(action)
                }
            } else {
                tvButton.isVisible = false
            }
        }
        customPainterView.setPaths(painterViewModel.paths)
    }

    private fun initListeners() = with(binding) {
        ivPenButton.setOnClickListener { viewModel.pickPen() }
        ivEraserButton.setOnClickListener { viewModel.pickEraser() }
        ivEraseAllButton.setOnClickListener { viewModel.eraseAll() }
        customPainterView.setOnPainterListener {
            painterViewModel.savePaths(it)
        }
    }

    private fun render(state: MemoState) = with(binding) {
        // 타이머 렌더링
        tbMemo.tvTitle.text = state.lastSeconds.toTimerFormat()

        if (state.currentTool == DrawingTool.Pen) {
            customPainterView.pickPen()
        } else {
            customPainterView.pickEraser()
        }
        ivPenButton.isSelected = state.currentTool == DrawingTool.Pen
        ivEraserButton.isSelected = state.currentTool == DrawingTool.Eraser
    }

    private fun handleEvent(event: MemoEvent) {
        when (event) {
            MemoEvent.EraseAll -> binding.customPainterView.eraseAll()
        }
    }

    override fun onDetach() {
        super.onDetach()
        backCallback.remove()
    }
}

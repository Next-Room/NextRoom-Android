package com.nextroom.nextroom.presentation.ui.counter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentStartTimerBinding
import com.nextroom.nextroom.presentation.extension.enableFullScreen
import org.orbitmvi.orbit.viewmodel.observe

class CounterFragment : BaseFragment<FragmentStartTimerBinding>(FragmentStartTimerBinding::inflate) {

    private val viewModel: CounterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enableFullScreen()

        viewModel.observe(viewLifecycleOwner, state = ::render)
    }

    override fun onStop() {
        super.onStop()
        viewModel.onLeave() // 화면 이탈 시간 체크
    }

    private fun render(state: CounterState) = with(binding) {
        tvLastSeconds.text = state.lastSeconds.toString()
    }
}

package com.nextroom.nextroom.presentation.ui.counter

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.domain.model.TimerState
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentStartTimerBinding
import com.nextroom.nextroom.presentation.extension.enableFullScreen
import org.orbitmvi.orbit.viewmodel.observe

class CounterFragment : BaseFragment<FragmentStartTimerBinding>(FragmentStartTimerBinding::inflate) {

    private val viewModel: CounterViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        enableFullScreen()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observe(viewLifecycleOwner, lifecycleState = Lifecycle.State.CREATED, state = ::render)
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.container.stateFlow.value.timerState is TimerState.Finished) {
            val action = CounterFragmentDirections.actionStartTimerFragmentToMainFragment()
            findNavController().navigate(action)
        }
    }

    private fun render(state: CounterState) = with(binding) {
        tvLastSeconds.text = state.lastSeconds.toString()

        if (state.timerState is TimerState.Finished) {
            val action = CounterFragmentDirections.actionStartTimerFragmentToMainFragment()
            findNavController().navigate(action)
        }
    }
}

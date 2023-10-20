package com.nextroom.nextroom.presentation.ui.counter

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.domain.model.TimerState
import com.nextroom.nextroom.presentation.BuildConfig
import com.nextroom.nextroom.presentation.base.BaseFragment
import com.nextroom.nextroom.presentation.databinding.FragmentStartTimerBinding
import org.orbitmvi.orbit.viewmodel.observe

class CounterFragment :
    BaseFragment<FragmentStartTimerBinding, CounterState, Nothing>({ layoutInflater, viewGroup ->
        FragmentStartTimerBinding.inflate(layoutInflater, viewGroup, false)
    }) {

    private val viewModel: CounterViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setFullscreen()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observe(viewLifecycleOwner, state = ::render)
    }

    override fun onStart() {
        super.onStart()
        viewModel.startCounter(if (BuildConfig.DEBUG) 3 else 10)
    }

    private fun render(state: CounterState) = with(binding) {
        tvLastSeconds.text = state.lastSeconds.toString()

        if (state.timerState is TimerState.Finished) {
            val action = CounterFragmentDirections.actionStartTimerFragmentToMainFragment()
            findNavController().navigate(action)
        }
    }
}

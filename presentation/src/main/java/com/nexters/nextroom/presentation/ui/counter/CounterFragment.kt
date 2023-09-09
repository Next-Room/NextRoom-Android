package com.nexters.nextroom.presentation.ui.counter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nexters.nextroom.domain.model.TimerState
import com.nexters.nextroom.presentation.base.BaseFragment
import com.nexters.nextroom.presentation.databinding.FragmentStartTimerBinding
import com.nexters.nextroom.presentation.ui.theme.NextRoomTheme
import org.orbitmvi.orbit.compose.collectAsState

class CounterFragment :
    BaseFragment<FragmentStartTimerBinding, CounterState, Nothing>({ layoutInflater, viewGroup ->
        FragmentStartTimerBinding.inflate(layoutInflater, viewGroup, false)
    }) {

    private val viewModel: CounterViewModel by viewModels()
    override var _binding: FragmentStartTimerBinding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        setFullscreen()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStartTimerBinding.inflate(inflater, container, false)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                NextRoomTheme {
                    Surface {
                        val state by viewModel.collectAsState()

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            LaunchedEffect(Unit) {
                                viewModel.startCounter()
                            }
                            Text(
                                text = state.lastSeconds.toString(),
                                style = MaterialTheme.typography.displayLarge,
                            )
                        }

                        if (state.timerState is TimerState.Finished) {
                            val action =
                                CounterFragmentDirections.actionStartTimerFragmentToMainFragment()
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
        return binding.root
    }
}

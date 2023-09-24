package com.nexters.nextroom.presentation.ui.counter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nexters.nextroom.domain.model.TimerState
import com.nexters.nextroom.presentation.ui.theme.NextRoomTheme
import org.orbitmvi.orbit.compose.collectAsState

class CounterFragment : Fragment() {

    private val viewModel: CounterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                NextRoomTheme {
                    Surface {
                        LaunchedEffect(Unit) {
                            viewModel.startCounter()
                        }

                        val state by viewModel.collectAsState()
                        CounterScreen(state)
                    }
                }
            }
        }
    }

    @Composable
    fun CounterScreen(state: CounterState) {
        if (state.timerState is TimerState.Finished) {
            val action =
                CounterFragmentDirections.actionStartTimerFragmentToMainFragment()
            findNavController().navigate(action)
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = state.lastSeconds.toString(),
                style = MaterialTheme.typography.displayLarge,
            )
        }
    }
}

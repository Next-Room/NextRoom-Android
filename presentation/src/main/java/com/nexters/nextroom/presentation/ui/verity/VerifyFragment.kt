package com.nexters.nextroom.presentation.ui.verity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nexters.nextroom.presentation.R
import com.nexters.nextroom.presentation.base.BaseFragment
import com.nexters.nextroom.presentation.databinding.FragmentAdminCodeBinding
import com.nexters.nextroom.presentation.extension.safeNavigate
import com.nexters.nextroom.presentation.extension.snackbar
import com.nexters.nextroom.presentation.ui.component.button.MainButton
import com.nexters.nextroom.presentation.ui.component.textinput.NRTextInput
import com.nexters.nextroom.presentation.ui.login.LoginEvent
import com.nexters.nextroom.presentation.ui.login.LoginState
import com.nexters.nextroom.presentation.ui.theme.NextRoomTheme
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@AndroidEntryPoint
class VerifyFragment :
    BaseFragment<FragmentAdminCodeBinding, LoginState, Nothing>({ layoutInflater, viewGroup ->
        FragmentAdminCodeBinding.inflate(layoutInflater, viewGroup, false)
    }) {

    private val viewModel: VerifyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAdminCodeBinding.inflate(inflater, container, false)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                NextRoomTheme {
                    Surface {
                        val state by viewModel.collectAsState()
                        viewModel.collectSideEffect(sideEffect = ::handleEvent)
                        VerifyScreen(modifier = Modifier.fillMaxSize(), state = state)
                    }
                }
            }
        }
        return binding.root
    }

    private fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.LoginSuccess -> {
                val action = VerifyFragmentDirections.actionVerifyFragmentToStartTimerFragment()
                findNavController().safeNavigate(action)
            }

            is LoginEvent.LoginFailed -> snackbar(event.message)
            is LoginEvent.ShowMessage -> snackbar(event.message.toString(requireContext()))
        }
    }

    @Composable
    fun VerifyScreen(modifier: Modifier = Modifier, state: VerifyState) {
        Column(modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.size(90.dp))
            Text(
                text = stringResource(id = R.string.admin_code_input_label),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = stringResource(id = R.string.admin_code_description),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 10.dp),
            )
            NRTextInput(
                modifier = Modifier.padding(top = 32.dp),
                value = state.currentInput,
                onValueChange = viewModel::inputCode,
                hint = stringResource(id = R.string.admin_code_hint),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { viewModel.complete() }),
            )
            MainButton(
                modifier = Modifier.padding(top = 44.dp),
                text = stringResource(id = R.string.admin_code_input_button),
                enabled = state.currentInput.length == 5,
                onClick = viewModel::complete,
            )
            TextButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                contentPadding = PaddingValues(),
                onClick = { viewModel.forgotCode() },
            ) {
                Text(
                    text = stringResource(id = R.string.admin_code_forgot_button),
                    style = MaterialTheme.typography.labelSmall,
                    textDecoration = TextDecoration.Underline,
                )
            }
        }
    }

    @Preview
    @Composable
    fun VerifyScreenPreview() {
        NextRoomTheme {
            Surface {
                VerifyScreen(
                    modifier = Modifier.fillMaxSize(),
                    state = VerifyState(),
                )
            }
        }
    }
}

package com.nexters.nextroom.presentation.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nexters.nextroom.presentation.R
import com.nexters.nextroom.presentation.base.BaseFragment
import com.nexters.nextroom.presentation.databinding.FragmentLoginBinding
import com.nexters.nextroom.presentation.extension.safeNavigate
import com.nexters.nextroom.presentation.extension.snackbar
import com.nexters.nextroom.presentation.model.InputState
import com.nexters.nextroom.presentation.ui.component.button.MainButton
import com.nexters.nextroom.presentation.ui.component.textinput.NRTextInput
import com.nexters.nextroom.presentation.ui.theme.NextRoomTheme
import dagger.hilt.android.AndroidEntryPoint
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@AndroidEntryPoint
class LoginFragment :
    BaseFragment<FragmentLoginBinding, LoginState, LoginEvent>({ layoutInflater, viewGroup ->
        FragmentLoginBinding.inflate(layoutInflater, viewGroup, false)
    }) {

    private val viewModel: LoginViewModel by viewModels()

    override var _binding: FragmentLoginBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                NextRoomTheme {
                    Surface {
                        val state by viewModel.collectAsState()
                        viewModel.collectSideEffect(sideEffect = ::handleEvent)
                        LoginScreen(modifier = Modifier.fillMaxSize(), state = state)
                    }
                }
            }
        }
        return binding.root
    }

    @Composable
    private fun LoginScreen(modifier: Modifier = Modifier, state: LoginState) {
        BoxWithConstraints(modifier = modifier) {
            Image(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                painter = painterResource(id = R.drawable.bg),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                alpha = 0.4f,
            )
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.size(90.dp))
                Text(
                    text = stringResource(id = R.string.login_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.size(12.dp))
                Text(
                    text = stringResource(id = R.string.login_description),
                    style = MaterialTheme.typography.labelMedium,
                )
                Spacer(modifier = Modifier.size(32.dp))
                NRTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.currentIdInput,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next,
                        autoCorrect = false,
                    ),
                    hint = stringResource(id = R.string.login_admin_code_hint),
                    isError = state.idInputState is InputState.Error,
                ) {
                    viewModel.inputCode(it)
                }
                Spacer(modifier = Modifier.size(16.dp))
                NRTextInput(
                    modifier = Modifier.fillMaxWidth(),
                    value = state.currentPasswordInput,
                    hint = stringResource(id = R.string.login_password_hint),
                    isPassword = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { viewModel.complete() },
                    ),
                    isError = state.passwordInputState is InputState.Error,
                ) {
                    viewModel.inputPassword(it)
                }
                Spacer(Modifier.size(42.dp))
                MainButton(
                    text = stringResource(id = R.string.login_button),
                    isKorean = false,
                ) {
                    viewModel.complete()
                }
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
            Text(
                text = stringResource(id = R.string.privacy_policy),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 24.dp).clickable {
                    val action = LoginFragmentDirections.actionGlobalWebViewFragment(
                        "https://basalt-cathedral-c81.notion.site/fbb1f04ae70d473380e64d12ed013df8?pvs=4",
                    )
                    findNavController().safeNavigate(action)
                },
            )
        }
    }

    private fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.LoginSuccess -> {
                val action =
                    LoginFragmentDirections.actionAdminCodeFragmentToAdminMainFragment()
                findNavController().safeNavigate(action)
            }

            is LoginEvent.ShowMessage -> snackbar(event.message.toString(requireContext()))
            is LoginEvent.LoginFailed -> {
                snackbar(event.message)
            }
        }
    }

    override fun onStop() {
        viewModel.initState()
        super.onStop()
    }

    @Preview(device = Devices.PIXEL_3A, showSystemUi = true)
    @Composable
    fun LoginScreenPreview() {
        NextRoomTheme {
            Surface(Modifier.fillMaxSize()) {
                LoginScreen(
                    modifier = Modifier.fillMaxSize(),
                    state = LoginState(
                        currentIdInput = "12321",
                        currentPasswordInput = "next123!@#",
                    ),
                )
            }
        }
    }
}

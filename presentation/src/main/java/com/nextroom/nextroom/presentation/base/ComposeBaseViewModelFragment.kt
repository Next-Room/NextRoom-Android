package com.nextroom.nextroom.presentation.base

import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.common.NROneButtonDialog
import com.nextroom.nextroom.presentation.extension.repeatOnStarted
import com.nextroom.nextroom.presentation.extension.safeNavigate
import kotlinx.coroutines.launch

abstract class ComposeBaseViewModelFragment<VM : NewBaseViewModel> : ComposeBaseFragment() {
    abstract val viewModel: VM

    private var pendingErrorAction: NewBaseViewModel.ErrorAction = NewBaseViewModel.ErrorAction.STAY

    override fun initObserve() {
        super.initObserve()

        setFragmentResultListener(ERROR_DIALOG_KEY) { _, _ ->
            val action = pendingErrorAction
            pendingErrorAction = NewBaseViewModel.ErrorAction.STAY
            if (action == NewBaseViewModel.ErrorAction.POP_BACK_STACK) {
                findNavController().popBackStack()
            }
        }

        viewLifecycleOwner.repeatOnStarted {
            launch {
                viewModel.errorFlow.collect { event ->
                    pendingErrorAction = event.action
                    NavGraphDirections.moveToNrOneButtonDialog(
                        NROneButtonDialog.NROneButtonArgument(
                            title = getString(R.string.dialog_noti),
                            message = getString(R.string.error_something),
                            btnText = getString(R.string.text_confirm),
                            errorText = event.throwable.message,
                            dialogKey = ERROR_DIALOG_KEY,
                        )
                    ).also { findNavController().safeNavigate(it) }
                }
            }
        }
    }

    companion object {
        private const val ERROR_DIALOG_KEY = "compose_base_error_dialog"
    }
}

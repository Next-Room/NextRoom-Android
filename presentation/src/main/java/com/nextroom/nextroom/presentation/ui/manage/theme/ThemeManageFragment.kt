package com.nextroom.nextroom.presentation.ui.manage.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.domain.model.ThemeInfo
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.common.NRTwoButtonDialog
import com.nextroom.nextroom.presentation.common.compose.NRLoading
import com.nextroom.nextroom.presentation.extension.BUNDLE_KEY_RESULT_DATA
import com.nextroom.nextroom.presentation.extension.getResultData
import com.nextroom.nextroom.presentation.extension.hasResultData
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThemeManageFragment : ComposeBaseViewModelFragment<ThemeManageViewModel>() {

    override val screenName: String = "theme_manage"
    override val viewModel: ThemeManageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is ThemeManageEvent.ThemeSaved -> snackbar(R.string.theme_manage_save_success)
                            is ThemeManageEvent.ThemeDeleted -> snackbar(R.string.theme_manage_delete_success)
                        }
                    }
                }

                when (val state = uiState) {
                    is ThemeManageUiState.Loading -> NRLoading(isVisible = true)
                    is ThemeManageUiState.Loaded -> ThemeManageScreen(
                        state = state,
                        onBackClick = { findNavController().navigateUp() },
                        onAddClick = viewModel::showAddSheet,
                        onThemeClick = { moveToHintManage(it) },
                        onEditClick = viewModel::showEditSheet,
                        onDeleteClick = { showDeleteConfirmDialog(it) },
                        onHideSheet = viewModel::hideSheet,
                        onTitleChange = viewModel::updateTitle,
                        onTimeLimitChange = viewModel::updateTimeLimit,
                        onHintLimitChange = viewModel::updateHintLimit,
                        onSaveTheme = viewModel::saveTheme,
                    )
                }
            }
        }
    }

    override fun initSubscribe() {
        // Event handling is done in Compose LaunchedEffect
    }

    override fun setFragmentResultListeners() {
        setFragmentResultListener(DIALOG_KEY_DELETE_THEME) { _, bundle ->
            if (bundle.hasResultData()) {
                bundle.getResultData()?.toIntOrNull()?.let { themeId ->
                    viewModel.confirmDelete(themeId)
                }
            }
        }
    }

    private fun showDeleteConfirmDialog(theme: ThemeInfo) {
        NavGraphDirections.moveToNrTwoButtonDialog(
            NRTwoButtonDialog.NRTwoButtonArgument(
                title = "'${theme.title}' ${getString(R.string.theme_manage_delete_confirm)}",
                message = getString(R.string.theme_manage_delete_confirm_desc),
                posBtnText = getString(R.string.text_delete),
                negBtnText = getString(R.string.text_cancel),
                dialogKey = DIALOG_KEY_DELETE_THEME,
                bundle = bundleOf(BUNDLE_KEY_RESULT_DATA to theme.id.toString())
            )
        ).also {
            findNavController().safeNavigate(it)
        }
    }

    fun moveToHintManage(themeInfo: ThemeInfo) {
        ThemeManageFragmentDirections.moveToHintManageFragment(
            themeId = themeInfo.id,
            themeTitle = themeInfo.title,
        ).also {
            findNavController().safeNavigate(it)
        }
    }

    companion object {
        private const val DIALOG_KEY_DELETE_THEME = "DIALOG_KEY_DELETE_THEME"
    }
}

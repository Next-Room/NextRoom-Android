package com.nextroom.nextroom.presentation.ui.manage.hint

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.nextroom.nextroom.domain.model.Hint
import com.nextroom.nextroom.presentation.NavGraphDirections
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.ComposeBaseViewModelFragment
import com.nextroom.nextroom.presentation.common.NRTwoButtonDialog
import com.nextroom.nextroom.presentation.common.compose.NRLoading
import com.nextroom.nextroom.presentation.extension.safeNavigate
import com.nextroom.nextroom.presentation.extension.snackbar
import com.nextroom.nextroom.presentation.ui.manage.hint.compose.HintManageScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HintManageFragment : ComposeBaseViewModelFragment<HintManageViewModel>() {

    override val screenName: String = "hint_manage"
    override val viewModel: HintManageViewModel by viewModels()

    private var onHintImagesSelected: ((List<Uri>) -> Unit)? = null
    private var onAnswerImagesSelected: ((List<Uri>) -> Unit)? = null

    private val hintImagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = MAX_SELECT_IMAGE_COUNT)
    ) { uris ->
        if (uris.isNotEmpty()) {
            onHintImagesSelected?.invoke(uris)
        }
    }

    private val answerImagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = MAX_SELECT_IMAGE_COUNT)
    ) { uris ->
        if (uris.isNotEmpty()) {
            onAnswerImagesSelected?.invoke(uris)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        onHintImagesSelected = { uris -> viewModel.addHintImages(uris) }
        onAnswerImagesSelected = { uris -> viewModel.addAnswerImages(uris) }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is HintManageEvent.RequestDeleteHint -> showDeleteConfirmDialog(event.hint)
                            is HintManageEvent.HintSaved -> snackbar(R.string.hint_manage_save_success)
                            is HintManageEvent.HintDeleted -> snackbar(R.string.hint_manage_delete_success)
                            is HintManageEvent.ImageUploadFailed -> {
                                when (event.reason) {
                                    HintManageEvent.ImageUploadFailed.Reason.INVALID_FORMAT -> R.string.image_invalid_format
                                    HintManageEvent.ImageUploadFailed.Reason.INVALID_SIZE -> R.string.image_invalid_size
                                    HintManageEvent.ImageUploadFailed.Reason.EXCEED_IMAGE_COUNT -> R.string.image_count_exceed
                                    HintManageEvent.ImageUploadFailed.Reason.CONVERT_FAIL -> R.string.image_convert_failed
                                    HintManageEvent.ImageUploadFailed.Reason.UPLOAD_FAIL -> R.string.image_upload_failed
                                    HintManageEvent.ImageUploadFailed.Reason.NOT_SUBSCRIBE -> R.string.feature_for_subscriber
                                }.also {
                                    snackbar(it)
                                }
                            }
                        }
                    }
                }

                when (val state = uiState) {
                    is HintManageUiState.PreLoading -> NRLoading(isVisible = true)
                    is HintManageUiState.Loaded -> HintManageScreen(
                        themeTitle = viewModel.themeTitle,
                        state = state,
                        onBackClick = { findNavController().navigateUp() },
                        onAddClick = viewModel::showAddSheet,
                        onHintClick = viewModel::showEditSheet,
                        onDeleteClick = viewModel::requestDelete,
                        onHideSheet = viewModel::hideSheet,
                        onCodeChange = viewModel::updateCode,
                        onContentsChange = viewModel::updateContents,
                        onAnswerChange = viewModel::updateAnswer,
                        onProgressChange = viewModel::updateProgress,
                        onSaveHint = viewModel::saveHint,
                        onAddHintImages = { launchHintImagePicker() },
                        onAddAnswerImages = { launchAnswerImagePicker() },
                        onRemoveHintImage = viewModel::removeHintImage,
                        onRemoveAnswerImage = viewModel::removeAnswerImage,
                        onSortTypeChange = viewModel::changeSortType,
                    )
                }
            }
        }
    }

    override fun initSubscribe() {
        // Event handling is now done in Compose LaunchedEffect
    }

    override fun setFragmentResultListeners() {
        setFragmentResultListener(DIALOG_KEY_DELETE_HINT) { _, _ ->
            viewModel.confirmDelete()
        }
    }

    private fun launchHintImagePicker() {
        hintImagePickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun launchAnswerImagePicker() {
        answerImagePickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun showDeleteConfirmDialog(hint: Hint) {
        NavGraphDirections.moveToNrTwoButtonDialog(
            NRTwoButtonDialog.NRTwoButtonArgument(
                title = getString(R.string.hint_manage_delete_confirm),
                message = "[${hint.code}] ${hint.description.take(ELLIPSIS_THRESHOLD)}${if (hint.description.length > ELLIPSIS_THRESHOLD) "…" else ""}",
                posBtnText = getString(R.string.text_delete),
                negBtnText = getString(R.string.text_cancel),
                dialogKey = DIALOG_KEY_DELETE_HINT,
            )
        ).also {
            findNavController().safeNavigate(
                direction = it,
                navOptions = NavOptions.Builder().setLaunchSingleTop(true).build(),
            )
        }
    }

    companion object {
        private const val DIALOG_KEY_DELETE_HINT = "DIALOG_KEY_DELETE_HINT"
        private const val ELLIPSIS_THRESHOLD = 30
        private const val MAX_SELECT_IMAGE_COUNT = 5
    }
}

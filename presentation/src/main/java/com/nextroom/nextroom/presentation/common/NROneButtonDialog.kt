package com.nextroom.nextroom.presentation.common

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nextroom.nextroom.presentation.R
import com.nextroom.nextroom.presentation.base.BaseDialogFragment
import com.nextroom.nextroom.presentation.databinding.DialogFragmentNrOneButtonBinding
import com.nextroom.nextroom.presentation.extension.dp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class NROneButtonDialog :
    BaseDialogFragment<DialogFragmentNrOneButtonBinding>(DialogFragmentNrOneButtonBinding::inflate) {

    private val args: NROneButtonDialogArgs by navArgs()
    private var isErrorTextExpanded = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = args.nrOneButtonArgument.isCancelable
        return super.onCreateDialog(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val bg = InsetDrawable(ColorDrawable(Color.TRANSPARENT), 36.dp)
        dialog?.window?.setBackgroundDrawable(bg)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (args.nrOneButtonArgument as? NROneButtonArgument)?.let { arg ->
            binding.tvTitle.text = arg.title
            binding.tvMessage.text = arg.message
            binding.button.text = arg.btnText
            binding.tvError.text = arg.errorText
            binding.clShowError.isVisible = !arg.errorText.isNullOrEmpty()
        }

        initListeners()
    }

    private fun initListeners() {
        binding.button.setOnClickListener {
            findNavController().popBackStack()
            args.nrOneButtonArgument.dialogKey?.let {
                setFragmentResult(it, bundleOf())
            }
        }
        binding.llShowError.setOnClickListener {
            if (isErrorTextExpanded) {
                binding.tvShowError.text = getString(R.string.text_show_error)
                binding.ivArrow.setImageResource(R.drawable.ic_arrow_down)
                binding.tvError.isVisible = false
                isErrorTextExpanded = false
            } else {
                binding.tvShowError.text = getString(R.string.text_fold)
                binding.ivArrow.setImageResource(R.drawable.ic_arrow_up)
                binding.tvError.isVisible = true
                isErrorTextExpanded = true
            }
        }
    }

    @Parcelize
    data class NROneButtonArgument(
        val title: String,
        val message: String,
        val isCancelable: Boolean = false,
        val btnText: String? = null,
        val errorText: String? = null,
        val dialogKey: String? = null,
    ) : Parcelable
}

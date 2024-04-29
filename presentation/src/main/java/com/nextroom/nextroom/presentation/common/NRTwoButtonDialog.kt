package com.nextroom.nextroom.presentation.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.nextroom.nextroom.presentation.base.BaseDialogFragment
import com.nextroom.nextroom.presentation.databinding.DialogFragmentNrTwoButtonBinding
import com.nextroom.nextroom.presentation.extension.dp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class NRTwoButtonDialog :
    BaseDialogFragment<DialogFragmentNrTwoButtonBinding>(DialogFragmentNrTwoButtonBinding::inflate) {

    private val args: NRTwoButtonDialogArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val bg = InsetDrawable(ColorDrawable(Color.TRANSPARENT), 36.dp)
        dialog?.window?.setBackgroundDrawable(bg)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(args.nrTwoButtonArgument) {
            binding.tvTitle.text = title
            binding.tvMessage.text = message
            binding.btnPositive.text = posBtnText
            binding.btnNegative.text = negBtnText
        }

        initListeners()
    }

    private fun initListeners() {
        binding.btnPositive.setOnClickListener {
            dismiss()
            setFragmentResult(args.nrTwoButtonArgument.dialogKey, bundleOf())
        }
        binding.btnNegative.setOnClickListener {
            dismiss()
        }
    }

    @Parcelize
    data class NRTwoButtonArgument(
        val title: String,
        val message: String,
        val isCancelable: Boolean = false,
        val posBtnText: String? = null,
        val negBtnText: String? = null,
        val dialogKey: String,
    ) : Parcelable
}

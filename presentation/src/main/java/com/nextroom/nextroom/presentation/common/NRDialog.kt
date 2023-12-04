package com.nextroom.nextroom.presentation.common

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.nextroom.nextroom.presentation.databinding.CommonDialogBinding
import com.nextroom.nextroom.presentation.extension.dp

class NRDialog : DialogFragment() {
    private var _binding: CommonDialogBinding? = null
    private val binding: CommonDialogBinding
        get() = _binding ?: error("binding is null")

    private var title: String? = null
    private var message: String = ""
    private var isCancelable: Boolean = false
    private var posBtnText: String? = null
    private var negBtnText: String? = null
    private var posListener: OnClickListener? = null
    private var negListener: OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            title = it.getString(ARG_TITLE)
            message = it.getString(ARG_MESSAGE, "")
            isCancelable = it.getBoolean(ARG_IS_CANCELABLE)
            posBtnText = it.getString(ARG_POS_BTN)
            negBtnText = it.getString(ARG_NEG_BTN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CommonDialogBinding.inflate(inflater, container, false)
        val bg = InsetDrawable(ColorDrawable(Color.TRANSPARENT), 36.dp)
        dialog?.window?.setBackgroundDrawable(bg)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.setCancelable(isCancelable)
        binding.tvTitle.isVisible = title != null
        title?.let { title -> binding.tvTitle.text = title }

        binding.tvMessage.text = message

        binding.btnPositive.isVisible = posBtnText != null
        posBtnText?.let { text ->
            binding.btnPositive.text = text
            binding.btnPositive.setOnClickListener {
                posListener?.onClick(this@NRDialog.dialog, if (negBtnText == null) 0 else 1)
            }
        }

        binding.btnNegative.isVisible = negBtnText != null
        negBtnText?.let { text ->
            binding.btnNegative.text = text
            binding.btnNegative.setOnClickListener {
                negListener?.onClick(this@NRDialog.dialog, 0)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT,
        )
    }

    fun setPositiveListener(listener: OnClickListener) {
        posListener = listener
    }

    fun setNegativeListener(listener: OnClickListener) {
        negListener = listener
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    class Builder(private val context: Context) {
        private val params = Params()

        fun setTitle(title: String): Builder {
            params.title = title
            return this
        }

        fun setTitle(@StringRes titleId: Int): Builder {
            return setTitle(context.getString(titleId))
        }

        fun setMessage(message: String): Builder {
            params.message = message
            return this
        }

        fun setMessage(@StringRes messageId: Int): Builder {
            return setMessage(context.getString(messageId))
        }

        fun setCancelable(isCancelable: Boolean): Builder {
            params.isCancelable = isCancelable
            return this
        }

        fun setPositiveButton(text: String, onClickListener: OnClickListener): Builder {
            params.posBtnText = text
            params.posListener = onClickListener
            return this
        }

        fun setPositiveButton(
            @StringRes textId: Int,
            onClickListener: OnClickListener,
        ): Builder {
            return setPositiveButton(context.getString(textId), onClickListener)
        }

        fun setNegativeButton(text: String, onClickListener: OnClickListener): Builder {
            params.negBtnText = text
            params.negListener = onClickListener
            return this
        }

        fun setNegativeButton(
            @StringRes textId: Int,
            onClickListener: OnClickListener,
        ): Builder {
            return setNegativeButton(context.getString(textId), onClickListener)
        }

        fun create(): NRDialog {
            return params.create()
        }

        fun show(manager: FragmentManager, tag: String? = null): NRDialog {
            return create().also {
                it.show(manager, tag)
            }
        }
    }

    companion object {
        private const val ARG_TITLE = "argTitle"
        private const val ARG_MESSAGE = "argMessage"
        private const val ARG_IS_CANCELABLE = "argIsCancelable"
        private const val ARG_POS_BTN = "argPositiveButtonText"
        private const val ARG_NEG_BTN = "argNegativeButtonText"

        private data class Params(
            var title: String? = null,
            var message: String? = null,
            var isCancelable: Boolean? = true,
            var posBtnText: String? = null,
            var posListener: OnClickListener? = null,
            var negBtnText: String? = null,
            var negListener: OnClickListener? = null,
        ) {
            fun create(): NRDialog {
                return NRDialog().apply {
                    arguments = Bundle().apply {
                        this@Params.title?.let { title -> putString(ARG_TITLE, title) }
                        this@Params.message?.let { message -> putString(ARG_MESSAGE, message) }
                        this@Params.isCancelable?.let { isCancelable -> putBoolean(ARG_IS_CANCELABLE, isCancelable) }
                        this@Params.posBtnText?.let { text -> putString(ARG_POS_BTN, text) }
                        this@Params.negBtnText?.let { text -> putString(ARG_NEG_BTN, text) }
                    }
                    this@Params.posListener?.let { listener -> setPositiveListener(listener) }
                    this@Params.negListener?.let { listener -> setNegativeListener(listener) }
                }
            }
        }
    }
}

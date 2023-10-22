package com.nextroom.nextroom.presentation.common

import android.app.ActionBar
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.nextroom.nextroom.presentation.databinding.CommonImageDialogBinding
import com.nextroom.nextroom.presentation.extension.dp

class NRImageDialog private constructor() : DialogFragment() {
    private var _binding: CommonImageDialogBinding? = null
    private val binding: CommonImageDialogBinding
        get() = _binding ?: error("binding is null")

    private var title: String? = null
    private var message: String = ""
    private var imageUrl: String? = null
    private var imageRes: Int? = null
    private var posBtnText: String? = null
    private var negBtnText: String? = null
    private var posListener: DialogInterface.OnClickListener? = null
    private var negListener: DialogInterface.OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
        arguments?.let {
            title = it.getString(ARG_TITLE)
            message = it.getString(ARG_MESSAGE, "")
            imageUrl = it.getString(ARG_IMAGE_URL)
            imageRes = it.getInt(ARG_IMAGE_RES)
            posBtnText = it.getString(ARG_POS_BTN)
            negBtnText = it.getString(ARG_NEG_BTN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CommonImageDialogBinding.inflate(inflater, container, false)
        val bg = InsetDrawable(ColorDrawable(Color.TRANSPARENT), 36.dp)
        dialog?.window?.setBackgroundDrawable(bg)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() = with(binding) {
        tvTitle.isVisible = title != null
        title?.let { title -> tvTitle.text = title }

        tvMessage.text = message
        imageUrl?.let { url -> setImageUrl(url) } ?: imageRes?.let { res -> setImageDrawable(res) }

        tvPositiveButton.isVisible = posBtnText != null
        posBtnText?.let { text ->
            tvPositiveButton.text = text
            tvPositiveButton.setOnClickListener {
                posListener?.onClick(this@NRImageDialog.dialog, if (negBtnText == null) 0 else 1)
            }
        }

        tvNegativeButton.isVisible = negBtnText != null
        negBtnText?.let { text ->
            tvNegativeButton.text = text
            tvNegativeButton.setOnClickListener {
                negListener?.onClick(this@NRImageDialog.dialog, 0)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT,
        )
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun setMessage(message: String) {
        binding.tvMessage.text = message
    }

    fun setImageDrawable(@DrawableRes drawableRes: Int) {
        Glide.with(binding.ivImage)
            .load(drawableRes)
            .into(binding.ivImage)
    }

    fun setImageUrl(url: String) {
        Glide.with(binding.ivImage)
            .load(url)
            .into(binding.ivImage)
    }

    fun setPositiveListener(listener: DialogInterface.OnClickListener) {
        posListener = listener
    }

    fun setNegativeListener(listener: DialogInterface.OnClickListener) {
        negListener = listener
    }

    override fun onDestroyView() {
        _binding = null
        posListener = null
        negListener = null
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

        fun setImage(imageUrl: String): Builder {
            params.imageUrl = imageUrl
            return this
        }

        fun setImage(@DrawableRes drawableRes: Int): Builder {
            params.imageRes = drawableRes
            return this
        }

        fun setPositiveButton(text: String, onClickListener: DialogInterface.OnClickListener): Builder {
            params.posBtnText = text
            params.posListener = onClickListener
            return this
        }

        fun setPositiveButton(
            @StringRes textId: Int,
            onClickListener: DialogInterface.OnClickListener,
        ): Builder {
            return setPositiveButton(context.getString(textId), onClickListener)
        }

        fun setNegativeButton(text: String, onClickListener: DialogInterface.OnClickListener): Builder {
            params.negBtnText = text
            params.negListener = onClickListener
            return this
        }

        fun setNegativeButton(
            @StringRes textId: Int,
            onClickListener: DialogInterface.OnClickListener,
        ): Builder {
            return setNegativeButton(context.getString(textId), onClickListener)
        }

        fun create(): NRImageDialog {
            return params.create()
        }

        fun show(manager: FragmentManager, tag: String? = null): NRImageDialog {
            return create().also {
                it.show(manager, tag)
            }
        }
    }

    companion object {
        private const val ARG_TITLE = "argTitle"
        private const val ARG_MESSAGE = "argMessage"
        private const val ARG_IMAGE_URL = "argImageUrl"
        private const val ARG_IMAGE_RES = "argImageRes"
        private const val ARG_POS_BTN = "argPositiveButtonText"
        private const val ARG_NEG_BTN = "argNegativeButtonText"

        private data class Params(
            var title: String? = null,
            var message: String? = null,
            var imageUrl: String? = null,
            @DrawableRes var imageRes: Int? = null,
            var posBtnText: String? = null,
            var posListener: DialogInterface.OnClickListener? = null,
            var negBtnText: String? = null,
            var negListener: DialogInterface.OnClickListener? = null,
        ) {
            fun create(): NRImageDialog {
                return NRImageDialog().apply {
                    arguments = Bundle().apply {
                        this@Params.title?.let { title -> putString(ARG_TITLE, title) }
                        this@Params.message?.let { message -> putString(ARG_MESSAGE, message) }
                        this@Params.imageUrl?.let { url -> putString(ARG_IMAGE_URL, url) }
                        this@Params.imageRes?.let { res -> putInt(ARG_IMAGE_RES, res) }
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

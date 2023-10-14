package com.nextroom.nextroom.presentation.common

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.nextroom.nextroom.presentation.databinding.CommonSnackbarBinding

class NRSnackbar private constructor(
    view: View,
    private val message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
) {

    private constructor(
        view: View,
        @StringRes messageId: Int,
        duration: Int = Snackbar.LENGTH_SHORT,
    ) : this(view, view.context.getString(messageId), duration)

    private val context = view.context
    private val snackbar = Snackbar.make(view, message, duration)
    private val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout
    private val binding: CommonSnackbarBinding = CommonSnackbarBinding.inflate(
        LayoutInflater.from(context),
        null,
        false,
    )

    init {
        initView()
        initData()
    }

    private fun initView() = with(snackbarLayout) {
        removeAllViews()
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        addView(binding.root)
    }

    private fun initData() = with(binding) {
        tvMessage.text = message
    }

    fun show() {
        snackbar.show()
    }

    companion object {
        fun make(view: View, message: String, duration: Int = Snackbar.LENGTH_SHORT) =
            NRSnackbar(view, message, duration)

        fun make(view: View, @StringRes messageId: Int, duration: Int = Snackbar.LENGTH_SHORT) =
            NRSnackbar(view, messageId, duration)
    }
}

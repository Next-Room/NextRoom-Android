package com.nexters.nextroom.presentation.customview

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.nexters.nextroom.presentation.R
import com.nexters.nextroom.presentation.databinding.CustomCodeInputBinding

class CodeInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val maxLength: Int = DEFAULT_LENGTH
    private var currentValue: String = ""
    private var _binding: CustomCodeInputBinding? = null
    private val binding: CustomCodeInputBinding
        get() = _binding ?: error("binding is null")

    private val codes: Array<TextView>

    init {
        _binding = CustomCodeInputBinding.inflate(LayoutInflater.from(context), null, false)
        addView(binding.root)
        codes = arrayOf(binding.tvCode1, binding.tvCode2, binding.tvCode3, binding.tvCode4)
    }

    fun setCode(code: String) {
        currentValue = if (code.length > maxLength) code.slice(0 until maxLength) else code
        clearCodes()
        if (codes.isNotEmpty()) {
            currentValue.forEachIndexed { i, c ->
                codes[i].setDefault()
                codes[i].text = c.toString()
                if (i < 3) codes[i + 1].setFocused()
            }
        }
        invalidate()
    }

    fun setError() {
        codes.forEach { it.setError() }
        invalidate()
        ObjectAnimator.ofFloat(binding.root, "translationX", 0f, 5f, 0f, -5f, 0f).apply {
            repeatCount = 3
            duration = 50
            start()
        }
    }

    private fun clearCodes() {
        codes.forEach {
            it.text = ""
            it.setDefault()
        }
    }

    private fun TextView.setDefault() {
        background = ResourcesCompat.getDrawable(resources, R.drawable.bg_code_normal, null)
    }

    private fun TextView.setError() {
        background = ResourcesCompat.getDrawable(resources, R.drawable.bg_code_error, null)
    }

    private fun TextView.setFocused() {
        background = ResourcesCompat.getDrawable(resources, R.drawable.bg_code_focused, null)
    }

    companion object {
        private const val DEFAULT_LENGTH = 4
    }
}

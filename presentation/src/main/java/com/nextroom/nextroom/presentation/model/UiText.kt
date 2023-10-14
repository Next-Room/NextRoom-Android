package com.nextroom.nextroom.presentation.model

import android.content.Context
import androidx.annotation.StringRes

class UiText private constructor(
    val message: String? = null,
    @StringRes val messageId: Int? = null,
) {
    constructor(message: String) : this(message, null)
    constructor(@StringRes messageId: Int) : this(null, messageId)

    fun toString(context: Context): String {
        return message ?: messageId?.let { context.getString(it) } ?: ""
    }

    override fun toString(): String {
        return message ?: ""
    }

    override fun hashCode(): Int {
        return message.hashCode() * 31 + messageId.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return message?.let { it == other.toString() }
            ?: messageId?.let { it.hashCode() == other.hashCode() }
            ?: true
    }
}

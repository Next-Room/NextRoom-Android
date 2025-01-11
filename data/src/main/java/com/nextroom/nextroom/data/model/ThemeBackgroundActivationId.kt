package com.nextroom.nextroom.data.model

import com.google.gson.annotations.SerializedName

data class ThemeBackgroundActivationId(
    @SerializedName("activate")
    val activate: List<Int>,
    @SerializedName("deactivate")
    val deactivate: List<Int>
)

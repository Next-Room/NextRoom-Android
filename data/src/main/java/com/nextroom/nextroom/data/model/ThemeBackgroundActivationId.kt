package com.nextroom.nextroom.data.model

import com.google.gson.annotations.SerializedName

data class ThemeBackgroundActivationId(
    @SerializedName("active")
    val activate: List<Int>,
    @SerializedName("deactive")
    val deactivate: List<Int>
)

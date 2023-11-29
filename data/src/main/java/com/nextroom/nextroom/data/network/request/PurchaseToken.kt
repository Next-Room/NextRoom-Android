package com.nextroom.nextroom.data.network.request

import com.google.gson.annotations.SerializedName

data class PurchaseToken(
    @SerializedName("purchaseToken") val purchaseToken: String,
)

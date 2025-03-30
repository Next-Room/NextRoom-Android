package com.nextroom.nextroom.presentation.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SelectItemBottomSheetArg(
    val header: String,
    val items: List<Item>,
    val requestKey: String,
) : Parcelable {
    @Parcelize
    data class Item(
        val id: String,
        val text: String,
        val isSelected: Boolean,
    ) : Parcelable
}
package com.nextroom.nextroom.presentation.extension

import java.net.URI

fun String?.isSamePath(otherLink: String?): Boolean {
    if (this == null && otherLink == null) return true
    if (this == null || otherLink == null) return false
    return try {
        URI(this).path == URI(otherLink).path
    } catch (e: Exception) {
        false
    }
}
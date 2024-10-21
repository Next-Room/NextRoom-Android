package com.nextroom.nextroom.data.db

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val loggedIn: Boolean = false,
    val adminCode: String = "",
    val shopName: String = "",
    val latestGameCode: Int = -1,
    val accessToken: String = "",
    val refreshToken: String = "",
    val isInitLaunch: Boolean = true,
    val lastLaunchDate: Long = 0L,
    val emailSaveChecked: Boolean = false,
    val userEmail: String = ""
)

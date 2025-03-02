package com.nextroom.nextroom.domain.util

interface UserEventLogger {
    fun logScreenEvent(screenName: String)
    fun logClickEvent(buttonName: String)
}
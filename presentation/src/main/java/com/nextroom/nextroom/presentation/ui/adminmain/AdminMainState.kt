package com.nextroom.nextroom.presentation.ui.adminmain

import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation

data class AdminMainState(
    val loading: Boolean = false,
//    val userSubscribeStatus: UserSubscribeStatus = UserSubscribeStatus(),
    val showName: String = "",
    val themes: List<ThemeInfoPresentation> = emptyList(),
) {
    /*private val dateTimeUtil = DateTimeUtil()

    fun calculateDday(): Int {
        return when (userSubscribeStatus.subscribeStatus) {
            SubscribeStatus.Free -> dateTimeUtil.stringToDate(userSubscribeStatus.expiryDate, "yyyy.MM.dd")?.calculateDday() ?: -1
            else -> -1
        }
    }*/
}

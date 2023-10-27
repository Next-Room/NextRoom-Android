package com.nextroom.nextroom.presentation.ui.adminmain

import com.nextroom.nextroom.domain.model.UserSubscribeStatus
import com.nextroom.nextroom.presentation.extension.calculateDday
import com.nextroom.nextroom.presentation.model.ThemeInfoPresentation
import com.nextroom.nextroom.util.DateTimeUtil

data class AdminMainState(
    val loading: Boolean = false,
    val userSubscribeStatus: UserSubscribeStatus = UserSubscribeStatus(),
    val showName: String = "",
    val themes: List<ThemeInfoPresentation> = emptyList(),
) {
    private val dateTimeUtil = DateTimeUtil()

    fun calculateDday(): Int {
        return dateTimeUtil.stringToDate(userSubscribeStatus.expiredDate, "yyyy.MM.dd")?.calculateDday() ?: -1
    }
}

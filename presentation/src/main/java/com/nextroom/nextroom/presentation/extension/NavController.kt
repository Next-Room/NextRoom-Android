package com.nextroom.nextroom.presentation.extension

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions

fun NavController.safeNavigate(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
}

fun NavController.safeNavigate(
    @IdRes currentDestinationId: Int,
    @IdRes id: Int,
    args: Bundle? = null,
) {
    if (currentDestinationId == currentDestination?.id) navigate(id, args)
}

fun NavController.navigateToStartDest() {
    val startDestination = graph.startDestinationId
    val navOptions = NavOptions.Builder()
        .setPopUpTo(startDestination, true)
        .build()
    navigate(startDestination, null, navOptions)
}

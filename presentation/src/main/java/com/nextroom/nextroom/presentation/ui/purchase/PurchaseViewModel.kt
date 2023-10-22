package com.nextroom.nextroom.presentation.ui.purchase

import androidx.lifecycle.SavedStateHandle
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<PurchaseState, PurchaseEvent>() {
    override val container: Container<PurchaseState, PurchaseEvent> = container(
        PurchaseState(savedStateHandle["subscribeStatus"] ?: SubscribeStatus.None),
    )
}

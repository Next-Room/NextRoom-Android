package com.nextroom.nextroom.presentation.ui.purchase

import androidx.lifecycle.SavedStateHandle
import com.nextroom.nextroom.domain.model.SubscribeItem
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.model.Ticket
import com.nextroom.nextroom.domain.model.UserSubscribe
import com.nextroom.nextroom.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<PurchaseState, PurchaseEvent>() {
    override val container: Container<PurchaseState, PurchaseEvent> = container(
        PurchaseState(
            subscribeStatus = savedStateHandle["subscribeStatus"] ?: SubscribeStatus.None,
            userSubscribe = UserSubscribe(SubscribeItem(id = 1, name = "미니")),
        ),
    )

    init {
        fetchSubscribes()
    }

    fun startPurchase(ticket: Ticket) = intent {
        postSideEffect(PurchaseEvent.StartPurchase(ticket))
    }

    private fun fetchSubscribes() = intent {
        reduce {
            state.copy(
                tickets = listOf(
                    Ticket(
                        id = 1,
                        plan = "미니",
                        description = "2개의 테마를 등록할 수 있어요.",
                        originPrice = 19900,
                        sellPrice = 9900,
                    ),
                    Ticket(
                        id = 2,
                        plan = "미디움",
                        description = "5개의 테마를 등록할 수 있어요.",
                        originPrice = 29900,
                        sellPrice = 14900,
                    ),
                    Ticket(
                        id = 3,
                        plan = "라지",
                        description = "8개의 테마를 등록할 수 있어요.",
                        originPrice = 39900,
                        sellPrice = 19900,
                    ),
                ),
            )
        }
    }
}

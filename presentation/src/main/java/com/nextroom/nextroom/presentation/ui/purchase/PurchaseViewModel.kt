package com.nextroom.nextroom.presentation.ui.purchase

import androidx.lifecycle.SavedStateHandle
import com.nextroom.nextroom.domain.model.SubscribeItem
import com.nextroom.nextroom.domain.model.SubscribeStatus
import com.nextroom.nextroom.domain.model.Ticket
import com.nextroom.nextroom.domain.model.UserSubscription
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.BillingRepository
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
    private val billingRepository: BillingRepository,
) : BaseViewModel<PurchaseState, PurchaseEvent>() {
    override val container: Container<PurchaseState, PurchaseEvent> = container(
        PurchaseState(
            subscribeStatus = savedStateHandle["subscribeStatus"] ?: SubscribeStatus.None,
            userSubscription = UserSubscription(SubscribeItem(id = 1, name = "미니")),
        ),
    )

    init {
        fetchTickets()
    }

    fun startPurchase(ticket: Ticket) = intent {
        postSideEffect(PurchaseEvent.StartPurchase(ticket))
    }

    private fun fetchTickets() = intent {
        billingRepository.getTickets().onSuccess { tickets ->
            reduce { state.copy(tickets = tickets) }
        }
        /*reduce {
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
        }*/
    }
}

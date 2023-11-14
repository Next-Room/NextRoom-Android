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
            userSubscription = UserSubscription(SubscribeItem(id = "", name = "미니")),
        ),
    )

    init {
        fetchTickets()
    }

    fun startPurchase(ticket: Ticket) = intent {
        postSideEffect(
            PurchaseEvent.StartPurchase(
                productId = ticket.id.toString(),
                tag = "",
                upDowngrade = (ticket.id == container.stateFlow.value.userSubscribe?.type?.id),
            ),
        )
    }

    private fun fetchTickets() = intent {
        billingRepository.getTickets().onSuccess { tickets ->
            reduce { state.copy(tickets = tickets) }
        }
    }
}

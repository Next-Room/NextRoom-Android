package com.nextroom.nextroom.presentation.ui.purchase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.BillingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel @Inject constructor(
    private val billingRepository: BillingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        fetchTickets()
    }

    // TODO JH: 수정
//    fun startPurchase(ticket: Ticket) = intent {
//        postSideEffect(
//            PurchaseEvent.StartPurchase(
//                productId = ticket.id,
//                tag = "",
//                upDowngrade = (ticket.id == container.stateFlow.value.userSubscription?.type?.id),
//            ),
//        )
//    }

    private fun fetchTickets() {
        viewModelScope.launch {
            billingRepository.getTickets().onSuccess { tickets ->
                tickets.firstOrNull()?.let { ticket ->
                    UiState.Loaded(
                        id = ticket.id,
                        subscriptionProductId = ticket.subscriptionProductId,
                        planId = ticket.planId,
                        productName = ticket.productName,
                        description = ticket.description,
                        subDescription = ticket.subDescription,
                        originPrice = DecimalFormat("#,###원").format(ticket.originPrice),
                        sellPrice = DecimalFormat("#,###원").format(ticket.sellPrice),
                        discountRate = ticket.discountRate,
                    )
                }?.also {
                    _uiState.emit(it)
                }
            }.onFailure {
                _uiState.emit(UiState.Failure)
            }
        }
    }

    sealed interface UiState {
        data object Loading : UiState
        data class Loaded(
            val id: String,
            val subscriptionProductId: String,
            val planId: String,
            val productName: String,
            val description: String,
            val subDescription: String,
            val originPrice: String,
            val sellPrice: String,
            val discountRate: Int,
        ) : UiState

        data object Failure : UiState
    }
}

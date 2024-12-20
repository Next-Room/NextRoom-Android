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
        fetchPlans()
    }

    private fun fetchPlans() {
        viewModelScope.launch {
            billingRepository.getTicketInfo().onSuccess { ticket ->
                ticket.plans.firstOrNull()?.let { plan ->
                    UiState.Loaded(
                        id = plan.id,
                        subscriptionProductId = plan.subscriptionProductId,
                        planId = plan.planId,
                        productName = plan.productName,
                        description = plan.description,
                        subDescription = plan.subDescription,
                        originPrice = DecimalFormat("#,###원").format(plan.originPrice),
                        sellPrice = DecimalFormat("#,###원").format(plan.sellPrice),
                        discountRate = plan.discountRate,
                        url = ticket.url
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
            val url: String,
        ) : UiState

        data object Failure : UiState
    }
}

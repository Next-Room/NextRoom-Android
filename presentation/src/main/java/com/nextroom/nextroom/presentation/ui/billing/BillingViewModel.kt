package com.nextroom.nextroom.presentation.ui.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.nextroom.nextroom.domain.model.onFailure
import com.nextroom.nextroom.domain.model.onSuccess
import com.nextroom.nextroom.domain.repository.BillingRepository
import com.nextroom.nextroom.presentation.ui.Constants
import com.nextroom.nextroom.presentation.util.BillingClientLifecycle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BillingViewModel
@Inject constructor(
    billingClientLifecycle: BillingClientLifecycle,
    billingRepository: BillingRepository,
) : ViewModel() {

    // 사용자의 현재 구독 상품 구매 정보
    private val purchases = billingClientLifecycle.subscriptionPurchases

    // 콘솔에 등록된 상품들 정보
    private val membershipProductWithProductDetails = billingClientLifecycle.membershipProductWithProductDetails

    private val _buyEvent = MutableSharedFlow<BillingFlowParams>()
    val buyEvent = _buyEvent.asSharedFlow()

    private val _uiEvent = MutableSharedFlow<BillingEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            billingClientLifecycle.uiEvent.collect {
                when (it) {
                    BillingClientLifecycle.UIEvent.PurchaseAcknowledged -> _uiEvent.emit(BillingEvent.PurchaseAcknowledged)
                }
            }
        }
        viewModelScope.launch {
            purchases.collect {
                it.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        billingRepository
                            .postPurchaseToken(purchase.purchaseToken)
                            .onSuccess {
                                _uiEvent.emit(BillingEvent.PurchaseAcknowledged)
                            }
                            .onFailure {
                                _uiEvent.emit(BillingEvent.PurchaseFailed(purchaseState = purchase.purchaseState))
                            }
                    } else {
                        _uiEvent.emit(BillingEvent.PurchaseFailed(purchaseState = purchase.purchaseState))
                    }
                }
            }
        }
    }

    /**
     * @param productDetails ProductDetails object returned by the library.
     * @param offerToken the least priced offer's offer id token returned by
     * [leastPricedOfferToken].
     *
     * @return [BillingFlowParams] builder.
     */
    private fun billingFlowParamsBuilder(productDetails: ProductDetails, offerToken: String):
        BillingFlowParams {
        return BillingFlowParams.newBuilder().setProductDetailsParamsList(
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)
                    .build(),
            ),
        ).build()
    }

    /**
     * BillingFlowParams Builder for upgrades and downgrades.
     *
     * @param productDetails ProductDetails object returned by the library.
     * @param offerToken the least priced offer's offer id token returned by
     * [leastPricedOfferToken].
     * @param oldToken the purchase token of the subscription purchase being upgraded or downgraded.
     *
     * @return [BillingFlowParams] builder.
     */
    private fun upDowngradeBillingFlowParamsBuilder(
        productDetails: ProductDetails,
        offerToken: String,
        oldToken: String,
    ): BillingFlowParams {
        return BillingFlowParams.newBuilder().setProductDetailsParamsList(
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)
                    .build(),
            ),
        ).setSubscriptionUpdateParams(
            BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                .setOldPurchaseToken(oldToken)
                .setSubscriptionReplacementMode(
                    BillingFlowParams.SubscriptionUpdateParams.ReplacementMode.DEFERRED,
                ).build(),
        ).build()
    }

    // 이미 구독 중인 상품이 있는지 체크
    private fun purchaseForProduct(purchases: List<Purchase>?, product: String) =
        purchases?.firstOrNull { it.products.first() == product }

    // 이미 구독 중인 상품이 있는지 리턴 (로컬)
    fun deviceHasGooglePlaySubscription(purchases: List<Purchase>?, product: String) =
        purchaseForProduct(purchases, product) != null

    /**
     * 요금제 구매
     *
     * @param productId: 구매 하려는 상품의 id
     * @param upDowngrade: 구매가 업그레이드 또는 다운그레이드인지, 요금제를 전환하려는 경우에 true
     */
    fun buyPlans(productId: String, upDowngrade: Boolean) {
        val isProductOnDevice = deviceHasGooglePlaySubscription(purchases.value, productId)
        if (isProductOnDevice) {
            Timber.d("The user already owns this item: $productId")
            return
        }

        when (productId) {
            Constants.MEMBERSHIP_PRODUCT -> membershipProductWithProductDetails.value
            else -> null
        }?.let { productDetails ->
            val offerToken = requireNotNull(productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken)
            launchFlow(upDowngrade, offerToken, productDetails)
        } ?: run {
            throw Exception("Could not find product details. productId: $productId")
        }
    }

    private fun launchFlow(
        upDowngrade: Boolean,
        offerToken: String,
        productDetails: ProductDetails,
    ) {
        val currentSubscriptionPurchaseCount = purchases.value.count {
            it.products.contains(Constants.MEMBERSHIP_PRODUCT)
        }
        if (currentSubscriptionPurchaseCount > EXPECTED_SUBSCRIPTION_PURCHASE_LIST_SIZE) {
            Timber.e("There are more than one subscription purchases on the device.")
            return

            TODO(
                "Handle this case better, such as by showing a dialog to the user or by " +
                    "programmatically getting the correct purchase token.",
            )
        }

        val oldToken = purchases.value.filter {
            it.products.contains(Constants.MEMBERSHIP_PRODUCT)
        }.firstOrNull { it.purchaseToken.isNotEmpty() }?.purchaseToken ?: ""

        val billingParams: BillingFlowParams = if (upDowngrade) {
            upDowngradeBillingFlowParamsBuilder(
                productDetails = productDetails,
                offerToken = offerToken,
                oldToken = oldToken,
            )
        } else {
            billingFlowParamsBuilder(
                productDetails = productDetails,
                offerToken = offerToken,
            )
        }

        viewModelScope.launch {
            _buyEvent.emit(billingParams)
        }
    }

    companion object {
        const val TAG = "BillingViewModel"
        const val EXPECTED_SUBSCRIPTION_PURCHASE_LIST_SIZE = 1 // 가질 수 있는 최대 상품 개수
    }
}

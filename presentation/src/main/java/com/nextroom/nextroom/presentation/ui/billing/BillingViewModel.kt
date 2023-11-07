package com.nextroom.nextroom.presentation.ui.billing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
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
) : ViewModel() {

    // 사용자의 현재 구독 상품 구매 정보
    private val purchases = billingClientLifecycle.subscriptionPurchases

    // 콘솔에 등록된 상품들 정보
    private val premiumSubProductWithProductDetails = billingClientLifecycle.premiumSubProductWithProductDetails
    private val basicSubProductWithProductDetails = billingClientLifecycle.basicSubProductWithProductDetails

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
                        billingClientLifecycle.acknowledgePurchase(purchase.purchaseToken)
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
                    BillingFlowParams.SubscriptionUpdateParams.ReplacementMode.CHARGE_FULL_PRICE,
                ).build(),
        ).build()
    }

    /**
     * Calculates the lowest priced offer amongst all eligible offers.
     * In this implementation the lowest price of all offers' pricing phases is returned.
     * It's possible the logic can be implemented differently.
     * For example, the lowest average price in terms of month could be returned instead.
     *
     * @param offerDetails List of of eligible offers and base plans.
     *
     * @return the offer id token of the lowest priced offer.
     *
     */
    private fun leastPricedOfferToken(
        offerDetails: List<ProductDetails.SubscriptionOfferDetails>,
    ): String {
        var offerToken = String()
        var leastPricedOffer: ProductDetails.SubscriptionOfferDetails
        var lowestPrice = Int.MAX_VALUE

        if (offerDetails.isNotEmpty()) {
            for (offer in offerDetails) {
                for (price in offer.pricingPhases.pricingPhaseList) {
                    if (price.priceAmountMicros < lowestPrice) {
                        lowestPrice = price.priceAmountMicros.toInt()
                        leastPricedOffer = offer
                        offerToken = leastPricedOffer.offerToken
                    }
                }
            }
        }
        return offerToken

        TODO("Replace this with least average priced offer implementation")
    }

    /**
     * Retrieves all eligible base plans and offers using tags from ProductDetails.
     *
     * @param offerDetails offerDetails from a ProductDetails returned by the library.
     * @param tag string representing tags associated with offers and base plans.
     *
     * @return the eligible offers and base plans in a list.
     *
     */
    private fun retrieveEligibleOffers(
        offerDetails: MutableList<ProductDetails.SubscriptionOfferDetails>,
        tag: String,
    ):
        List<ProductDetails.SubscriptionOfferDetails> {
        val eligibleOffers = emptyList<ProductDetails.SubscriptionOfferDetails>().toMutableList()
        offerDetails.forEach { offerDetail ->
            if (offerDetail.offerTags.contains(tag)) {
                eligibleOffers.add(offerDetail)
            }
        }
        return eligibleOffers
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
     * @param tag: 요금제와 관련된 태그를 나타내는 문자열
     * @param product: 구매 하려는 상품
     * @param upDowngrade: 구매가 업그레이드 또는 다운그레이드인지, 요금제를 전환하려는 경우에 true
     */
    fun buyBasePlans(tag: String, product: String, upDowngrade: Boolean) {
        val isProductOnDevice = deviceHasGooglePlaySubscription(purchases.value, product)
        if (isProductOnDevice) {
            Timber.d("The user already owns this item: $product")
            return
        }
        val basicSubProductDetails = basicSubProductWithProductDetails.value ?: run {
            Timber.e("Could not find Basic product details.")
            return
        }

        val basicOffers =
            basicSubProductDetails.subscriptionOfferDetails?.let { offerDetailsList ->
                retrieveEligibleOffers(
                    offerDetails = offerDetailsList,
                    tag = tag,
                )
            }

        val offerToken = basicOffers?.let { leastPricedOfferToken(it) }.toString()
        launchFlow(upDowngrade, offerToken, basicSubProductDetails)
    }

    private fun launchFlow(
        upDowngrade: Boolean,
        offerToken: String,
        productDetails: ProductDetails,
    ) {
        val currentSubscriptionPurchaseCount = purchases.value.count {
            it.products.contains(Constants.BASIC_PRODUCT) ||
                it.products.contains(Constants.PREMIUM_PRODUCT)
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
            it.products.contains(Constants.BASIC_PRODUCT) ||
                it.products.contains(Constants.PREMIUM_PRODUCT)
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

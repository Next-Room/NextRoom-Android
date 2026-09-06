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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BillingViewModel
@Inject constructor(
    private val billingClientLifecycle: BillingClientLifecycle,
    billingRepository: BillingRepository,
) : ViewModel() {

    // 사용자의 현재 구독 상품 구매 정보
    private val purchases = billingClientLifecycle.subscriptionPurchases

    // 콘솔에 등록된 상품들 정보
    private val membershipProductDetails = billingClientLifecycle.membershipProductDetails

    private val _buyEvent = MutableSharedFlow<BillingFlowParams>()
    val buyEvent = _buyEvent.asSharedFlow()

    /**
     * 결제 결과는 결제 시트(별도 액티비티)가 닫히는 시점에 도착하는데, 그때 화면이 아직 STOPPED라
     * SharedFlow로 방출하면 구독자가 없어 이벤트가 유실된다. 화면이 다시 STARTED가 될 때까지
     * 큐에 담아두기 위해 Channel을 쓴다.
     */
    private val _uiEvent = Channel<BillingEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            billingClientLifecycle.uiEvent.collect {
                when (it) {
                    BillingClientLifecycle.UIEvent.PurchaseAcknowledged -> {
                        _uiEvent.send(BillingEvent.PurchaseAcknowledged)
                    }

                    BillingClientLifecycle.UIEvent.PurchaseCanceled -> {
                        _uiEvent.send(BillingEvent.PurchaseCanceled)
                    }

                    is BillingClientLifecycle.UIEvent.PurchaseFailed -> {
                        _uiEvent.send(
                            BillingEvent.PurchaseFailed(
                                errorMessage = "${it.responseCode} ${it.debugMessage}",
                            ),
                        )
                    }
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
                                _uiEvent.send(BillingEvent.PurchaseAcknowledged)
                            }
                            .onFailure {
                                _uiEvent.send(BillingEvent.PurchaseFailed(purchaseState = purchase.purchaseState))
                            }
                    } else {
                        _uiEvent.send(BillingEvent.PurchaseFailed(purchaseState = purchase.purchaseState))
                    }
                }
            }
        }
    }

    /**
     * @param productDetails ProductDetails object returned by the library.
     * @param offerToken the least priced offer's offer id token returned by
     * [selectLeastPricedOffer].
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
     * [selectLeastPricedOffer].
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
     * 실패는 예외 대신 [BillingEvent.PurchaseFailed]로 알린다.
     *
     * @param productId: 구매 하려는 상품의 id
     * @param basePlanId: 구매 하려는 base plan의 id. null이면 가장 저렴한 offer를 고른다.
     * @param upDowngrade: 구매가 업그레이드 또는 다운그레이드인지, 요금제를 전환하려는 경우에 true
     */
    fun buyPlans(productId: String, basePlanId: String? = null, upDowngrade: Boolean) {
        viewModelScope.launch {
            val isProductOnDevice = deviceHasGooglePlaySubscription(purchases.value, productId)
            if (isProductOnDevice) {
                Timber.d("The user already owns this item: $productId")
                _uiEvent.send(BillingEvent.PurchaseFailed(errorMessage = ERROR_ALREADY_OWNED))
                return@launch
            }

            // 무료 체험 자격은 구매 이력에 따라 바뀌므로 결제 직전에 offer 목록을 갱신한다.
            // Play 콜백이 유실돼도 화면이 로딩에 갇히지 않도록 상한을 두고, 실패하면 캐시로 진행한다.
            val refreshed = withTimeoutOrNull(PRODUCT_DETAILS_TIMEOUT_MS) {
                billingClientLifecycle.refreshSubscriptionProductDetails()
            }.orEmpty()

            val productDetails = refreshed.firstOrNull { it.productId == productId }
                ?: cachedProductDetails(productId)

            if (productDetails == null) {
                Timber.e("Could not find product details. productId: $productId")
                _uiEvent.send(BillingEvent.PurchaseFailed(errorMessage = ERROR_PRODUCT_DETAILS_NOT_FOUND))
                return@launch
            }

            val offer = productDetails.selectLeastPricedOffer(basePlanId)
            if (offer == null) {
                Timber.e("Could not find a purchasable offer. productId: $productId, basePlanId: $basePlanId")
                _uiEvent.send(BillingEvent.PurchaseFailed(errorMessage = ERROR_OFFER_NOT_FOUND))
                return@launch
            }

            launchFlow(upDowngrade, offer.offerToken, productDetails)
        }
    }

    private fun cachedProductDetails(productId: String): ProductDetails? = when (productId) {
        Constants.MEMBERSHIP_PRODUCT -> membershipProductDetails.value
        else -> null
    }

    private suspend fun launchFlow(
        upDowngrade: Boolean,
        offerToken: String,
        productDetails: ProductDetails,
    ) {
        val currentSubscriptionPurchaseCount = purchases.value.count {
            it.products.contains(Constants.MEMBERSHIP_PRODUCT)
        }
        if (currentSubscriptionPurchaseCount > EXPECTED_SUBSCRIPTION_PURCHASE_LIST_SIZE) {
            // TODO JH: 올바른 purchaseToken을 골라 결제를 이어갈 수 있도록 개선
            Timber.e("There are more than one subscription purchases on the device.")
            _uiEvent.send(BillingEvent.PurchaseFailed(errorMessage = ERROR_MULTIPLE_SUBSCRIPTIONS))
            return
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

        private const val PRODUCT_DETAILS_TIMEOUT_MS = 5_000L

        private const val ERROR_PRODUCT_DETAILS_NOT_FOUND = "Could not find product details."
        private const val ERROR_OFFER_NOT_FOUND = "Could not find a purchasable offer."
        private const val ERROR_ALREADY_OWNED = "The user already owns this item."
        private const val ERROR_MULTIPLE_SUBSCRIPTIONS =
            "There are more than one subscription purchases on the device."
    }
}

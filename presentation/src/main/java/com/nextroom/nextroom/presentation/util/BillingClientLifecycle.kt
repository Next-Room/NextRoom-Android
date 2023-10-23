package com.nextroom.nextroom.presentation.util

import android.app.Activity
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.nextroom.nextroom.presentation.ui.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.pow

class BillingClientLifecycle private constructor(
    private val applicationContext: Context,
    private val externalScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
) : DefaultLifecycleObserver, PurchasesUpdatedListener, BillingClientStateListener,
    ProductDetailsResponseListener, PurchasesResponseListener {

    private val _subscriptionPurchases = MutableStateFlow<List<Purchase>>(emptyList())
    val subscriptionPurchases = _subscriptionPurchases.asStateFlow()


    /**
     * Cached in-app product purchases details.
     */
    private var cachedPurchasesList: List<Purchase>? = null

    /**
     * ProductDetails for all known products.
     */
    val premiumSubProductWithProductDetails = MutableLiveData<ProductDetails?>()
    val basicSubProductWithProductDetails = MutableLiveData<ProductDetails?>()
    val oneTimeProductWithProductDetails = MutableLiveData<ProductDetails?>()

    /**
     * Instantiate a new BillingClient instance.
     */
    private lateinit var billingClient: BillingClient

    override fun onCreate(owner: LifecycleOwner) {
        billingClient = BillingClient.newBuilder(applicationContext)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        if (!billingClient.isReady) {
            billingClient.startConnection(this)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Timber.d("onBillingSetupFinished: $responseCode $debugMessage")
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            querySubscriptionProductDetails()
            querySubscriptionPurchases()
        }
    }

    override fun onBillingServiceDisconnected() {
        Timber.d("onBillingServiceDisconnected")
        // TODO: Try connecting again with exponential backoff.
    }

    /**
     * In order to make purchases, you need the [ProductDetails] for the item or subscription.
     * This is an asynchronous call that will receive a result in [onProductDetailsResponse].
     *
     * querySubscriptionProductDetails uses method calls from GPBL 5.0.0. PBL5, released in May 2022,
     * is backwards compatible with previous versions.
     * To learn more about this you can read:
     * https://developer.android.com/google/play/billing/compatibility
     */
    private fun querySubscriptionProductDetails() {
        val params = QueryProductDetailsParams.newBuilder()

        val productList: MutableList<QueryProductDetailsParams.Product> = arrayListOf()
        for (product in LIST_OF_SUBSCRIPTION_PRODUCTS) {
            productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(product)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )
        }

        params.setProductList(productList).let { productDetailsParams ->
            billingClient.queryProductDetailsAsync(productDetailsParams.build(), this)
        }
    }

    override fun onProductDetailsResponse(
        billingResult: BillingResult,
        productDetailsList: MutableList<ProductDetails>
    ) {
        val response = BillingResponse(billingResult.responseCode)
        val debugMessage = billingResult.debugMessage
        when {
            response.isOk -> {
                processProductDetails(productDetailsList)
            }

            response.isTerribleFailure -> {
                // These response codes are not expected.
                Timber.w("onProductDetailsResponse - Unexpected error: " + response.code + " " + debugMessage)
            }

            else -> {
                Timber.e("onProductDetailsResponse: " + response.code + " " + debugMessage)
            }

        }
    }

    /**
     * This method is used to process the product details list returned by the [BillingClient]and
     * post the details to the [basicSubProductWithProductDetails] and
     * [premiumSubProductWithProductDetails] live data.
     *
     * @param productDetailsList The list of product details.
     *
     */
    private fun processProductDetails(productDetailsList: MutableList<ProductDetails>) {
        val expectedProductDetailsCount = LIST_OF_SUBSCRIPTION_PRODUCTS.size
        if (productDetailsList.isEmpty()) {
            Timber.e("processProductDetails: Expected $expectedProductDetailsCount, Found null ProductDetails. " +
                    "Check to see if the products you requested are correctly published in the Google Play Console.")
            postProductDetails(emptyList())
        } else {
            postProductDetails(productDetailsList)
        }
    }

    private fun postProductDetails(productDetailsList: List<ProductDetails>) {
        productDetailsList.forEach { productDetails ->
            when (productDetails.productType) {
                BillingClient.ProductType.SUBS -> {
                    if (productDetails.productId == Constants.PREMIUM_PRODUCT) {
                        premiumSubProductWithProductDetails.postValue(productDetails)
                    } else if (productDetails.productId == Constants.BASIC_PRODUCT) {
                        basicSubProductWithProductDetails.postValue(productDetails)
                    }
                }
            }
        }
    }

    /**
     * Query Google Play Billing for existing subscription purchases.
     *
     * New purchases will be provided to the PurchasesUpdatedListener.
     * You still need to check the Google Play Billing API to know when purchase tokens are removed.
     */
    private fun querySubscriptionPurchases() {
        if (!billingClient.isReady) {
            Timber.e("querySubscriptionPurchases: BillingClient is not ready")
            billingClient.startConnection(this)
        }
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build(), this
        )
    }

    /**
     * Callback from the billing library when queryPurchasesAsync is called.
     */
    override fun onQueryPurchasesResponse(
        billingResult: BillingResult,
        purchasesList: MutableList<Purchase>
    ) {
        processPurchases(purchasesList)
    }

    /**
     * Called by the Billing Library when new purchases are detected.
     */
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Timber.d("onPurchasesUpdated: $responseCode $debugMessage")
        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases == null) {
                    Timber.d("onPurchasesUpdated: null purchase list")
                    processPurchases(null)
                } else {
                    processPurchases(purchases)
                }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Timber.i("onPurchasesUpdated: User canceled the purchase")
            }

            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Timber.i("onPurchasesUpdated: The user already owns this item")
            }

            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                Timber.e("onPurchasesUpdated: Developer error means that Google Play does "
                        + "not recognize the configuration. If you are just getting started, "
                        + "make sure you have configured the application correctly in the "
                        + "Google Play Console. The product ID must match and the APK you "
                        + "are using must be signed with release keys.")
            }
        }
    }

    /**
     * Send purchase to StateFlow, which will trigger network call to verify the subscriptions
     * on the sever.
     */
    private fun processPurchases(purchasesList: List<Purchase>?) {
        Timber.d("processPurchases: " + purchasesList?.size + " purchase(s)")
        purchasesList?.let { list ->
            if (isUnchangedPurchaseList(list)) {
                Timber.d("processPurchases: Purchase list has not changed")
                return
            }
            externalScope.launch {
                val subscriptionPurchaseList = list.filter { purchase ->
                    purchase.products.any { product ->
                        product in listOf(Constants.PREMIUM_PRODUCT, Constants.BASIC_PRODUCT)
                    }
                }

                _subscriptionPurchases.emit(subscriptionPurchaseList)
            }
            logAcknowledgementStatus(list)
        }
    }

    /**
     * Check whether the purchases have changed before posting changes.
     */
    private fun isUnchangedPurchaseList(purchasesList: List<Purchase>): Boolean {
        val isUnchanged = purchasesList == cachedPurchasesList
        if (!isUnchanged) {
            cachedPurchasesList = purchasesList
        }
        return isUnchanged
    }

    /**
     * Log the number of purchases that are acknowledge and not acknowledged.
     *
     * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
     *
     * When the purchase is first received, it will not be acknowledge.
     * This application sends the purchase token to the server for registration. After the
     * purchase token is registered to an account, the Android app acknowledges the purchase token.
     * The next time the purchase list is updated, it will contain acknowledged purchases.
     */
    private fun logAcknowledgementStatus(purchasesList: List<Purchase>) {
        var acknowledgedCounter = 0
        var unacknowledgedCounter = 0
        for (purchase in purchasesList) {
            if (purchase.isAcknowledged) {
                acknowledgedCounter++
            } else {
                unacknowledgedCounter++
            }
        }
        Timber.d("logAcknowledgementStatus: acknowledged=$acknowledgedCounter unacknowledged=$unacknowledgedCounter")
    }

    /**
     * Launching the billing flow.
     *
     * Launching the UI to make a purchase requires a reference to the Activity.
     */
    fun launchBillingFlow(activity: Activity, params: BillingFlowParams): Int {
        if (!billingClient.isReady) {
            Timber.e("launchBillingFlow: BillingClient is not ready")
        }
        val billingResult = billingClient.launchBillingFlow(activity, params)
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Timber.d("launchBillingFlow: BillingResponse $responseCode $debugMessage")
        return responseCode
    }

    /**
     * Acknowledge a purchase.
     *
     * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
     *
     * Apps should acknowledge the purchase after confirming that the purchase token
     * has been associated with a user. This app only acknowledges purchases after
     * successfully receiving the subscription data back from the server.
     *
     * Developers can choose to acknowledge purchases from a server using the
     * Google Play Developer API. The server has direct access to the user database,
     * so using the Google Play Developer API for acknowledgement might be more reliable.
     * TODO(134506821): Acknowledge purchases on the server.
     * TODO: Remove client side purchase acknowledgement after removing the associated tests.
     * If the purchase token is not acknowledged within 3 days,
     * then Google Play will automatically refund and revoke the purchase.
     * This behavior helps ensure that users are not charged for subscriptions unless the
     * user has successfully received access to the content.
     * This eliminates a category of issues where users complain to developers
     * that they paid for something that the app is not giving to them.
     */
    suspend fun acknowledgePurchase(purchaseToken: String): Boolean {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        for (trial in 1..MAX_RETRY_ATTEMPT) {
            var response = BillingResponse(500)
            var bResult: BillingResult? = null
            billingClient.acknowledgePurchase(params) { billingResult ->
                response = BillingResponse(billingResult.responseCode)
                bResult = billingResult
            }

            when {
                response.isOk -> {
                    Timber.i("Acknowledge success - token: $purchaseToken")
                    return true
                }

                response.canFailGracefully -> {
                    // Ignore the error
                    Timber.i("Token $purchaseToken is already owned.")
                    return true
                }

                response.isRecoverableError -> {
                    // Retry to ack because these errors may be recoverable.
                    val duration = 500L * 2.0.pow(trial).toLong()
                    delay(duration)
                    if (trial < MAX_RETRY_ATTEMPT) {
                        Timber.w("Retrying(" + trial + ") to acknowledge for token "
                                + purchaseToken
                                + " - "
                                + "code: "
                                + bResult!!.responseCode
                                + ", message: "
                                + bResult!!.debugMessage)
                    }
                }

                response.isNonrecoverableError || response.isTerribleFailure -> {
                    Timber.e("Failed to acknowledge for token "
                            + purchaseToken
                            + " - "
                            + "code: "
                            + bResult!!.responseCode
                            + ", message: "
                            + bResult!!.debugMessage)
                    break
                }
            }
        }
        throw Exception("Failed to acknowledge the purchase!")
    }

    companion object {
        private const val TAG = "BillingLifecycle"
        private const val MAX_RETRY_ATTEMPT = 3

        private val LIST_OF_SUBSCRIPTION_PRODUCTS = listOf(
            Constants.BASIC_PRODUCT, Constants.PREMIUM_PRODUCT
        )

        @Volatile
        private var INSTANCE: BillingClientLifecycle? = null

        fun getInstance(applicationContext: Context): BillingClientLifecycle =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: BillingClientLifecycle(applicationContext).also { INSTANCE = it }
            }
    }
}

@JvmInline
private value class BillingResponse(val code: Int) {
    val isOk: Boolean
        get() = code == BillingClient.BillingResponseCode.OK
    val canFailGracefully: Boolean
        get() = code == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED
    val isRecoverableError: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.ERROR,
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
        )
    val isNonrecoverableError: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE,
            BillingClient.BillingResponseCode.DEVELOPER_ERROR,
        )
    val isTerribleFailure: Boolean
        get() = code in setOf(
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED,
            BillingClient.BillingResponseCode.USER_CANCELED,
        )
}
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.pow

class BillingClientLifecycle private constructor(
    private val applicationContext: Context,
    private val externalScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default),
) : DefaultLifecycleObserver,
    /*
     * PurchasesUpdatedListener: 결제 상태가 업데이트될 때 감지
     * BillingClientStateListener: Billing Client의 상태 변화 감지
     * ProductDetailsResponseListener: 상품 정보를 검색하고 수신
     * PurchasesResponseListener: 사용자의 인앱 구매 및 구독 상태 감지
     */
    PurchasesUpdatedListener,
    BillingClientStateListener,
    ProductDetailsResponseListener,
    PurchasesResponseListener {

    // 사용자의 현재 구독 상품 구매 정보
    private val _subscriptionPurchases = MutableStateFlow<List<Purchase>>(emptyList())
    val subscriptionPurchases = _subscriptionPurchases.asStateFlow()

    // 상품 정보 캐싱
    private var cachedPurchasesList: List<Purchase>? = null

    // 콘솔에 등록된 상품들 정보
    val largeSubProductWithProductDetails = MutableLiveData<ProductDetails?>()
    val mediumSubProductWithProductDetails = MutableLiveData<ProductDetails?>()
    val miniSubProductWithProductDetails = MutableLiveData<ProductDetails?>()

    private val _uiEvent = MutableSharedFlow<UIEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    private lateinit var billingClient: BillingClient

    // onCreate때 billingClient를 생성하고 연결
    override fun onCreate(owner: LifecycleOwner) {
        billingClient = BillingClient.newBuilder(applicationContext)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        if (!billingClient.isReady) {
            billingClient.startConnection(this)
        }
    }

    // onDestroy때 billingClient 연결 해제
    override fun onDestroy(owner: LifecycleOwner) {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    /*
     * onBillingSetupFinished: billingClient 연결이 완료되면 호출됨
     * querySubscriptionProductDetails: 구독 상품 정보 조회
     * querySubscriptionPurchases: 사용자의 현재 구독 상품 구매 내역을 조회
     */
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
    }

    /**
     * 구매를 하려면 일회성 결제 또는 구독을 위한 [ProductDetails]가 필요
     * 상품 정보 조회가 완료되면 [onProductDetailsResponse]가 호출됨
     *
     * queryProductDetailsAsync: 상품 정보 조회
     */
    private fun querySubscriptionProductDetails() {
        val params = QueryProductDetailsParams.newBuilder()

        val productList: MutableList<QueryProductDetailsParams.Product> = arrayListOf()
        for (product in LIST_OF_SUBSCRIPTION_PRODUCTS) {
            productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(product)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
            )
        }

        params.setProductList(productList).let { productDetailsParams ->
            billingClient.queryProductDetailsAsync(productDetailsParams.build(), this)
        }
    }

    // 상품 정보 조회 완료시 호출됨
    override fun onProductDetailsResponse(
        billingResult: BillingResult,
        productDetailsList: MutableList<ProductDetails>,
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

    private fun processProductDetails(productDetailsList: MutableList<ProductDetails>) {
        val expectedProductDetailsCount = LIST_OF_SUBSCRIPTION_PRODUCTS.size
        if (productDetailsList.isEmpty()) {
            Timber.e(
                "processProductDetails: Expected $expectedProductDetailsCount, Found null ProductDetails. " +
                    "Check to see if the products you requested are correctly published in the Google Play Console.",
            )
            postProductDetails(emptyList())
        } else {
            postProductDetails(productDetailsList)
        }
    }

    // 구글 콘솔에서 받아온 상품 정보를 저장
    private fun postProductDetails(productDetailsList: List<ProductDetails>) {
        productDetailsList.forEach { productDetails ->
            when (productDetails.productType) {
                BillingClient.ProductType.SUBS -> {
                    when (productDetails.productId) {
                        Constants.LARGE_PRODUCT -> largeSubProductWithProductDetails.postValue(productDetails)
                        Constants.MEDIUM_PRODUCT -> mediumSubProductWithProductDetails.postValue(productDetails)
                        Constants.MINI_PRODUCT -> miniSubProductWithProductDetails.postValue(productDetails)
                    }
                }
            }
        }
    }

    // 사용자의 현재 구독 상품 구매 내역을 조회
    private fun querySubscriptionPurchases() {
        if (!billingClient.isReady) {
            Timber.e("querySubscriptionPurchases: BillingClient is not ready")
            billingClient.startConnection(this)
        } else {
            billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build(),
                this,
            )
        }
    }

    // queryPurchasesAsync를 호출하여 사용자의 구독 상품 구매 내역을 조회가 끝나면 onQueryPurchasesResponse가 호출됨
    override fun onQueryPurchasesResponse(
        billingResult: BillingResult,
        purchasesList: MutableList<Purchase>,
    ) {
        processPurchases(purchasesList)
    }

    // 새로운 구매가 감지 되었을 때 콜백
    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?,
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
                Timber.e(
                    "onPurchasesUpdated: Developer error means that Google Play does " +
                        "not recognize the configuration. If you are just getting started, " +
                        "make sure you have configured the application correctly in the " +
                        "Google Play Console. The product ID must match and the APK you " +
                        "are using must be signed with release keys.",
                )
            }
        }
    }

    /**
     * stateFlow로 사용자가 구매한 상품 목록을 방출
     * observe 하는 곳에서 구독에 대한 자격 부여 처리(구글에 전송)
     */
    private fun processPurchases(purchasesList: List<Purchase>?) {
        purchasesList?.let { list ->
            if (isUnchangedPurchaseList(list)) {
                Timber.d("processPurchases: Purchase list has not changed")
                return
            }
            externalScope.launch {
                val subscriptionPurchaseList = list.filter { purchase ->
                    purchase.products.any { product ->
                        product in listOf(Constants.LARGE_PRODUCT, Constants.MEDIUM_PRODUCT, Constants.MINI_PRODUCT)
                    }
                }

                _subscriptionPurchases.emit(subscriptionPurchaseList)
            }
            logAcknowledgementStatus(list)
        }
    }

    // 상품 구매 내용이 이전과 다른지 체크
    private fun isUnchangedPurchaseList(purchasesList: List<Purchase>): Boolean {
        val isUnchanged = purchasesList == cachedPurchasesList
        if (!isUnchanged) {
            cachedPurchasesList = purchasesList
        }
        return isUnchanged
    }

    /**
     * 조회한 구매 목록 중에서 어떤 구매가 이미 확인(acknowledged)되었고
     * 아직 확인되지 않은(unacknowledged) 구매가 있는지를 로그로 출력
     *
     * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
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

    // 결제 플로우 실행
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

    // 구매 승인
    suspend fun acknowledgePurchase(purchaseToken: String): Boolean = suspendCoroutine { continuation ->
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()

        val maxRetryAttempt = MAX_RETRY_ATTEMPT
        var currentTrial = 0

        fun acknowledge() {
            billingClient.acknowledgePurchase(params) { billingResult ->
                val response = BillingResponse(billingResult.responseCode)

                when {
                    response.isOk -> {
                        Timber.i("Acknowledge success - token: $purchaseToken")
                        externalScope.launch {
                            _uiEvent.emit(UIEvent.PurchaseAcknowledged)
                        }
                        continuation.resume(true)
                    }

                    response.canFailGracefully -> {
                        Timber.i("Token $purchaseToken is already owned.")
                        continuation.resume(true)
                    }

                    response.isRecoverableError -> {
                        if (currentTrial < maxRetryAttempt) {
                            val duration = 500L * 2.0.pow(currentTrial).toLong()
                            Timber.w(
                                "Retrying($currentTrial) to acknowledge for token $purchaseToken - " +
                                    "code: ${billingResult.responseCode}, message: ${billingResult.debugMessage}",
                            )
                            externalScope.launch {
                                delay(duration)
                                currentTrial++
                                acknowledge()
                            }
                        } else {
                            Timber.e(
                                "Failed to acknowledge for token $purchaseToken - " +
                                    "code: ${billingResult.responseCode}, message: ${billingResult.debugMessage}",
                            )
                            continuation.resume(false)
                        }
                    }

                    response.isNonrecoverableError || response.isTerribleFailure -> {
                        Timber.e(
                            "Failed to acknowledge for token $purchaseToken - " +
                                "code: ${billingResult.responseCode}, message: ${billingResult.debugMessage}",
                        )
                        continuation.resume(false)
                    }
                }
            }
        }

        acknowledge()
    }

    sealed interface UIEvent {
        data object PurchaseAcknowledged : UIEvent
    }

    companion object {
        private const val TAG = "BillingLifecycle"
        private const val MAX_RETRY_ATTEMPT = 3

        private val LIST_OF_SUBSCRIPTION_PRODUCTS = listOf(
            Constants.MINI_PRODUCT,
            Constants.MEDIUM_PRODUCT,
            Constants.LARGE_PRODUCT,
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

package com.pramod.eyecare.framework.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.pramod.eyecare.BuildConfig
import com.pramod.eyecare.framework.ui.utils.Security
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

interface BillingHelper {

    fun setup(productIds: List<String>)

    suspend fun purchase(activity: Activity, productId: String)

    suspend fun isPurchased(productId: String): Boolean

    fun clear()

    fun addBillingListener(billingListener: BillingListener)

    fun removeBillingListener(billingListener: BillingListener)

    fun addPurchaseListener(purchaseListener: PurchaseListener)

    fun removePurchaseListener(purchaseListener: PurchaseListener)
}

class BillingHelperImpl @Inject constructor(@ApplicationContext context: Context) :
    BillingListenerHandler(),
    PurchasesUpdatedListener,
    BillingClientStateListener,
    BillingHelper {

    companion object {
        private const val TAG = "BillingHelperNew"
    }

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    //billing client
    private val mBillingClient: BillingClient = BillingClient
        .newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    //cache purchases
    private val mPurchases = mutableMapOf<String, Purchase>()

    private var mProductIds: List<String> = emptyList()

    private var mProductDetailList: List<ProductDetails> = emptyList()

    private var connectionRetryLimit = 3

    private var connectionRetryMadeCount = 0

    override fun onBillingSetupFinished(p0: BillingResult) {
        Timber.tag(TAG).d("onBillingSetupFinished: ")
        coroutineScope.launch {
            if (p0.responseCode.isBillingResultOk()) {
                emitBillingInitialized()
                val products = queryProducts()
                Timber.tag(TAG).d("Total Products:%s", products.size)
                mProductDetailList = products
                emitAvailableProductDetails(products)
                val purchasesList = queryPurchases()
                processPurchases(purchasesList, true)
            }
        }

    }

    override fun onBillingServiceDisconnected() {
        emitBillingClientError("Billing service is disconnected!")
        if (connectionRetryMadeCount < connectionRetryLimit) {
            connectionRetryMadeCount++
            Timber.tag(TAG).d("Retrying to connect: $connectionRetryMadeCount")
            setup(mProductIds)
        } else {
            Timber.tag(TAG).d("Retry limit exhausted!: $connectionRetryMadeCount")
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult, purchaseList: MutableList<Purchase>?
    ) {
        coroutineScope.launch {
            when (billingResult.responseCode) {
                BillingResponseCode.OK -> {
                    purchaseList?.let { processPurchases(it, false) }
                }
                BillingResponseCode.ITEM_ALREADY_OWNED -> {
                    purchaseList?.let { processPurchases(it, true) }
                }
                BillingResponseCode.USER_CANCELED -> {
                    emitPurchaseError("Purchase wasn't made, you can try again now or later. Thank you :)")
                }
                else -> {
                    emitPurchaseError(billingResult.debugMessage)
                }
            }
        }
    }

    override suspend fun isPurchased(productId: String): Boolean {
        return withContext(Dispatchers.IO) { queryPurchases().any { it.products.firstOrNull() == productId } }
    }

    private suspend fun isPurchasePending(productID: String): Boolean {
        return withContext(Dispatchers.IO) {
            queryPurchases().find {
                productID == it.products.firstOrNull()
            }?.purchaseState == Purchase.PurchaseState.PENDING
        }
    }

    override fun setup(productIds: List<String>) {
        mProductIds = productIds
        if (!mBillingClient.isReady) mBillingClient.startConnection(this)
    }


    override suspend fun purchase(activity: Activity, productId: String) {
        if (mBillingClient.isReady) {
            when {
                isPurchased(productId) -> {
                    if (isPurchasePending(productId)) {
                        Timber.tag(TAG).i("buy: purchase pending")
                        emitPurchaseError("Please wait your purchase is under process, check on this after some time.")
                    } else {
                        Timber.tag(TAG).i("buy: already purchased")
                        emitPurchaseError("You have already donated this item, Thank you so much ❤")
                    }
                }
                else -> {
                    Timber.tag(TAG).i("buy: start purchasing")
                    val products = queryProducts()
                    if (products.isEmpty()) {
                        Timber.tag(TAG).i("buy: no product to buy")
                        emitPurchaseError("Umm something is wrong,there's no product to buy right now!")
                    } else {
                        queryProducts().find { it.productId == productId }?.let { productDetails ->
                            Timber.tag(TAG)
                                .i("buy: start purchasing: productDetails:$productDetails")
                            mBillingClient.launchBillingFlow(
                                activity,
                                BillingFlowParams.newBuilder().setProductDetailsParamsList(
                                    listOf(
                                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                            .setProductDetails(productDetails).setOfferToken(
                                                productDetails.oneTimePurchaseOfferDetails?.zza()
                                                    .orEmpty()
                                            ).build()
                                    )
                                ).build()
                            )
                        }
                    }
                }
            }
        } else {
            Timber.tag(TAG).d("Billing is not ready")
        }
    }

    override fun clear() {
        removeListeners()
        mBillingClient.endConnection()
        coroutineScope.coroutineContext.cancelChildren()
    }

    private suspend fun queryProducts(): List<ProductDetails> {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(mProductIds.map {
                Product.newBuilder().setProductId(it)
                    .setProductType(ProductType.INAPP).build()
            })
        val result =
            withContext(Dispatchers.IO) { mBillingClient.queryProductDetails(params.build()) }
        return if (result.billingResult.responseCode == BillingResponseCode.OK) {
            result.productDetailsList.orEmpty()
        } else {
            //product not available
            listOf()
        }
    }

    private suspend fun queryPurchases(): List<Purchase> {
        val result = withContext(Dispatchers.IO) {
            mBillingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(ProductType.INAPP).build()
            )
        }
        return if (result.billingResult.responseCode.isBillingResultOk()) {
            result.purchasesList
        } else {
            listOf()
        }
    }

    private suspend fun processPurchases(
        purchases: List<Purchase>, isRestored: Boolean
    ) {
        for (purchase in purchases) {

            //storing purchase in cache
            mPurchases[purchase.purchaseToken] = purchase

            when (purchase.purchaseState) {
                Purchase.PurchaseState.PURCHASED -> {
                    if (!isValidSignature(purchase.originalJson, purchase.signature)) {
                        // Invalid purchase
                        // show error to user
                        Timber.i("processPurchase: invalid purchase")
                        continue
                    }
                    // else purchase is valid
                    //if item is purchased and not acknowledged
                    if (!purchase.isAcknowledged) {
                        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken).build()
                        val result = mBillingClient.acknowledgePurchase(acknowledgePurchaseParams)
                        if (result.responseCode.isBillingResultOk()) {
                            //if purchase is acknowledged Grant entitlement to the user
                            purchased(purchase.products.first(), isRestored)
                        }
                    } else {
                        // Grant entitlement to the user on item purchase
                        purchased(purchase.products.first(), isRestored)
                    }
                }
                Purchase.PurchaseState.PENDING -> {
                    purchasePending(purchase.skus.first())
                }
                else -> {
                    Timber.e("processPurchase: Purchase State: ${purchase.purchaseState}")
                }
            }

        }
    }

    private fun Int.isBillingResultOk(): Boolean = this == BillingResponseCode.OK

    private fun isValidSignature(signedData: String, signature: String): Boolean {
        return try {
            val base64Key = BuildConfig.GOOGLE_IN_APP_RSA_KEY
            Security.verifyPurchase(base64Key, signedData, signature)
        } catch (e: IOException) {
            false
        }
    }
}


interface BillingListener {

    //this method is invoked when billing client is ready to use
    fun onBillingInitialized()

    //if there any error in billing client this method will be invoked
    fun onBillingError(message: String)

    //called when list of sku details is available
    fun onBillingSkuDetailsAvailable(productDetailList: List<ProductDetails>)

}

interface PurchaseListener : BillingListener {

    fun onPurchased(sku: String)

    fun onPurchasedRestored(sku: String)

    fun onPurchasePending(sku: String)

    fun onPurchaseError(message: String)

}

abstract class BillingListenerHandler {

    private val billingListeners = mutableSetOf<BillingListener>()

    private val purchaseListeners = mutableSetOf<PurchaseListener>()

    fun addBillingListener(billingListener: BillingListener) {
        billingListeners.add(billingListener)
    }

    fun removeBillingListener(billingListener: BillingListener) {
        billingListeners.remove(billingListener)
    }

    fun addPurchaseListener(purchaseListener: PurchaseListener) {
        purchaseListeners.add(purchaseListener)
    }

    fun removePurchaseListener(purchaseListener: PurchaseListener) {
        purchaseListeners.remove(purchaseListener)
    }

    fun purchased(sku: String, isRestored: Boolean) {
        purchaseListeners.forEach {
            if (isRestored) it.onPurchasedRestored(sku)
            else it.onPurchased(sku)
        }
    }

    fun purchasePending(sku: String) {
        purchaseListeners.forEach {
            it.onPurchasePending(sku)
        }
    }

    fun emitPurchaseError(message: String) {
        purchaseListeners.forEach {
            it.onPurchaseError(message)
        }
    }

    fun emitBillingClientError(message: String) {
        billingListeners.forEach {
            it.onBillingError(message)
        }
        purchaseListeners.forEach {
            it.onBillingError(message)
        }
    }

    fun emitBillingInitialized() {
        billingListeners.forEach {
            it.onBillingInitialized()
        }
        purchaseListeners.forEach {
            it.onBillingInitialized()
        }
    }

    fun emitAvailableProductDetails(skuDetailsList: List<ProductDetails>) {
        billingListeners.forEach {
            it.onBillingSkuDetailsAvailable(skuDetailsList)
        }
        purchaseListeners.forEach {
            it.onBillingSkuDetailsAvailable(skuDetailsList)
        }
    }

    fun removeListeners() {
        billingListeners.clear()
        purchaseListeners.clear()
    }

}

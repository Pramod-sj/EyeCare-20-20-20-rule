package com.pramod.eyecare.framework.ui.fragment.donate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.SkuDetails
import com.pramod.eyecare.business.interactor.GetDonationItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DonateViewModel @Inject constructor(
    private val getDonationItems: GetDonationItems
) : ViewModel() {

    private val _donateItemList = MutableLiveData<List<DonateItem>>()
    val donateItemList: LiveData<List<DonateItem>> = _donateItemList

    private fun fetchDonateItems() {
        viewModelScope.launch {
            _donateItemList.value = getDonationItems()
        }
    }

    fun updateDonateItemStatus(sku: String, donateState: DonateItemState) {
        val current = _donateItemList.value?.let { ArrayList(it) } ?: arrayListOf<DonateItem>()

        val immutableCurrent = _donateItemList.value?.let { ArrayList(it) } ?: listOf<DonateItem>()

        for (donateItem in immutableCurrent) {
            if (donateItem.itemProductId == sku) {
                current[immutableCurrent.indexOf(donateItem)] =
                    DonateItem(
                        donateItem.itemProductId,
                        donateItem.drawableId,
                        donateItem.title,
                        donateItem.amount,
                        donateItem.color,
                        donateState
                    )
            }
        }

        _donateItemList.value = current
    }

    fun updateDonateItemPrice(skuDetailsList: List<ProductDetails>) {

        val current = _donateItemList.value?.let { ArrayList(it) } ?: mutableListOf<DonateItem>()

        val immutableCurrent = _donateItemList.value?.let { ArrayList(it) } ?: listOf<DonateItem>()

        for (skuDetails in skuDetailsList) {
            immutableCurrent.find { skuDetails.productId == it.itemProductId }?.let {
                current[immutableCurrent.indexOf(it)] =
                    DonateItem(
                        it.itemProductId,
                        it.drawableId,
                        it.title,
                        skuDetails.zza(),
                        it.color,
                        it.donateItemState
                    )
            }
        }

        _donateItemList.value = current
    }

    init {
        fetchDonateItems()
    }
}
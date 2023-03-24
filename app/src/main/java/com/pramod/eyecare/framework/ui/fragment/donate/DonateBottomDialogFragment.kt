package com.pramod.eyecare.framework.ui.fragment.donate

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.SkuDetails
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.pramod.eyecare.R
import com.pramod.eyecare.databinding.DialogDonateBinding
import com.pramod.eyecare.framework.billing.BillingHelper
import com.pramod.eyecare.framework.billing.PurchaseListener
import com.pramod.eyecare.framework.ui.common.BaseBottomSheetDialogFragment
import com.pramod.eyecare.framework.ui.utils.doWithInset
import com.pramod.eyecare.framework.ui.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DonateBottomDialogFragment :
    BaseBottomSheetDialogFragment(R.layout.dialog_donate), PurchaseListener {

    private val binding by viewBinding<DialogDonateBinding>()

    private val viewModel: DonateViewModel by viewModels()

    @Inject
    lateinit var billingHelper: BillingHelper

    override fun getBottomSheetBehaviorView(): View {
        return binding.cardBottomSheet
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            FirebaseAnalytics.getInstance(requireContext()).logEvent(
                FirebaseAnalytics.Event.SCREEN_VIEW,
                bundleOf(FirebaseAnalytics.Param.SCREEN_NAME to DonateBottomDialogFragment::class.simpleName)
            )
        }
        setUpBillingHelper()
        applyBottomInsetToScrollView()
        setUpAdapter()
    }

    private fun setUpBillingHelper() {
        viewModel.donateItemList.observe(viewLifecycleOwner) { donateItems ->
            billingHelper.setup(productIds = donateItems.map { it.itemProductId })
        }
        billingHelper.addPurchaseListener(this)
    }

    private fun setUpAdapter() {
        val adapter = DonateItemAdapter { i: Int, donateItem: DonateItem ->
            lifecycleScope.launch {
                billingHelper.purchase(requireActivity(), donateItem.itemProductId)
            }
        }
        binding.donateRecyclerView.adapter = adapter
        viewModel.donateItemList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun applyBottomInsetToScrollView() {
        binding.nestedScrollView.doWithInset { view, top, bottom ->
            binding.nestedScrollView.updatePadding(bottom = bottom)
        }
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        super.onStateChanged(bottomSheet, newState)
        //make bottom sheet dialog draggable when scroll view is not scroll
        bottomSheetBehavior.isDraggable =
            !binding.nestedScrollView.canScrollVertically(-1)
    }

    override fun onPurchased(sku: String) {
        viewModel.updateDonateItemStatus(sku, DonateItemState.PURCHASED)
        binding.coordinator.showSnackBar("Thank you so much ❤")
        //setting has donated to true
        //prefManager.setHasDonated(true)
    }

    override fun onPurchasedRestored(sku: String) {
        viewModel.updateDonateItemStatus(sku, DonateItemState.PURCHASED)
        //setting has donated to true
        //prefManager.setHasDonated(true)
    }

    override fun onPurchasePending(sku: String) {
        viewModel.updateDonateItemStatus(sku, DonateItemState.PURCHASE_IN_PROCESS)
        binding.coordinator.showSnackBar("Thank you so much ❤, your purchase is under process.")
    }

    override fun onPurchaseError(message: String) {
        binding.coordinator.showSnackBar(message)
    }

    override fun onBillingInitialized() {
        Timber.i("onBillingInitialized: ")
    }

    override fun onBillingError(message: String) {
        binding.coordinator.showSnackBar(message)
    }

    override fun onBillingSkuDetailsAvailable(productDetailList: List<ProductDetails>) {
        viewModel.updateDonateItemPrice(productDetailList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        billingHelper.clear()
    }

    companion object {

        private val TAG = DonateBottomDialogFragment::class.java.simpleName

        fun show(supportFragmentManager: FragmentManager) {
            DonateBottomDialogFragment().show(supportFragmentManager, TAG)
        }
    }
}

fun CoordinatorLayout.showSnackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(
        /* view = */ this,
        /* text = */ message,
        /* duration = */ duration
    ).show()

}
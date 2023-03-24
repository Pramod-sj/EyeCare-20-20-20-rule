package com.pramod.eyecare.framework.ui.fragment.donate

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.pramod.eyecare.R

enum class DonateItemState {
    NOT_PURCHASED,
    PURCHASE_IN_PROCESS,
    PURCHASED
}

data class DonateItem(
    val itemProductId: String,
    val drawableId: Int,
    val title: String,
    val amount: String,
    val color: Int,
    val donateItemState: DonateItemState = DonateItemState.NOT_PURCHASED
) {
    fun getDonateStateIcon(context: Context): Drawable? {
        return when (donateItemState) {
            DonateItemState.NOT_PURCHASED -> {
                null
            }
            DonateItemState.PURCHASE_IN_PROCESS -> {
                ContextCompat.getDrawable(context, R.drawable.ic_donation_in_process)
            }
            DonateItemState.PURCHASED -> {
                ContextCompat.getDrawable(context, R.drawable.ic_donation_made)
            }
        }
    }

    fun isDonateItemPurchased() = donateItemState != DonateItemState.NOT_PURCHASED

}
package com.pramod.eyecare.business.interactor

import com.pramod.eyecare.R
import com.pramod.eyecare.framework.ui.fragment.donate.DonateItem
import javax.inject.Inject

class GetDonationItems @Inject constructor(

) {

    operator fun invoke(): List<DonateItem> {
        return arrayListOf(
            DonateItem(
                "cookie_new_",
                R.drawable.ic_donate_cookie,
                "Buy me cookies",
                "₹30.00",
                R.color.color_cookie
            ),
            DonateItem(
                "coffee_new_",
                R.drawable.ic_donate_coffee,
                "Buy me a cup of coffee",
                "₹60.00",
                R.color.color_coffee
            ),
            DonateItem(
                "snacks_",
                R.drawable.ic_snacks,
                "Buy me snacks",
                "₹150.00",
                R.color.color_snacks
            ),
            DonateItem(
                "movie_",
                R.drawable.ic_donate_movies,
                "Buy movie ticket for me",
                "₹200.00",
                R.color.color_movie
            ),
            DonateItem(
                "meal_",
                R.drawable.ic_donate_meal,
                "Buy meal for me",
                "₹350.00",
                R.color.color_meal
            ),
            DonateItem(
                "gift_new_",
                R.drawable.ic_donate_giftcard,
                "Buy me a gift",
                "₹750.00",
                R.color.color_gift
            )
        )
    }

}
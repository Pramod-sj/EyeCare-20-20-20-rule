package com.pramod.eyecare.framework.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.pramod.eyecare.R


fun Fragment.openWebsite(url: String) {
    requireContext().openWebsite(url)
}

fun Context.openWebsite(url: String) {
    val builder = CustomTabsIntent.Builder()
    builder.setShowTitle(false)
    builder.setUrlBarHidingEnabled(true)
    builder.setStartAnimations(
        this, R.anim.slide_in_right, R.anim.slide_out_left
    )
    builder.setExitAnimations(
        this, android.R.anim.slide_in_left, android.R.anim.slide_out_right
    )
    val customTabsIntent = builder.build()
    try {
        customTabsIntent.launchUrl(this, Uri.parse(url))
    } catch (e: Exception) {
        e.printStackTrace()
        val webpageUri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpageUri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No browser application found", Toast.LENGTH_SHORT).show()
        }
    }
}
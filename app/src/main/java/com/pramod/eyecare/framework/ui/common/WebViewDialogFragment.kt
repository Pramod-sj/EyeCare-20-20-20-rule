package com.pramod.eyecare.framework.ui.common

import android.os.Bundle
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.pramod.eyecare.R
import com.pramod.eyecare.databinding.DialogWebviewLayoutBinding
import com.pramod.eyecare.framework.ui.utils.isNetworkActive
import com.pramod.eyecare.framework.ui.utils.viewBinding

class WebViewDialogFragment :
    BaseBottomSheetDialogFragment(R.layout.dialog_webview_layout) {

    private val binding by viewBinding<DialogWebviewLayoutBinding>()

    override fun getBottomSheetBehaviorView(): View {
        return binding.cardBottomSheet
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isLight) {
            try {
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    WebSettingsCompat.setForceDark(
                        binding.webView.settings,
                        WebSettingsCompat.FORCE_DARK_ON
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.txtViewAppBar.text = arguments?.getString(EXTRA_DIALOG_TITLE)
        arguments?.getString(EXTRA_WEB_PAGE_URL)?.let {
            binding.webView.loadUrl(it)
        }
        binding.webView.webViewClient = object : WebViewClient() {

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?,
            ) {
                super.onReceivedError(view, request, error)
                binding.webView.isVisible = false
                binding.txtViewErrorMessage.isVisible = true
                if (!requireContext().isNetworkActive()) {
                    binding.txtViewErrorMessage.text =
                        "Please connect to an active internet connection!"
                } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    binding.txtViewErrorMessage.text = error?.description
                } else {
                    binding.txtViewErrorMessage.text = "Something went wrong!"
                }
            }
        }
        detectWebViewScroll()
    }

    private fun detectWebViewScroll() {
        binding.webView.onScrollChangedCallback =
            object : ObservableWebView.OnScrollChangeListener {
                override fun onScrollChanged(
                    currentHorizontalScroll: Int,
                    currentVerticalScroll: Int,
                    oldHorizontalScroll: Int,
                    oldcurrentVerticalScroll: Int,
                ) {
                    bottomSheetBehavior.isDraggable = !binding.webView.canScrollVertically(-1)
                }

            }
    }

    override fun onStateChanged(bottomSheet: View, newState: Int) {
        super.onStateChanged(bottomSheet, newState)
        bottomSheetBehavior.isDraggable = !binding.webView.canScrollVertically(-1)
    }

    companion object {
        val TAG = WebViewDialogFragment::class.java.simpleName
        private const val EXTRA_DIALOG_TITLE = "title"
        private const val EXTRA_WEB_PAGE_URL = "url"

        fun show(title: String, url: String, fragmentManager: FragmentManager) {
            val dialog = WebViewDialogFragment()
                .apply {
                    arguments = bundleOf(
                        EXTRA_DIALOG_TITLE to title,
                        EXTRA_WEB_PAGE_URL to url
                    )
                }
            dialog.show(fragmentManager, TAG)
        }
    }

}
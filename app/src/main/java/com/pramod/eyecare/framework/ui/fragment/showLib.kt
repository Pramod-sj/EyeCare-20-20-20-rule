package com.pramod.eyecare.framework.ui.fragment

import android.text.method.LinkMovementMethod
import android.view.WindowManager
import android.widget.AbsListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pramod.eyecare.R

fun AlertDialog.applyStyleOnAlertDialog() {
    window?.let { window ->
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.setDimAmount(0.75f)
        window.setWindowAnimations(R.style.DialogWindowAnimation)
    }
}

fun Fragment.showOpensourceLibLicense() {
    val builder = MaterialAlertDialogBuilder(requireContext())
        .setTitle("Open source libraries")
        .setItems(R.array.libraries_name, null)

    val alertDialog = builder.create()
    alertDialog.applyStyleOnAlertDialog()
    alertDialog.setOnShowListener {
        alertDialog.listView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                for (i in 0 until visibleItemCount) {
                    val textView: TextView = alertDialog.listView[i].findViewById(android.R.id.text1)
                    textView.linksClickable = true
                    textView.movementMethod = LinkMovementMethod.getInstance()
                    textView.background = null
                }
            }

            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

            }

        })
    }
    alertDialog.show()

}
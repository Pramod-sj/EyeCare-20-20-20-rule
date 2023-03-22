package com.pramod.eyecare.framework.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import com.google.android.material.materialswitch.MaterialSwitch
import com.pramod.eyecare.R
import com.pramod.eyecare.databinding.CustomItemLayoutBinding
import com.pramod.eyecare.framework.ui.utils.getColorFromAttr

class ITSCustomLayout
    (context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var customItsLayoutBinding: CustomItemLayoutBinding

    val switch: MaterialSwitch
        get() = customItsLayoutBinding.tailSwitch

    @DrawableRes
    var icon = -1
        set(value) {
            field = value
            setIcon(tint = iconTintColor, showCircularBg = showIconBackground, resId = icon)
        }

    @ColorInt
    var iconTintColor: Int =
        context.getColorFromAttr(com.google.android.material.R.attr.colorAccent)
        set(value) {
            field = value
            setIcon(tint = iconTintColor, showCircularBg = showIconBackground, resId = icon)
        }

    var showIconBackground = false
        set(value) {
            field = value
            setIcon(tint = iconTintColor, showCircularBg = showIconBackground, resId = icon)
        }

    var maxSubTitleLines = 2 //default 2
        set(value) {
            field = value
            customItsLayoutBinding.txtViewCustomSubtitle.maxLines = value
        }

    var title: String = ""
        set(value) {
            field = value
            customItsLayoutBinding.txtViewCustomTitle.text = value
        }

    var subtitle: String? = null
        set(value) {
            field = value
            customItsLayoutBinding.txtViewCustomSubtitle.text = value
        }

    var isSubtitleVisible: Boolean = false
        set(value) {
            field = value
            customItsLayoutBinding.txtViewCustomSubtitle.isVisible = field
        }


    var iconVisibility: Int = View.VISIBLE
        set(value) {
            field = value
            customItsLayoutBinding.itsFrameLayoutHead.visibility = value
        }

    var switchVisibility: Int = View.VISIBLE
        set(value) {
            field = value
            customItsLayoutBinding.tailSwitch.visibility = value
        }

    private fun setIcon(
        @DrawableRes resId: Int,
        tint: Int,
        showCircularBg: Boolean,
    ) {
        if (resId != -1) {
            customItsLayoutBinding.imageIconCustomLayout.setImageDrawable(
                ContextCompat.getDrawable(context, resId)
            )
            customItsLayoutBinding.imageIconCustomLayout.imageTintList =
                ColorStateList.valueOf(tint)
            customItsLayoutBinding.itsFrameLayoutImageIcon.visibility = View.VISIBLE
            if (showCircularBg) {
                val colorWithAlphaComponent = ColorUtils.setAlphaComponent(tint, 30)
                customItsLayoutBinding.itsFrameLayoutImageIcon.backgroundTintList =
                    ColorStateList.valueOf(colorWithAlphaComponent)
            } else {
                customItsLayoutBinding.itsFrameLayoutImageIcon.backgroundTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(context, android.R.color.transparent)
                    )
            }
        } else {
            customItsLayoutBinding.itsFrameLayoutImageIcon.visibility = View.INVISIBLE
        }
        invalidate()
    }

    init {
        customItsLayoutBinding = CustomItemLayoutBinding.inflate(
            LayoutInflater.from(context), this, true
        )
        val a = context.obtainStyledAttributes(attrs, R.styleable.ITSCustomLayout, 0, 0)
        title = a.getString(R.styleable.ITSCustomLayout_title).orEmpty()
        subtitle = a.getString(R.styleable.ITSCustomLayout_subtitle)
        icon = a.getResourceId(R.styleable.ITSCustomLayout_icon, -1)
        iconTintColor = a.getResourceId(R.styleable.ITSCustomLayout_iconColorTint, -1).let { id ->
            if (id == -1) {
                context.getColorFromAttr(attrColor = com.google.android.material.R.attr.colorOnSurface)
            } else {
                ContextCompat.getColor(context, id)
            }
        }
        showIconBackground = a.getBoolean(R.styleable.ITSCustomLayout_showIconBackground, false)
        maxSubTitleLines = a.getInteger(R.styleable.ITSCustomLayout_maxSubTitleLines, 2)
        TextViewCompat.setTextAppearance(
            customItsLayoutBinding.txtViewCustomTitle, R.style.CustomLayoutTitleTextAppearance
        )
        TextViewCompat.setTextAppearance(
            customItsLayoutBinding.txtViewCustomSubtitle, R.style.CustomLayoutSubtitleTextAppearance
        )
        iconVisibility = when (a.getInt(R.styleable.ITSCustomLayout_iconVisibility, View.GONE)) {
            0 -> View.VISIBLE
            1 -> View.INVISIBLE
            else -> View.GONE
        }
        switchVisibility = when (a.getInt(R.styleable.ITSCustomLayout_switchVisibility, 0)) {
            0 -> View.VISIBLE
            1 -> View.INVISIBLE
            else -> View.GONE
        }
        a.recycle()
    }
}
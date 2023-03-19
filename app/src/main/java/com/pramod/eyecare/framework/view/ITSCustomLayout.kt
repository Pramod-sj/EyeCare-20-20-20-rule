package com.pramod.eyecare.framework.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import com.pramod.eyecare.R
import com.pramod.eyecare.databinding.CustomItemLayoutBinding
import com.pramod.eyecare.framework.ui.utils.getColorFromAttr
import timber.log.Timber

class ITSCustomLayout : LinearLayout {
    private lateinit var customItsLayoutBinding: CustomItemLayoutBinding
    private var title: String? = null
    private var titleTextSize: Float = 16f
    private var subTitle: String? = null

    @DrawableRes
    private var icon = -1

    @ColorInt
    private var iconTintColor: Int = -1
    private var noIconTint = false
    private var showIconBackground = false
    private var iconBackgroundTintResId = -1

    /**
     * default max lines is 2
     */
    private var maxSubTitleLines = 2

    /**
     * 0 - italic
     * 1 - bold
     * 2 - normal
     */
    private var titleTextStyle = 1

    @Deprecated("")
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
    ) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ITSCustomLayout, 0, 0)
        title = a.getString(R.styleable.ITSCustomLayout_title)
        subTitle = a.getString(R.styleable.ITSCustomLayout_subtitle)
        icon = a.getResourceId(R.styleable.ITSCustomLayout_icon, -1)
        iconTintColor =
            a.getResourceId(R.styleable.ITSCustomLayout_iconColorTint, -1)
                .let { id ->
                    if (id == -1) {
                        context.getColorFromAttr(attrColor = com.google.android.material.R.attr.colorOnSurface)
                    } else {
                        ContextCompat.getColor(context, id)
                    }
                }
        showIconBackground = a.getBoolean(R.styleable.ITSCustomLayout_showIconBackground, false)
        iconBackgroundTintResId =
            a.getResourceId(
                R.styleable.ITSCustomLayout_iconBackgroundTint,
                R.color.stroke_about_cards
            )
        maxSubTitleLines =
            a.getInteger(R.styleable.ITSCustomLayout_maxSubTitleLines, 2) //default: 2
        init()
        TextViewCompat.setTextAppearance(
            customItsLayoutBinding.txtViewCustomTitle,
            a.getResourceId(
                R.styleable.ITSCustomLayout_subtitleTextAppearance,
                com.google.android.material.R.style.TextAppearance_MaterialComponents_Subtitle2
            )
        )
        TextViewCompat.setTextAppearance(
            customItsLayoutBinding.txtViewCustomSubtitle,
            a.getResourceId(
                R.styleable.ITSCustomLayout_titleTextAppearance,
                com.google.android.material.R.style.TextAppearance_MaterialComponents_Caption
            )
        )
        a.recycle()
    }

    private fun init() {
        customItsLayoutBinding = CustomItemLayoutBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
        if (titleTextSize != 0f) {
            customItsLayoutBinding.txtViewCustomTitle.textSize = titleTextSize
        }
        setTitle(title)
        applyTitleTextStyle()
        setSubTitle(subTitle)
        applyMaxLineSubTitle()
        setIcon(icon)
    }

    private fun applyMaxLineSubTitle() {
        customItsLayoutBinding.txtViewCustomSubtitle
            .maxLines = maxSubTitleLines
    }

    private fun applyTitleTextStyle() {
        customItsLayoutBinding.txtViewCustomTitle.apply {
            Timber.i("applyTitleTextStyle: $titleTextStyle")
            val tf = when (titleTextStyle) {
                0 -> Typeface.ITALIC
                1 -> Typeface.BOLD
                else -> Typeface.NORMAL
            }
            setTypeface(
                if (tf == Typeface.NORMAL) {
                    null
                } else {
                    typeface
                },
                tf
            )
        }

    }

    private fun shouldShowBackgroundTint() {
        if (showIconBackground && !noIconTint) {
            val colorWithAlphaComponent = ColorUtils.setAlphaComponent(iconTintColor, 30)
            customItsLayoutBinding.itsFrameLayoutImageIcon.backgroundTintList =
                ColorStateList.valueOf(colorWithAlphaComponent)
        }
    }

    fun getTitle(): String? {
        return title
    }

    fun getSubTitle(): String? {
        return subTitle
    }

    fun setTitle(title: String?) {
        customItsLayoutBinding.txtViewCustomTitle.text = title
        invalidate()
    }

    fun setSubTitle(subTitle: String?) {
        if (!TextUtils.isEmpty(subTitle)) {
            customItsLayoutBinding.txtViewCustomSubtitle.text = subTitle
            customItsLayoutBinding.txtViewCustomSubtitle.visibility = View.VISIBLE
            invalidate()
        }
    }

    fun setIcon(@DrawableRes resId: Int) {
        if (resId != -1) {
            customItsLayoutBinding.imageIconCustomLayout.setImageDrawable(
                ContextCompat.getDrawable(
                    context,
                    resId
                )
            )
            if (!noIconTint) {
                customItsLayoutBinding.imageIconCustomLayout.imageTintList =
                    ColorStateList.valueOf(iconTintColor)
            }
            customItsLayoutBinding.itsFrameLayoutImageIcon.visibility = View.VISIBLE
            shouldShowBackgroundTint()
        } else {
            customItsLayoutBinding.itsFrameLayoutImageIcon.visibility = View.INVISIBLE
        }
        invalidate()
    }

    fun setIsSubtitleVisible(isVisible: Boolean) {
        customItsLayoutBinding.txtViewCustomSubtitle.isVisible = isVisible
        invalidate()
    }

}
package com.pramod.eyecare.framework.ui.common

import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.FloatRange
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.WindowCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.pramod.eyecare.R
import com.pramod.eyecare.framework.ui.utils.doWithInset
import com.pramod.eyecare.framework.ui.utils.getColorFromAttr
import timber.log.Timber

abstract class BaseBottomSheetDialogFragment(@LayoutRes val layoutId: Int) :
    DialogFragment(layoutId) {

    var topInset: Int = 0

    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    abstract fun getBottomSheetBehaviorView(): View

    /**
     * override this method and return false when you don't want to stop bottom sheet dragging when
     * dialog is in expanded state
     * this method is returning true because to fix child view scrolling issue
     */
    open fun lockBottomSheetDragWhenExpanded(): Boolean {
        return true
    }

    /**
     * bottom sheet dialog opening delay (in millis)
     * default value is 300ms
     */
    open fun getInitialDelay(): Long {
        return 300L
    }

    /**
     * close to 0 less than half expanded i.e. covering less screen
     * use = 0.5 half expanded i.e. covering half of the screen
     * close to 1 more than half expanded i.e. covering more screen
     */
    @FloatRange(from = 0.0, to = 1.0)
    open fun peekHeightFactor(): Float {
        return 0.7f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_EyeCare202020RuleTheme_BottomSheetDialogFragmentStyle)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog?.setOnKeyListener { dialog, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
                return@setOnKeyListener true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomSheetBehavior()
        view.doWithInset { view, top, bottom ->
            topInset = top
        }
        dismissWhenClickOutside()
        view.postDelayed({
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }, getInitialDelay())
    }

    private fun initBottomSheetBehavior() {
        bottomSheetBehavior = BottomSheetBehavior.from(getBottomSheetBehaviorView())

        val factorInPercentage = convertNumberRangeToAnotherRangeFromFloat(
            peekHeightFactor(),
            0f to 1f,
            0f to 100f
        )

        val peekHeightInPixel =
            (factorInPercentage * Resources.getSystem().displayMetrics.heightPixels) / 100f

        bottomSheetBehavior.peekHeight = peekHeightInPixel.toInt()

        bottomSheetBehavior.isHideable = true

        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }


    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }

            bottomSheetBehavior.isDraggable =
                !lockBottomSheetDragWhenExpanded() && newState != BottomSheetBehavior.STATE_EXPANDED

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isLight) {
                    dialog?.window?.configStatusBar(
                        makeLight = newState == BottomSheetBehavior.STATE_EXPANDED,
                        R.color.white,
                        true
                    )
                }

            }

            //dispatch stateChange updates to extending fragments
            this@BaseBottomSheetDialogFragment.onStateChanged(bottomSheet, newState)
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            applyBackgroundDim(slideOffset)

            applyTopInset(slideOffset)

            applyRadiusBasedOnSlideOffset(slideOffset)

            //dispatch onSlide updates to extending fragments
            this@BaseBottomSheetDialogFragment.onSlide(bottomSheet, slideOffset)
        }
    }

    open fun onStateChanged(bottomSheet: View, newState: Int) {}

    open fun onSlide(bottomSheet: View, slideOffset: Float) {}


    private fun applyBackgroundDim(slideOffset: Float) {
        view?.setBackgroundColor(
            ColorUtils.setAlphaComponent(
                Color.BLACK,
                convertNumberRangeToAnotherRangeFromFloat(
                    slideOffset,
                    -1f to 1f,
                    100f to 255f
                ).toInt()
            )
        )
    }

    private fun applyTopInset(slideOffset: Float) {
        val topMargin = convertNumberRangeToAnotherRangeFromFloat(
            slideOffset,
            0.8f to 1f,
            0f to topInset.toFloat()
        ).toInt()
        if (getBottomSheetBehaviorView() is MaterialCardView) {
            (getBottomSheetBehaviorView() as MaterialCardView).setContentPadding(
                0, if (topMargin < 0) 0 else topMargin, 0, 0
            )
        }
    }


    private fun applyRadiusBasedOnSlideOffset(slideOffset: Float) {
        if (getBottomSheetBehaviorView() is MaterialCardView) {

            val radiusInPixel = 35f
            val newCornerRadius = convertNumberRangeToAnotherRangeFromFloat(
                slideOffset,
                0.8f to 1f,
                radiusInPixel to 0f
            )

            val newShape: ShapeAppearanceModel = ShapeAppearanceModel.Builder()
                .setTopLeftCorner(
                    CornerFamily.ROUNDED,
                    if (newCornerRadius > radiusInPixel) radiusInPixel else newCornerRadius.toFloat()
                )
                .setTopRightCorner(
                    CornerFamily.ROUNDED,
                    if (newCornerRadius > radiusInPixel) radiusInPixel else newCornerRadius.toFloat()
                )
                .build()
            (getBottomSheetBehaviorView() as MaterialCardView).shapeAppearanceModel = newShape
        } else {
            Timber.e(

                "applyRadiusBasedOnSlideOffset: not a card view, hence cannot apply radius manipulation"
            )
        }
    }


    private fun dismissWhenClickOutside() {
        view?.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onDestroyView() {
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)
        super.onDestroyView()
    }


    companion object {
        val TAG = BaseBottomSheetDialogFragment::class.java.simpleName
    }

    private fun convertNumberRangeToAnotherRangeFromFloat(
        oldValue: Float,
        oldRange: Pair<Float, Float>,
        newRange: Pair<Float, Float>,
    ): Float {

        return ((oldValue - oldRange.first) / (oldRange.second - oldRange.first)) *
                (newRange.second - newRange.first) +
                newRange.first
    }

    fun Window.configStatusBar(
        makeLight: Boolean,
        statusBarColorResId: Int = -1,
        matchingBackgroundColor: Boolean = false,
    ) {
        val oldFlags = decorView.systemUiVisibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = oldFlags
            flags = if (makeLight) {
                flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            decorView.systemUiVisibility = flags
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = if (matchingBackgroundColor) {
                context.getColorFromAttr(android.R.attr.windowBackground)
            } else {
                ContextCompat.getColor(
                    requireContext(),
                    if (statusBarColorResId != -1) statusBarColorResId
                    else (if (makeLight) R.color.white else R.color.black)
                )
            }
        } else {
            statusBarColor = ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        }
    }
}
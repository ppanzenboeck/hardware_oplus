/*
 * SPDX-FileCopyrightText: 2019 CypherOS
 * SPDX-FileCopyrightText: 2014-2020 Paranoid Android
 * SPDX-FileCopyrightText: 2023-2026 The LineageOS Project
 * SPDX-FileCopyrightText: 2023 Yet Another AOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.settings.device

import android.animation.Animator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.view.Gravity
import android.view.Surface
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.content.res.ColorStateList
import android.util.Log
import android.util.TypedValue
import androidx.core.graphics.ColorUtils

/** View with some logging to show that its being run. */
class AlertSliderDialog(private var context: Context, private var sysuiContext: Context) :
    Dialog(context, R.style.alert_slider_theme) {
    private val dialogView by lazy { findViewById<LinearLayout>(R.id.alert_slider_dialog) }
    private val frameView by lazy { findViewById<ViewGroup>(R.id.alert_slider_view) }
    private val iconView by lazy { findViewById<ImageView>(R.id.alert_slider_icon) }
    private val textView by lazy { findViewById<TextView>(R.id.alert_slider_text) }

    private val rotation: Int = context.getDisplay().getRotation()
    private val isLand: Boolean = rotation != Surface.ROTATION_0
    private val isLeft = context.resources.getBoolean(R.bool.alert_slider_dialog_left)
    private val length: Int
    private val xPos: Int
    private val yPos: Int

    private var isAnimating = false
    private var animator = ValueAnimator()

    init {
        // window init
        getWindow()?.let {
            it.requestFeature(Window.FEATURE_NO_TITLE)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.addFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
            )
            it.addPrivateFlags(WindowManager.LayoutParams.PRIVATE_FLAG_TRUSTED_OVERLAY)
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
            it.setType(WindowManager.LayoutParams.TYPE_VOLUME_OVERLAY)

            it.attributes =
                it.attributes.apply {
                    format = PixelFormat.TRANSLUCENT
                    layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
                    title = TAG
                }
        }

        setCanceledOnTouchOutside(false)
        setContentView(R.layout.alert_slider_dialog)

        // position calculations
        val fraction = context.resources.getFraction(R.fraction.alert_slider_dialog_y, 1, 1)
        val widthPixels = context.resources.displayMetrics.widthPixels
        val heightPixels = context.resources.displayMetrics.heightPixels
        val pads = dialogView!!.paddingTop * 2 // equal paddings in all 4 directions
        length =
            if (isLand) context.resources.getDimension(R.dimen.alert_slider_dialog_width).toInt()
            else context.resources.getDimension(R.dimen.alert_slider_dialog_height).toInt()
        val hv = (length + pads) * 0.5

        xPos =
            if (isLand) (widthPixels * fraction - hv).toInt()
            else if (isLeft) 0 else widthPixels / 100
        yPos =
            if (isLand) (if (isLeft) (widthPixels / 100) else 0)
            else (heightPixels * fraction - hv).toInt()

        getWindow()?.let {
            it.attributes =
                it.attributes.apply {
                    gravity =
                        when (rotation) {
                            Surface.ROTATION_0 ->
                                if (isLeft) (Gravity.TOP or Gravity.LEFT)
                                else (Gravity.TOP or Gravity.RIGHT)
                            Surface.ROTATION_90 ->
                                if (isLeft) (Gravity.BOTTOM or Gravity.LEFT)
                                else (Gravity.TOP or Gravity.LEFT)
                            Surface.ROTATION_270 ->
                                if (isLeft) (Gravity.TOP or Gravity.RIGHT)
                                else (Gravity.BOTTOM or Gravity.RIGHT)
                            else ->
                                if (isLeft) (Gravity.BOTTOM or Gravity.LEFT)
                                else (Gravity.TOP or Gravity.LEFT)
                        }

                    x = xPos
                    y = yPos
                }
        }
    }

    @Synchronized
    fun setState(position: Int, ringerMode: Int, invertColors: Boolean) {
        val delta =
            length *
                when (position) {
                    KeyHandler.POSITION_TOP -> -1
                    KeyHandler.POSITION_BOTTOM -> 1
                    else -> 0 // KeyHandler.POSITION_MIDDLE
                }
        var endX = xPos
        var endY = yPos
        if (isLand) endX += delta else endY += delta

        if (isShowing()) {
            animatePosition(endX, endY, position, ringerMode, invertColors)
        } else {
            applyOnStart(ringerMode)
            applyOnEnd(endX, endY, position, invertColors)
        }
    }

    @Synchronized
    private fun animatePosition(endX: Int, endY: Int, position: Int, ringerMode: Int, invertColors: Boolean) {
        if (isAnimating) animator.cancel()
        animator = ValueAnimator()
        animator.setDuration(100)
        animator.setInterpolator(OvershootInterpolator())
        getWindow()?.let {
            animator.setValues(
                PropertyValuesHolder.ofInt("x", it.attributes.x, endX),
                PropertyValuesHolder.ofInt("y", it.attributes.y, endY)
            )
        }
        animator.addUpdateListener(
            object : ValueAnimator.AnimatorUpdateListener {
                override fun onAnimationUpdate(animation: ValueAnimator) {
                    getWindow()?.let {
                        it.attributes =
                            it.attributes.apply {
                                x = animation.getAnimatedValue("x") as Int
                                y = animation.getAnimatedValue("y") as Int
                            }
                    }
                }
            }
        )
        animator.addListener(
            object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    isAnimating = true
                    applyOnStart(ringerMode)
                }

                override fun onAnimationEnd(animation: Animator) {
                    applyOnEnd(endX, endY, position, invertColors)
                    isAnimating = false
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}
            }
        )
        animator.start()
    }

    private fun applyOnStart(ringerMode: Int) {
        sIconResMap.get(ringerMode)?.let { iconView!!.setImageResource(it) }
            ?: run { iconView!!.setImageResource(R.drawable.ic_info) }

        sTextResMap.get(ringerMode)?.let { textView!!.setText(it) }
            ?: run { textView!!.setText(R.string.alert_slider_mode_none) }
    }

    private fun applyOnEnd(endX: Int, endY: Int, position: Int, invertColors: Boolean) {
        if (isLeft) {
            frameView!!.setBackgroundResource(
                when (rotation) {
                    Surface.ROTATION_90 -> sBackgroundResMapLeft90.get(position)!!
                    Surface.ROTATION_270 -> sBackgroundResMapLeft270.get(position)!!
                    else -> sBackgroundResMapLeft.get(position)!! // Surface.ROTATION_0
                }
            )
        } else {
            frameView!!.setBackgroundResource(
                when (rotation) {
                    Surface.ROTATION_90 -> sBackgroundResMap90.get(position)!!
                    Surface.ROTATION_270 -> sBackgroundResMap270.get(position)!!
                    else -> sBackgroundResMap.get(position)!! // Surface.ROTATION_0
                }
            )
        }

        val isDark = (sysuiContext.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) == android.content.res.Configuration.UI_MODE_NIGHT_YES

        val bgResId = if (isDark) android.R.color.system_neutral1_800 else android.R.color.system_neutral1_100
        val accentResId = if (isDark) android.R.color.system_accent1_100 else android.R.color.system_accent1_500

        val bgColor = sysuiContext.getColor(bgResId)
        val accentColor = sysuiContext.getColor(accentResId)

        val finalBg = if (invertColors) accentColor else bgColor
        val finalAccent = if (invertColors) bgColor else accentColor

        val tonalColor = getTonalTextColor(finalBg, finalAccent)

        frameView!!.backgroundTintList = ColorStateList.valueOf(finalBg)
        iconView!!.imageTintList = ColorStateList.valueOf(tonalColor)
        textView!!.setTextColor(tonalColor)

        getWindow()?.let {
            it.attributes =
                it.attributes.apply {
                    x = endX
                    y = endY
                }
        }
    }

    companion object {
        private const val TAG = "AlertSliderDialog"

        private val sBackgroundResMap =
            hashMapOf(
                KeyHandler.POSITION_TOP to R.drawable.alert_slider_top,
                KeyHandler.POSITION_MIDDLE to R.drawable.alert_slider_middle,
                KeyHandler.POSITION_BOTTOM to R.drawable.alert_slider_bottom
            )

        private val sBackgroundResMapLeft =
            hashMapOf(
                KeyHandler.POSITION_TOP to R.drawable.alert_slider_top_270,
                KeyHandler.POSITION_MIDDLE to R.drawable.alert_slider_middle,
                KeyHandler.POSITION_BOTTOM to R.drawable.alert_slider_bottom_90
            )

        private val sBackgroundResMap90 =
            hashMapOf(
                KeyHandler.POSITION_TOP to R.drawable.alert_slider_top_90,
                KeyHandler.POSITION_MIDDLE to R.drawable.alert_slider_middle,
                KeyHandler.POSITION_BOTTOM to R.drawable.alert_slider_bottom_90
            )

        private val sBackgroundResMapLeft90 =
            hashMapOf(
                KeyHandler.POSITION_TOP to R.drawable.alert_slider_bottom_270,
                KeyHandler.POSITION_MIDDLE to R.drawable.alert_slider_middle,
                KeyHandler.POSITION_BOTTOM to R.drawable.alert_slider_top_270
            )

        private val sBackgroundResMap270 =
            hashMapOf(
                KeyHandler.POSITION_TOP to R.drawable.alert_slider_top_270,
                KeyHandler.POSITION_MIDDLE to R.drawable.alert_slider_middle,
                KeyHandler.POSITION_BOTTOM to R.drawable.alert_slider_bottom_270
            )

        private val sBackgroundResMapLeft270 =
            hashMapOf(
                KeyHandler.POSITION_TOP to R.drawable.alert_slider_bottom_90,
                KeyHandler.POSITION_MIDDLE to R.drawable.alert_slider_middle,
                KeyHandler.POSITION_BOTTOM to R.drawable.alert_slider_top_90
            )

        private val sIconResMap =
            hashMapOf(
                AudioManager.RINGER_MODE_SILENT to R.drawable.ic_volume_ringer_mute,
                AudioManager.RINGER_MODE_VIBRATE to R.drawable.ic_volume_ringer_vibrate,
                AudioManager.RINGER_MODE_NORMAL to R.drawable.ic_volume_ringer,
                KeyHandler.ZEN_PRIORITY_ONLY to R.drawable.ic_notifications_alert,
                KeyHandler.ZEN_TOTAL_SILENCE to R.drawable.ic_notifications_silence,
                KeyHandler.ZEN_ALARMS_ONLY to R.drawable.ic_alarm
            )

        private val sTextResMap =
            hashMapOf(
                AudioManager.RINGER_MODE_SILENT to R.string.alert_slider_mode_silent,
                AudioManager.RINGER_MODE_VIBRATE to R.string.alert_slider_mode_vibration,
                AudioManager.RINGER_MODE_NORMAL to R.string.alert_slider_mode_normal,
                KeyHandler.ZEN_PRIORITY_ONLY to R.string.alert_slider_mode_dnd_priority_only,
                KeyHandler.ZEN_TOTAL_SILENCE to R.string.alert_slider_mode_dnd_total_silence,
                KeyHandler.ZEN_ALARMS_ONLY to R.string.alert_slider_mode_dnd_alarms_only
            )

        /**
         * Calculates a readable tonal text color (ARGB) to sit on top of the given background color.
         * Uses luminance to pick black or white, then blends the accent color until WCAG 6.5:1 contrast is met.
         */
        @JvmStatic
        private fun getTonalTextColor(bgColor: Int, accentColor: Int): Int {
            val bgLum = ColorUtils.calculateLuminance(bgColor)
            val isBgLight = bgLum > 0.5
            val targetColor = if (isBgLight) android.graphics.Color.BLACK else android.graphics.Color.WHITE

            val minContrast = 6.5
            var blendRatio = 0.0f

            // If the raw accent already has enough contrast against the background, use it immediately
            if (ColorUtils.calculateContrast(accentColor, bgColor) >= minContrast) {
                return accentColor
            }

            Log.d("AlertSliderDialog", "Calculating high-end tint for bg=${Integer.toHexString(bgColor)}")

            // Iterative blend loop to find the perfect tonal shade
            while (blendRatio <= 1.0f) {
                val newColorArgb = ColorUtils.blendARGB(accentColor, targetColor, blendRatio)
                if (ColorUtils.calculateContrast(newColorArgb, bgColor) >= minContrast) {
                    Log.d("AlertSliderDialog", "Found contrast match at ratio $blendRatio: ${Integer.toHexString(newColorArgb)}")
                    return newColorArgb
                }
                blendRatio += 0.05f
            }

            return targetColor
        }
    }
}

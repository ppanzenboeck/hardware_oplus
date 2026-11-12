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
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.TransitionDrawable
import android.media.AudioManager
import android.view.Gravity
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.android.internal.graphics.drawable.BackgroundBlurDrawable

class AlertSliderDialog(private val context: Context, private val sysuiContext: Context) :
    Dialog(context, R.style.alert_slider_theme) {
    private val dialogView by lazy { findViewById<LinearLayout>(R.id.alert_slider_dialog)!! }
    private val frameView by lazy { findViewById<ViewGroup>(R.id.alert_slider_view)!! }
    private val iconView by lazy { findViewById<ImageView>(R.id.alert_slider_icon)!! }
    private val textView by lazy { findViewById<TextView>(R.id.alert_slider_text)!! }

    private val rotation: Int = context.getDisplay().getRotation()
    private val isLandscape = rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270
    private val flip = context.resources.getBoolean(R.bool.alert_slider_dialog_left)

    private val length: Int
    private val xPos: Int
    private val yPos: Int

    private var isAnimating = false
    private var animator = ValueAnimator()

    // Latest applied state, cached so the layered surface can be re-applied
    // once the dialog window is actually attached to its ViewRootImpl. The
    // initial setState() runs before show()/attach (the plugin sends
    // MSG_DIALOG_UPDATE before MSG_DIALOG_SHOW), so viewRootImpl is still null
    // at that point and the surface layer silently falls back to the plain
    // shape drawable.
    private var lastPosition = KeyHandler.POSITION_MIDDLE
    private var lastRingerMode = NONE
    private var lastInvertColors = false
    private var lastAttached = false
    // Resolved final window position (incl. the top/bottom delta), cached so the
    // layered surface can be re-applied without losing the pill's offset.
    private var lastEndX = 0
    private var lastEndY = 0

    init {
        window?.let {
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
        val res = context.resources
        val fraction = res.getFraction(R.fraction.alert_slider_dialog_y, 1, 1)
        val widthPixels = res.displayMetrics.widthPixels
        val heightPixels = res.displayMetrics.heightPixels
        val pads = dialogView.paddingTop * 2 // equal paddings in all 4 directions
        length =
            if (isLandscape) res.getDimension(R.dimen.alert_slider_dialog_width).toInt()
            else res.getDimension(R.dimen.alert_slider_dialog_height).toInt()
        val hv = (length + pads) * 0.5

        xPos =
            if (isLandscape) (widthPixels * fraction - hv).toInt()
            else if (flip) 0 else widthPixels / 100
        yPos =
            if (isLandscape) (if (flip) (widthPixels / 100) else 0)
            else (heightPixels * fraction - hv).toInt()

        window?.let {
            it.attributes =
                it.attributes.apply {
                    gravity =
                        when (rotation) {
                            Surface.ROTATION_0 ->
                                if (flip) Gravity.TOP or Gravity.LEFT
                                else Gravity.TOP or Gravity.RIGHT
                            Surface.ROTATION_90 ->
                                if (flip) Gravity.BOTTOM or Gravity.LEFT
                                else Gravity.TOP or Gravity.LEFT
                            Surface.ROTATION_270 ->
                                if (flip) Gravity.TOP or Gravity.RIGHT
                                else Gravity.BOTTOM or Gravity.RIGHT
                            else ->
                                if (flip) Gravity.BOTTOM or Gravity.LEFT
                                else Gravity.TOP or Gravity.LEFT
                        }

                    x = xPos
                    y = yPos
                }
        }

        // Re-apply the layered surface as soon as the dialog window is
        // attached. The initial setState() runs before show()/attach (the
        // plugin sends MSG_DIALOG_UPDATE before MSG_DIALOG_SHOW), so
        // viewRootImpl is null then and the surface layer silently falls back
        // to the plain shape drawable. The window's addView() inside
        // super.show() fires onViewAttachedToWindow, so the first visible frame
        // already carries the surface layer; show() below also re-applies as a
        // belt-and-suspenders guard against any attach path this listener
        // misses.
        frameView.addOnAttachStateChangeListener(
            object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(view: View) {
                    if (!lastAttached) {
                        applySurfaceLayerBackground()
                        lastAttached = true
                    }
                }

                override fun onViewDetachedFromWindow(view: View) {
                    lastAttached = false
                }
            }
        )
    }

    /**
     * Re-applies the UI mode and the layered translucent surface using the
     * last known state. Safe to call repeatedly; used to guarantee the
     * [BackgroundBlurDrawable] layer is bound before the first frame is drawn,
     * regardless of when the dialog window was attached.
     */
    private fun applySurfaceLayerBackground() {
        applyUiMode(lastRingerMode, lastInvertColors)
        applyPositionAndBackground(lastEndX, lastEndY, lastPosition)
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
        if (isLandscape) endX += delta else endY += delta

        if (isShowing) {
            animatePosition(endX, endY, position, ringerMode, invertColors)
        } else {
            applyUiMode(ringerMode, invertColors)
            applyPositionAndBackground(endX, endY, position)
        }
        lastPosition = position
        lastRingerMode = ringerMode
        lastInvertColors = invertColors
        lastEndX = endX
        lastEndY = endY
    }

    @Synchronized
    private fun animatePosition(
        endX: Int,
        endY: Int,
        position: Int,
        ringerMode: Int,
        invertColors: Boolean,
    ) {
        if (isAnimating) animator.cancel()
        animator = ValueAnimator()
        animator.duration = 100
        animator.interpolator = OvershootInterpolator()

        window?.let {
            animator.setValues(
                PropertyValuesHolder.ofInt("x", it.attributes.x, endX),
                PropertyValuesHolder.ofInt("y", it.attributes.y, endY),
            )
        }

        animator.addUpdateListener { animation ->
            window?.let {
                it.attributes =
                    it.attributes.apply {
                        x = animation.getAnimatedValue("x") as Int
                        y = animation.getAnimatedValue("y") as Int
                    }
            }
        }

        animator.addListener(
            object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    isAnimating = true
                    applyUiMode(ringerMode, invertColors)
                    val transition =
                        TransitionDrawable(
                            arrayOf(
                                frameView.background,
                                buildPillBackground(
                                    context.resources.getDrawable(
                                        backgroundFor(rotation, position, flip),
                                        null,
                                    ),
                                ),
                            )
                        )
                    frameView.background = transition
                    transition.setCrossFadeEnabled(true)
                    transition.startTransition(30)
                }

                override fun onAnimationEnd(animation: Animator) {
                    applyPositionAndBackground(endX, endY, position)
                    isAnimating = false
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}
            }
        )
        animator.start()
    }

    private fun applyUiTheme(invertColors: Boolean) {
        val currentUiMode = sysuiContext.resources.configuration.uiMode
        val isDark =
            (currentUiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES

        val accentResId =
            if (isDark) {
                android.R.color.system_accent1_100
            } else {
                android.R.color.system_accent1_500
            }

        val bgColor = resolveSurfaceColor(isDark)
        val accentColor = sysuiContext.getColor(accentResId)

        val activeFg = if (invertColors) bgColor else accentColor
        val activeBg = if (invertColors) accentColor else bgColor

        textView.setTextColor(activeFg)
        iconView.imageTintList = ColorStateList.valueOf(activeFg)
        // Opacity lives in the color value itself, exactly like SystemUI's
        // @drawable/volume_dialog_background, which carries no android:alpha and
        // leans on the surface token's own translucency instead of a frosted
        // blur. The inverted variant keeps the accent fully opaque so the pill
        // stays legible against the wallpaper.
        frameView.backgroundTintList =
            ColorStateList.valueOf(if (invertColors) activeBg else withSurfaceAlpha(activeBg))
    }

    /**
     * Resolves the Material You surface token SystemUI paints the volume dialog
     * with ([@android:color/materialColorSurface]). That token is private to the
     * framework (it has no public.xml entry), so it is looked up by name and
     * resolved through the SystemUI context, which already carries the right
     * night-mode configuration. Falls back to the public neutral ramp when the
     * token is unavailable.
     */
    private fun resolveSurfaceColor(isDark: Boolean): Int {
        val token =
            sysuiContext.resources.getIdentifier("materialColorSurface", "color", "android")
        return if (token != 0) {
            sysuiContext.getColor(token)
        } else {
            sysuiContext.getColor(
                if (isDark) {
                    android.R.color.system_neutral1_800
                } else {
                    android.R.color.system_neutral1_100
                }
            )
        }
    }

    /** Bakes [SURFACE_ALPHA] into [color] so the surface reads translucent. */
    private fun withSurfaceAlpha(color: Int): Int =
        (SURFACE_ALPHA shl 24) or (color and 0x00FFFFFF)

    private fun applyUiMode(ringerMode: Int, invertColors: Boolean) {
        val newIconRes =
            when (ringerMode) {
                AudioManager.RINGER_MODE_SILENT -> R.drawable.ic_volume_ringer_mute
                AudioManager.RINGER_MODE_VIBRATE -> R.drawable.ic_volume_ringer_vibrate
                AudioManager.RINGER_MODE_NORMAL -> R.drawable.ic_volume_ringer
                KeyHandler.ZEN_PRIORITY_ONLY -> R.drawable.ic_notifications_alert
                KeyHandler.ZEN_TOTAL_SILENCE -> R.drawable.ic_notifications_silence
                KeyHandler.ZEN_ALARMS_ONLY -> R.drawable.ic_alarm
                KeyHandler.TORCH_ON -> R.drawable.ic_torch_on
                KeyHandler.TORCH_OFF -> R.drawable.ic_torch_off
                else -> R.drawable.ic_info
            }

        // Animate a subtle scale-pop around the icon swap instead of an abrupt
        // instant change: shrink + fade down, swap the drawable at the trough,
        // then spring back to full size (OvershootInterpolator). This mirrors the
        // spring-like feel of the SystemUI volume dialog's ringer icon transitions.
        // Cancel any in-flight pop so a rapid mode change converges cleanly to 1.0.
        iconView.animate().cancel()
        iconView
            .animate()
            .scaleX(0.85f)
            .scaleY(0.85f)
            .alpha(0.5f)
            .setDuration(120)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                iconView.setImageResource(newIconRes)
                iconView
                    .animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(200)
                    .setInterpolator(OvershootInterpolator())
                    .start()
            }
            .start()

        textView.setText(
            when (ringerMode) {
                AudioManager.RINGER_MODE_SILENT -> R.string.alert_slider_mode_silent
                AudioManager.RINGER_MODE_VIBRATE -> R.string.alert_slider_mode_vibration
                AudioManager.RINGER_MODE_NORMAL -> R.string.alert_slider_mode_normal
                KeyHandler.ZEN_PRIORITY_ONLY -> R.string.alert_slider_mode_dnd_priority_only
                KeyHandler.ZEN_TOTAL_SILENCE -> R.string.alert_slider_mode_dnd_total_silence
                KeyHandler.ZEN_ALARMS_ONLY -> R.string.alert_slider_mode_dnd_alarms_only
                KeyHandler.TORCH_ON -> R.string.alert_slider_mode_torch_on
                KeyHandler.TORCH_OFF -> R.string.alert_slider_mode_torch_off
                else -> R.string.alert_slider_mode_none
            }
        )
        applyUiTheme(invertColors)
    }

    private fun applyPositionAndBackground(endX: Int, endY: Int, position: Int) {
        window?.let {
            it.attributes =
                it.attributes.apply {
                    x = endX
                    y = endY
                }
        }
        frameView.background =
            buildPillBackground(
                context.resources.getDrawable(backgroundFor(rotation, position, flip), null),
            )
    }

    /**
     * Composes the pill background the same way SystemUI composes the volume
     * dialog surface in VolumeDialogSlidersViewBinder /
     * VolumeDialogRingerViewBinder:
     *
     * ```
     * val surfaceEffect = background as GradientDrawable
     * val blurDrawable = viewRootImpl.createBackgroundBlurDrawable()
     * blurDrawable.setCornerRadius(dialogCornerRadius.toFloat())
     * blurDrawable.setBlurRadius(0)
     * background = LayerDrawable(arrayOf<Drawable>(blurDrawable, surfaceEffect))
     * ```
     *
     * The volume dialog is *not* frosted glass: it requests a
     * [BackgroundBlurDrawable] but pins its blur radius to 0, so that layer is
     * only a corner-clipped backing plate. All of the see-through quality comes
     * from the surface token's own translucency, which is why [applyUiTheme]
     * bakes the alpha into the tint instead of this helper lowering a drawable
     * alpha. The view-level [android.view.View.backgroundTintList] still lands
     * on the surface layer via [LayerDrawable] tint propagation.
     */
    private fun buildPillBackground(surface: Drawable): Drawable {
        val viewRoot = frameView.viewRootImpl ?: return surface
        val cornerRadius = context.resources.getDimension(R.dimen.alert_slider_corner_radius)

        val blurDrawable = viewRoot.createBackgroundBlurDrawable()
        // Match the volume dialog exactly: corner-clipped backing plate, no
        // gaussian frost applied to the layers behind the dialog.
        blurDrawable.setBlurRadius(0)
        blurDrawable.setCornerRadius(cornerRadius)

        return LayerDrawable(arrayOf<Drawable>(blurDrawable, surface))
    }

    private fun backgroundFor(rotation: Int, position: Int, flip: Boolean): Int {
        fun base(position: Int): Int =
            when (position) {
                KeyHandler.POSITION_TOP ->
                    if (flip) R.drawable.alert_slider_top_flip else R.drawable.alert_slider_top
                KeyHandler.POSITION_MIDDLE -> R.drawable.alert_slider_middle
                KeyHandler.POSITION_BOTTOM ->
                    if (flip) R.drawable.alert_slider_bottom_flip
                    else R.drawable.alert_slider_bottom
                else -> R.drawable.alert_slider_middle
            }

        return when (rotation) {
            Surface.ROTATION_90 ->
                when (position) {
                    KeyHandler.POSITION_TOP ->
                        if (flip) R.drawable.alert_slider_top_90_flip
                        else R.drawable.alert_slider_top_90
                    KeyHandler.POSITION_BOTTOM ->
                        if (flip) R.drawable.alert_slider_bottom_90_flip
                        else R.drawable.alert_slider_bottom_90
                    else -> R.drawable.alert_slider_middle
                }
            Surface.ROTATION_270 ->
                when (position) {
                    KeyHandler.POSITION_TOP ->
                        if (flip) R.drawable.alert_slider_top_270_flip
                        else R.drawable.alert_slider_top_270
                    KeyHandler.POSITION_BOTTOM ->
                        if (flip) R.drawable.alert_slider_bottom_270_flip
                        else R.drawable.alert_slider_bottom_270
                    else -> R.drawable.alert_slider_middle
                }
            else -> base(position) // ROTATION_0 / ROTATION_180
        }
    }

    /**
     * Whether the dialog is anchored to the LEFT edge of the screen.
     * When [flip] is true the dialog sits on the left; otherwise it sits on the right.
     * This mirrors SystemUI's [VolumeDialogViewBinder] logic that derives isLeft
     * from the window's absolute gravity.
     */
    private val isOnLeft: Boolean get() = flip

    override fun show() {
        dialogView.alpha = 0f
        super.show()
        // The initial setState() ran before the window was attached, so
        // viewRootImpl was null and the surface layer fell back to the plain
        // shape drawable. Now that super.show() has attached the window,
        // re-apply the layered background so the first visible frame is
        // translucent.
        applySurfaceLayerBackground()
        // Defer to the next frame so dialogView has a valid measured width,
        // then slide in from the screen edge (matching SystemUI volume dialog).
        dialogView.post {
            val slideDistance = dialogView.width.toFloat().takeIf { it > 0f }
                ?: context.resources.getDimension(R.dimen.alert_slider_dialog_width)
            dialogView.translationX = if (isOnLeft) -slideDistance else slideDistance
            // Animate in from the screen edge: slides along X from ±width to 0
            // while fading in. Overshoot gives the spring-like bounce of the
            // volume dialog's SpringAnimation.
            dialogView
                .animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(250)
                .setInterpolator(OvershootInterpolator())
                .start()
        }
    }

    override fun dismiss() {
        val slideDistance = dialogView.width.toFloat().takeIf { it > 0f }
            ?: context.resources.getDimension(R.dimen.alert_slider_dialog_width)
        // Slide out toward the screen edge it came from (matching SystemUI volume dialog).
        dialogView
            .animate()
            .alpha(0f)
            .translationX(if (isOnLeft) -slideDistance else slideDistance)
            .setDuration(200)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                dialogView.translationX = 0f
                super.dismiss()
            }
            .start()
    }

    companion object {
        private const val TAG = "AlertSliderDialog"

        // Alpha byte (0-255) baked into the pill surface color. SystemUI's
        // @drawable/volume_dialog_background sets no android:alpha at all and
        // instead leans on the translucency of @color/materialColorSurface, so
        // the equivalent translucency is folded into the tint here. ~90% keeps
        // the pill legible over bright and dark wallpapers alike.
        private const val SURFACE_ALPHA = 0xE6

        // Ringer mode sentinel (mirrors AlertSliderPlugin.NONE).
        private const val NONE = -1
    }
}

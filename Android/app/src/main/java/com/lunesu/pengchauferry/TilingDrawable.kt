package com.lunesu.pengchauferry

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat

open class DrawableWrapper(drawable: Drawable) : Drawable(), Drawable.Callback {
    var wrappedDrawable: Drawable = drawable
        set(drawable) {
            field.callback = null
            field = drawable
            drawable.callback = this
        }

    override fun draw(canvas: Canvas) = wrappedDrawable.draw(canvas)

    override fun onBoundsChange(bounds: Rect) {
        wrappedDrawable.bounds = bounds
    }

    override fun setChangingConfigurations(configs: Int) {
        wrappedDrawable.changingConfigurations = configs
    }

    override fun getChangingConfigurations() = wrappedDrawable.changingConfigurations

    override fun setDither(dither: Boolean) = wrappedDrawable.setDither(dither)

    override fun setFilterBitmap(filter: Boolean) {
        wrappedDrawable.isFilterBitmap = filter
    }

    override fun setAlpha(alpha: Int) {
        wrappedDrawable.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        wrappedDrawable.colorFilter = cf
    }

    override fun isStateful() = wrappedDrawable.isStateful

    override fun setState(stateSet: IntArray) = wrappedDrawable.setState(stateSet)

    override fun getState() = wrappedDrawable.state


    override fun jumpToCurrentState() = DrawableCompat.jumpToCurrentState(wrappedDrawable)

    override fun getCurrent() = wrappedDrawable.current

    override fun setVisible(visible: Boolean, restart: Boolean) = super.setVisible(visible, restart) || wrappedDrawable.setVisible(visible, restart)

    override fun getOpacity() = wrappedDrawable.opacity

    override fun getTransparentRegion() = wrappedDrawable.transparentRegion

    override fun getIntrinsicWidth() = wrappedDrawable.intrinsicWidth

    override fun getIntrinsicHeight() = wrappedDrawable.intrinsicHeight

    override fun getMinimumWidth() = wrappedDrawable.minimumWidth

    override fun getMinimumHeight() = wrappedDrawable.minimumHeight

    override fun getPadding(padding: Rect) = wrappedDrawable.getPadding(padding)

    override fun invalidateDrawable(who: Drawable) = invalidateSelf()

    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) = scheduleSelf(what, `when`)

    override fun unscheduleDrawable(who: Drawable, what: Runnable) = unscheduleSelf(what)

    override fun onLevelChange(level: Int) = wrappedDrawable.setLevel(level)

    override fun setAutoMirrored(mirrored: Boolean) = DrawableCompat.setAutoMirrored(wrappedDrawable, mirrored)

    override fun isAutoMirrored() = DrawableCompat.isAutoMirrored(wrappedDrawable)

    override fun setTint(tint: Int) = DrawableCompat.setTint(wrappedDrawable, tint)

    override fun setTintList(tint: ColorStateList?) = DrawableCompat.setTintList(wrappedDrawable, tint)

    override fun setTintMode(tintMode: PorterDuff.Mode) = DrawableCompat.setTintMode(wrappedDrawable, tintMode)

    override fun setHotspot(x: Float, y: Float) = DrawableCompat.setHotspot(wrappedDrawable, x, y)

    override fun setHotspotBounds(left: Int, top: Int, right: Int, bottom: Int) = DrawableCompat.setHotspotBounds(wrappedDrawable, left, top, right, bottom)
}

class TilingDrawable(drawable: Drawable) : DrawableWrapper(drawable) {

    private var callbackEnabled = true

    override fun draw(canvas: Canvas) {
        callbackEnabled = false
        val bounds: Rect = getBounds()
        val width = wrappedDrawable.intrinsicWidth
        val height = wrappedDrawable.intrinsicHeight
        var x: Int = bounds.left
        while (x < bounds.right + width - 1) {
            var y: Int = bounds.top
            while (y < bounds.bottom + height - 1) {
                wrappedDrawable.setBounds(x, y, x + width, y + height)
                wrappedDrawable.draw(canvas)
                y += height
            }
            x += width
        }
        callbackEnabled = true
    }

    override fun onBoundsChange(bounds: Rect) {}
    /**
     * {@inheritDoc}
     */
    override fun invalidateDrawable(who: Drawable) {
        if (callbackEnabled) {
            super.invalidateDrawable(who)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun scheduleDrawable(
        who: Drawable,
        what: Runnable,
        `when`: Long
    ) {
        if (callbackEnabled) {
            super.scheduleDrawable(who, what, `when`)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun unscheduleDrawable(
        who: Drawable,
        what: Runnable
    ) {
        if (callbackEnabled) {
            super.unscheduleDrawable(who, what)
        }
    }
}
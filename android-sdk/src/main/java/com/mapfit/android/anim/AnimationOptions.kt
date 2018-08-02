package com.mapfit.android.anim

import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator

@Suppress("UNCHECKED_CAST")
abstract class AnimationOptions<out T> {

    internal var duration: Long = 2000L
    internal var interpolator: Interpolator = AccelerateDecelerateInterpolator()
    internal var listener: AnimationListener? = null

    /**
     * Sets the duration of the animation.
     *
     * @param duration in milliseconds
     */
    fun duration(duration: Long): T {
        this.duration = duration
        return this as T
    }

    /**
     * Sets the interpolator for the animation.
     *
     * @param interpolator
     */
    fun interpolator(interpolator: Interpolator): T {
        this.interpolator = interpolator
        return this as T
    }

    /**
     * Sets [AnimationListener] for animation events.
     *
     * @param listener
     */
    fun animationListener(listener: AnimationListener): T {
        this.listener = listener
        return this as T
    }


}
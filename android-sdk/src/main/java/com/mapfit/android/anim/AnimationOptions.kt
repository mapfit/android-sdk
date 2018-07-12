package com.mapfit.android.anim

@Suppress("UNCHECKED_CAST")
abstract class AnimationOptions<out T> {

    internal var duration: Long = 0L
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
     * Sets [AnimationListener] for animation events.
     *
     * @param listener
     */
    fun setAnimationListener(listener: AnimationListener): T {
        this.listener = listener
        return this as T
    }

}
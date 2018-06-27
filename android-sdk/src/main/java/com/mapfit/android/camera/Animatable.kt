package com.mapfit.android.camera

interface Animatable {

    /**
     * Returns if the animation is running.
     *
     * @return true if animation is running
     */
    fun isRunning(): Boolean

    /**
     * Starts the animation.
     */
    fun start()

    /**
     * Stops the animation.
     */
    fun stop()

}
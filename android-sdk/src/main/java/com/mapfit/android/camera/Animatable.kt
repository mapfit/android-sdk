package com.mapfit.android.camera

interface Animatable {

    /**
     * Returns if there is a running animation.
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
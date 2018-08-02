@file:Suppress("UNCHECKED_CAST")

package com.mapfit.android.camera

import com.mapfit.android.MapController

/**
 * Defines options for a [CameraAnimation].
 */
open class CameraOptions<T> {

    internal var duration = 0L
    internal var tiltAngle: Float = Float.NaN
    internal var tiltDuration = 0L
    internal var tiltEaseType = MapController.EaseType.QUART_IN_OUT
    internal var rotationAngle: Float = Float.NaN
    internal var rotationDuration = 0L
    internal var rotationEaseType = MapController.EaseType.QUART_IN_OUT
    internal var zoomLevel: Float = Float.NaN
    internal var zoomDuration = 0L
    internal var zoomEaseType = MapController.EaseType.QUART_IN_OUT

    /**
     * Sets duration for the animation.
     *
     * @param duration
     */
    fun duration(duration: Long): T {
        this.duration = duration
        return this as T
    }

    /**
     * Sets the tilting animation options for the camera.
     *
     * @param angle angle in radians, 0 is to straight down
     * @param duration of the animation
     * @param easeType for the animation
     */
    @JvmOverloads
    fun tiltTo(
        angle: Float,
        duration: Long = 0,
        easeType: MapController.EaseType = MapController.EaseType.QUART_IN_OUT
    ): T {
        this.tiltAngle = angle
        this.tiltDuration = duration
        this.tiltEaseType = easeType
        return this as T
    }

    /**
     * Sets the rotation animation options for the camera.
     *
     * @param angle angle in radians, 0 is facing north
     * @param duration of the animation
     * @param easeType for the animation
     */
    @JvmOverloads
    fun rotateTo(
        angle: Float,
        duration: Long = 0,
        easeType: MapController.EaseType = MapController.EaseType.QUART_IN_OUT
    ): T {
        this.rotationAngle = angle
        this.rotationDuration = duration
        this.rotationEaseType = easeType
        return this as T
    }

    /**
     * Sets the zoom animation options for the camera.
     *
     * @param zoomLevel
     * @param duration of the animation
     * @param easeType for the animation
     */
    @JvmOverloads
    fun zoomTo(
        zoomLevel: Float,
        duration: Long = 0,
        easeType: MapController.EaseType = MapController.EaseType.QUART_IN_OUT
    ): T {
        this.zoomLevel = zoomLevel
        this.zoomDuration = duration
        this.zoomEaseType = easeType
        return this as T
    }

}
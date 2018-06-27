package com.mapfit.android.camera

import android.support.annotation.FloatRange
import com.mapfit.android.MapController
import com.mapfit.android.geometry.LatLng


/**
 * Defines orbit animation options for [OrbitAnimation].
 */
class OrbitTrajectory : CameraOptions<OrbitTrajectory>() {

    internal lateinit var pivotPosition: LatLng
    internal var loop: Boolean = true
    internal var centerToPivot = false
    internal var centeringDuration = 0L
    internal var centeringEaseType = MapController.EaseType.QUART_IN_OUT
    internal var speedMultiplier = 1f

    /**
     * Sets the pivot position for the camera to pan around of.
     *
     * @param position
     * @param centerToPivot set true to center to pivot
     * @param duration animation duration in milliseconds
     * @param easeType easing type for the animation
     */
    fun pivot(
        position: LatLng,
        centerToPivot: Boolean = true,
        duration: Long = 0,
        easeType: MapController.EaseType = MapController.EaseType.QUART_IN_OUT
    ): OrbitTrajectory {
        this.pivotPosition = position
        this.centerToPivot = centerToPivot
        this.centeringDuration = duration
        this.centeringEaseType = easeType
        return this
    }

    /**
     * Sets if the camera animation will run infinitely.
     *
     * @param loop
     */
    fun loop(loop: Boolean): OrbitTrajectory {
        this.loop = loop
        return this
    }

    /**
     * Sets the multiplier for rotation speed. For half speed, you can set `0.5f` where `1` is default
     * speed. Positive values will rotate anti-clockwise whereas negative values will rotate
     * clockwise.
     *
     * @param multiplier
     */
    fun speedMultiplier(multiplier: Float): OrbitTrajectory {
        this.speedMultiplier = multiplier
        return this
    }

}
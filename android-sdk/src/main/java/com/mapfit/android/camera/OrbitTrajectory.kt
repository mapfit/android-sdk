package com.mapfit.android.camera

import android.support.annotation.FloatRange
import com.mapfit.android.MapController
import com.mapfit.android.geometry.LatLng

class OrbitTrajectory : CameraOptions<OrbitTrajectory>() {

    internal lateinit var pivotPosition: LatLng
    internal var loop: Boolean = true
    internal var centerToPivot = false
    internal var centeringDuration = 0L
    internal var centeringEaseType = MapController.EaseType.QUINT_IN_OUT
    internal var speedMultiplier = 1f

    /**
     * Pivot position for camera to pan around of.
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
        easeType: MapController.EaseType
    ): OrbitTrajectory {
        this.pivotPosition = position
        this.centerToPivot = centerToPivot
        this.centeringDuration = duration
        this.centeringEaseType = easeType
        return this
    }

    /**
     * Camera will be rotating infinitely if loop is set to true.
     *
     * @param loop
     */
    fun loop(loop: Boolean): OrbitTrajectory {
        this.loop = loop
        return this
    }

    /**
     * Sets the multiplier for rotation speed.
     *
     * @param multiplier
     */
    fun speedMultiplier(@FloatRange(from = 0.0) multiplier: Float): OrbitTrajectory {
        this.speedMultiplier = multiplier
        return this
    }

}
package com.mapfit.android.camera

import com.mapfit.android.MapfitMap

/**
 * Cinematography class makes it easier to implement camera animations by providing pre-made
 * animations.
 */
class Cinematography(private val mapfitMap: MapfitMap) {

    /**
     * Creates an animation with the given [CameraOptions].
     *
     * @param cameraOptions options for camera animation
     * @param cameraAnimationCallback callback to listen to animation events
     */
    fun <T> create(
        cameraOptions: CameraOptions<T>,
        cameraAnimationCallback: CameraAnimationCallback? = null
    ): CameraAnimation {
        return when (cameraOptions) {
            is OrbitTrajectory -> OrbitAnimation(cameraOptions, mapfitMap, cameraAnimationCallback)
            else -> Any() as CameraAnimation
        }
    }
}
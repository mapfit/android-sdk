package com.mapfit.android.camera

import android.graphics.PointF
import com.mapfit.android.MapController
import com.mapfit.android.MapfitMap
import com.mapfit.android.geometry.toLatLng
import com.mapfit.android.geometry.toPointF
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class OrbitAnimation(
    private val orbitTrajectory: OrbitTrajectory,
    private val mapfitMap: MapfitMap,
    private val cameraAnimationCallback: CameraAnimationCallback?
) : CameraAnimation {

    private val STEP_DURATION = 150L
    private var rotation = Float.NaN
    private var isRunning = false
    private var playedDuration = 0L
    private var runInitialAnimations = true
    private val rotationDegrees = 1.0 * orbitTrajectory.speedMultiplier

    override fun start() {
        isRunning = true

        cameraAnimationCallback?.let { launch(UI) { it.onStart() } }
        launch {
            if (orbitTrajectory.loop) {
                while (orbitTrajectory.loop && isRunning) {
                    delay(STEP_DURATION)
                    animate()
                }

            } else {
                val repeatCount =
                    ((orbitTrajectory.duration - playedDuration) / STEP_DURATION).toInt()

                repeat(repeatCount) {
                    delay(STEP_DURATION)
                    playedDuration += STEP_DURATION
                    animate()

                    if (it == repeatCount - 1) {
                        cameraAnimationCallback?.let { launch(UI) { it.onFinish() } }
                    }

                    if (!isRunning) {
                        return@launch
                    }
                }

            }
        }
    }

    private fun animate() {
        rotation = mapfitMap.getRotation()

        if (runInitialAnimations) {
            runInitialAnimations = false

            setInitialTilt()
            setInitialZoom()
            setInitialCenter()
        }

        rotation = ((rotation + Math.toRadians(rotationDegrees)) % 360).toFloat()
        mapfitMap.setRotation(rotation, STEP_DURATION, MapController.EaseType.LINEAR)
    }

    private fun setInitialCenter() = launch {
        if (orbitTrajectory.centerToPivot) {
            orbitTrajectory.centerToPivot = false

            val spCenter = mapfitMap.getCenter()?.toPointF(mapfitMap.getZoom()) ?: PointF(0f, 0f)
            val spPivot = orbitTrajectory.pivotPosition.toPointF(mapfitMap.getZoom())

            val newX =
                spCenter.x + (spPivot.x - spCenter.x) * Math.cos(rotation.toDouble()) -
                        (spPivot.y - spCenter.y) * Math.sin(rotation.toDouble())

            val newY =
                spCenter.y + (spPivot.x - spCenter.x) * Math.sin(rotation.toDouble()) + (spPivot.y - spCenter.y) * Math.cos(
                    rotation.toDouble()
                )

            val newPoint = PointF(newX.toFloat(), newY.toFloat())

            mapfitMap.setCenter(
                newPoint.toLatLng(mapfitMap.getZoom()),
                orbitTrajectory.centeringDuration,
                orbitTrajectory.centeringEaseType
            )
        }
    }

    private fun setInitialTilt() = launch {
        if (!orbitTrajectory.tiltAngle.isNaN() &&
            mapfitMap.getTilt() != orbitTrajectory.tiltAngle
        ) {
            launch {
                mapfitMap.setTilt(
                    orbitTrajectory.tiltAngle,
                    orbitTrajectory.tiltDuration,
                    orbitTrajectory.tiltEaseType
                )
            }
        }
    }

    private fun setInitialZoom() = launch {
        if (!orbitTrajectory.zoomLevel.isNaN() &&
            mapfitMap.getZoom() != orbitTrajectory.zoomLevel
        ) {
            launch {
                mapfitMap.setZoom(
                    orbitTrajectory.zoomLevel,
                    orbitTrajectory.zoomDuration,
                    orbitTrajectory.zoomEaseType
                )
            }
        }
    }

    override fun isRunning() = isRunning

    override fun stop() {
        isRunning = false
    }

}
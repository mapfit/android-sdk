package com.mapfit.android.anim.polyline

import android.animation.Animator
import android.animation.ObjectAnimator
import android.support.annotation.Keep
import com.mapfit.android.anim.RouteEvaluator
import com.mapfit.android.annotations.Polyline
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.geometry.isEmpty
import com.mapfit.android.utils.logWarning
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class SnakeAnimation(private val animationOptions: SnakeAnimationOptions) : PolylineAnimation() {

    private var duration = animationOptions.duration
    private var listener = animationOptions.listener
    private var polylinePoints = mutableListOf<LatLng>()
    private val evaluatedLatLngs = mutableListOf<LatLng>()
    private var smoothingPolylines = mutableListOf<Polyline>()
    private var routeAnimator: Animator? = null
    private val overlappingPolylineCount = 4
    private var smoothingIndex = 0

    override fun start() {
        when {
            polyline == null -> logWarning("SnakeAnimation couldn't be executed without a polyline. Did you bind the animation to a polyline?")
            polyline?.points?.size ?: 0 < 2 -> logWarning("SnakeAnimation couldn't be executed with ${polyline?.points?.size} points. You need at least 2 points to animate.")
            else -> animatePath()
        }
    }

    override fun stop() {
        launch(UI) { routeAnimator?.cancel() }
    }

    private fun animatePath() {
        routeAnimator = getRouteAnimator()

        polyline?.let {
            // copy of the original points
            polylinePoints = it.points.toMutableList()

            // clear the points
            it.points = mutableListOf()
            it.polylineOptions.points = mutableListOf()
        }

        addAnimatingPolylines()

        routeAnimator?.apply {
            duration = this@SnakeAnimation.duration

            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    dispose()
                    running = false
                    finished = true
                    listener?.onFinish(this@SnakeAnimation)
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                    running = true
                    listener?.onStart(this@SnakeAnimation)
                }
            })

            launch(UI) {
                start()
            }
        }
    }

    private fun addAnimatingPolylines() {
        polyline?.let {
            it.polylineOptions.animation = null

            var pCount = overlappingPolylineCount
            while (pCount > 0) {
                smoothingPolylines.add(it.mapController.addPolyline(it.polylineOptions))
                pCount--
            }
        }
    }

    private fun getRouteAnimator(): ObjectAnimator? {
        val points = polyline?.points ?: listOf<LatLng>()

        val routeAnimator = ObjectAnimator.ofObject(
            this,
            "evaluatedLatLng",
            RouteEvaluator(),
            *points.toTypedArray()
        )
        routeAnimator.interpolator = animationOptions.interpolator

        return routeAnimator
    }

    private fun dispose() {
        polyline?.points = polylinePoints
        smoothingPolylines.forEach { it.remove() }
        smoothingPolylines.clear()
        evaluatedLatLngs.clear()
        smoothingIndex = 0
    }

    @Keep
    fun setEvaluatedLatLng(evaluatedLatLng: LatLng) {
        if (!evaluatedLatLng.isEmpty()) {
            if (!evaluatedLatLng.isEmpty()) {
                evaluatedLatLngs.add(evaluatedLatLng)
                smoothingPolylines[smoothingIndex.rem(overlappingPolylineCount)].points =
                        evaluatedLatLngs

                smoothingIndex++
            }
        }
    }

    private fun getPointsToIndex(index: Int): MutableList<LatLng> {
        val points = mutableListOf<LatLng>()

        var tIndex = index
        while (tIndex >= 0) {
            points.add(polylinePoints[tIndex])
            tIndex--
        }
        return points
    }

    override fun isRunning() = running

}
package com.mapfit.android.anim.polyline

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.support.annotation.Keep
import android.view.animation.LinearInterpolator
import com.mapfit.android.annotations.Polyline
import com.mapfit.android.anim.RouteEvaluator
import com.mapfit.android.geometry.LatLng
import com.mapfit.android.geometry.isEmpty
import com.mapfit.android.utils.logWarning
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch

class SnakeAnimation(animationOptions: SnakeAnimationOptions) : PolylineAnimation() {

    private var running = false
    private var duration = animationOptions.duration
    private var listener = animationOptions.listener
    private var animatorSet = AnimatorSet()
    private var polylinePoints = mutableListOf<LatLng>()
    private val evaluatedLatLngs = mutableListOf<LatLng>()
    private var smoothingPolylines = mutableListOf<Polyline>()
    private val overlappingPolylineCount = 4
    private var smoothingIndex = 0

    override fun start() {
        if (polyline.points.size < 2) {
            logWarning("SnakeAnimation couldn't be executed with ${polyline.points.size} points. You need at least 2 points to animate.")
        } else {
            animatePath()
        }
    }

    override fun stop() {
        launch(UI) { animatorSet.cancel() }
    }


    private fun animatePath() {
        val routeAnimator = getRouteAnimator()


        // copy of the original points
        polylinePoints = polyline.points.toMutableList()

        // clear the points
        polyline.points = mutableListOf()
        polyline.polylineOptions.points = mutableListOf()



        addAnimatingPolylines()

        routeAnimator?.apply {
            duration = this@SnakeAnimation.duration

            addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    cancel()
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
        polyline.polylineOptions.animation = null

        var pCount = overlappingPolylineCount
        while (pCount > 0) {
            smoothingPolylines.add(polyline.mapController.addPolyline(polyline.polylineOptions))
            pCount--
        }
    }

    private fun getRouteAnimator(): ObjectAnimator? {
        val routeAnimator = ObjectAnimator.ofObject(
            this,
            "evaluatedLatLng",
            RouteEvaluator(),
            *polyline.points.toTypedArray()
        )
        routeAnimator.interpolator = LinearInterpolator()

        return routeAnimator
    }

    private fun dispose() {
        polyline.points = polylinePoints
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
package com.mapfit.android.annotations.widget

import android.animation.Animator
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.TextView
import com.mapfit.android.MapController
import com.mapfit.android.R
import com.mapfit.android.annotations.Marker
import kotlinx.coroutines.experimental.*

/**
 * Place Info of to be shown when a [Marker] is clicked.
 *
 * Created by dogangulcan on 1/30/18.
 */
class PlaceInfo internal constructor(
    private var infoView: View,
    internal val marker: Marker,
    private val mapController: MapController
) {

    private lateinit var titleView: TextView
    private lateinit var subtitle1View: TextView
    private lateinit var subtitle2View: TextView
    private var viewHeight: Int? = null
    private var viewWidth: Int? = null
    private var positionUpdateJob = Job()

    init {
        if (infoView.tag == "default") {
            defaultInit()
        }

        infoView.post {
            viewHeight = infoView.height
            viewWidth = infoView.width
        }
    }

    private fun defaultInit() {
        titleView = infoView.findViewById(R.id.title)
        subtitle1View = infoView.findViewById(R.id.subtitle1)
        subtitle2View = infoView.findViewById(R.id.subtitle2)
        updatePlaceInfo()
    }

    internal fun updatePlaceInfo() {
        titleView.text =
                if (marker.title.isBlank()) {
                    marker.address?.streetAddress ?: marker.title
                } else {
                    marker.title
                }
        subtitle1View.text = marker.subtitle1
        subtitle2View.text = marker.subtitle2
    }

    fun show() {
        infoView.visibility = View.INVISIBLE
        updatePositionDelayed()
        infoView.alpha = 0f
        infoView.visibility = View.VISIBLE

        marker.placeInfoState(true, mapController)

        infoView.animate()
            .alpha(1f)
            .setDuration(75)
            .setListener(null)
    }

    internal fun updatePositionDelayed() {
        if (infoView.visibility != View.GONE) {
            runBlocking {
                positionUpdateJob.cancelAndJoin()
                launch {
                    repeat(1300) {
                        onPositionChanged()
                        delay(1)
                    }
                }
            }
        }
    }

    fun hide() {
        marker.placeInfoState(false, mapController)
        infoView.animate()
            .alpha(0f)
            .setDuration(75)
            .setListener(object : Animation.AnimationListener, Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    infoView?.visibility = View.GONE
                }
            })
    }

    fun getVisibility(): Boolean = infoView.visibility == View.VISIBLE

    fun getVisibility(mapController: MapController): Boolean =
        infoView.visibility == View.VISIBLE && this.mapController == mapController

    internal fun dispose(removed: Boolean = false) {
        if (infoView.parent != null) {
            (infoView.parent as ViewGroup).removeView(infoView)
        }

        if (!removed) {
            marker.placeInfoState(false, mapController)
        }

        infoView.visibility = View.GONE
    }

    internal fun onPositionChanged() {
        if (infoView.visibility != View.GONE) {
            val point = marker.getScreenPosition(mapController)

            infoView.post {
                infoView.x = point.x - (viewWidth?.div(2) ?: 0)
                infoView.y = point.y - (viewHeight ?: 0) + 10 -
                        (if (marker.hasCustomPlaceInfo) 30 else 0)/* adjustment for the shadow*/
            }
        }
    }

}
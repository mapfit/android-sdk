package com.mapfit.mapfitsdk.annotations.widget

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mapfit.mapfitsdk.MapController
import com.mapfit.mapfitsdk.R
import com.mapfit.mapfitsdk.annotations.Marker
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
    //    private val MARKER_HEIGHT_MULTIPLIER: Float = if (infoView.tag == "default") 1f else 3.3f
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
                if (marker.getTitle().isBlank()) {
                    marker.address?.streetAddress ?: marker.getTitle()
                } else {
                    marker.getTitle()
                }
        subtitle1View.text = marker.getSubtitle1()
        subtitle2View.text = marker.getSubtitle2()
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
        infoView.visibility = View.GONE
    }

    fun getVisible(): Boolean = infoView.visibility == View.VISIBLE


    fun getVisible(mapController: MapController): Boolean =
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
                infoView.y =
                        (point.y - (viewHeight ?: 0) + 15/* adjustment for the shadow padding*/)
            }
        }
    }

}
package com.mapfit.mapfitsdk.annotations.widget

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mapfit.mapfitsdk.R
import com.mapfit.mapfitsdk.annotations.Marker
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * Created by dogangulcan on 1/30/18.
 */
class PlaceInfo internal constructor(
    private var infoView: View,
    internal val marker: Marker
) {

    private lateinit var titleView: TextView
    private lateinit var subtitle1View: TextView
    private lateinit var subtitle2View: TextView
    private var isVisible = false
    private var viewHeight: Int? = null
    private var viewWidth: Int? = null

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

        infoView.animate()
            .alpha(1f)
            .setDuration(100)
            .setListener(null)


    }

    internal fun updatePositionDelayed() {
        if (infoView.visibility != View.GONE)
            launch {
                repeat(1000) {
                    onPositionChanged()
                    delay(1)
                }
            }
    }

    fun hide() {
        infoView.visibility = View.GONE
    }

    fun getVisible(): Boolean {
        return infoView.visibility == View.VISIBLE
    }

    internal fun dispose() {
        isVisible = false
        if (infoView.parent != null) {
            (infoView.parent as ViewGroup).removeView(infoView)
        }
    }

    internal fun onPositionChanged() {
        if (infoView.visibility != View.GONE) {
            val point = marker.getScreenPosition()
            infoView.post {
                infoView.x = point.x - (viewWidth?.div(2) ?: 0)
                infoView.y = (point.y - (viewHeight ?: 0) - marker.markerOptions.height * 2)
            }
        }
    }

}
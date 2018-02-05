package com.mapfit.mapfitsdk.annotations

import android.support.v4.content.ContextCompat
import com.mapfit.mapfitsdk.R


/**
 * Created by dogangulcan on 2/5/18.
 */
class PolylineOptions internal constructor(
    private var polyline: Polyline
) {

    var lineWidth = 5
        set(value) {
            field = value
            updateStyle()
        }

    var drawOrder = 1000
        set(value) {
            field = value
            updateStyle()
        }

    var color = "#" + Integer.toHexString(
        ContextCompat.getColor(
            polyline.context,
            R.color.cerulean_blue
        ) and 0x00ffffff
    )
        set(value) {
            field = value
            updateStyle()
        }

    internal var style =
        "{ style: 'lines', color: orange,width: ${lineWidth}px, order: $drawOrder, interactive: true, collide: false }"
        set(value) {
            field = value
            updateStyle()
        }

    init {
        updateStyle()
    }

    fun updateStyle() {
        polyline.mapBindings.forEach { it.key.setMarkerStylingFromString(it.value, style) }
    }
}
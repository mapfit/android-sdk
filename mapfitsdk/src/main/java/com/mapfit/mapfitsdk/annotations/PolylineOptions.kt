package com.mapfit.mapfitsdk.annotations

import android.support.v4.content.ContextCompat
import com.mapfit.mapfitsdk.R
import java.util.*

/**
 * Created by dogangulcan on 2/5/18.
 */
class PolylineOptions internal constructor(
    private var polyline: Polyline
) {

    val properties by lazy {
        val props = HashMap<String, String>()
//        props["type"] = "lines"
        props["type"] = "line"
        props["order"] = "1"
//        props["color"] = "white"
        props["width"] = "2px"
        props["color"] = "yellow"
        getStringMapAsArray(props)
    }

    private fun getStringMapAsArray(properties: Map<String, String>): Array<String?> {
        val out = arrayOfNulls<String>(properties.size * 2)
        var i = 0
        for ((key, value) in properties) {
            out[i++] = key
            out[i++] = value
        }
        return out
    }


    var lineWidth = 1
        set(value) {
            field = value
            updateStyle()
        }

    var drawOrder = 2000
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
        "{ style: 'lines', cap: round, color: orange,width: ${lineWidth}px, order: $drawOrder, interactive: true }"
        set(value) {
            field = value
            updateStyle()
        }

    init {
        updateStyle()
    }

    fun updateStyle() {
//        polyline.mapBindings.forEach { it.key.setMarkerStylingFromString(it.value, style) }
    }
}